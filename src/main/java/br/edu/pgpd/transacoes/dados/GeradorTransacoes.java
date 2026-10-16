package br.edu.pgpd.transacoes.dados;

import br.edu.pgpd.transacoes.modelo.Categoria;
import br.edu.pgpd.transacoes.modelo.Cidade;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Gera conjuntos sintéticos de transações financeiras.
 *
 * <p><b>Determinismo:</b> o conjunto é dividido em blocos de {@value #TAMANHO_BLOCO} transações e
 * cada bloco usa um gerador aleatório próprio, cuja semente depende apenas da semente global e do
 * número do bloco. Assim, o mesmo tamanho e a mesma semente produzem <i>exatamente</i> os mesmos
 * dados, independentemente de quantas threads foram usadas na geração.
 *
 * <p>A geração também é paralela (ExecutorService), mas <b>não</b> entra na medição de tempo
 * do benchmark — apenas as análises são medidas.
 *
 * @author Caio Polidoro
 */
public final class GeradorTransacoes {

    public static final long SEMENTE_PADRAO = 2026_2L;
    public static final int NUM_CLIENTES = 500_000;
    static final int TAMANHO_BLOCO = 1 << 16;

    /** Valor mínimo e máximo de uma transação: R$ 1,00 e R$ 1.000.000,00. */
    static final int VALOR_MINIMO_CENTAVOS = 100;
    static final int VALOR_MAXIMO_CENTAVOS = 100_000_000;

    // ---- Distribuição 1: categórica ponderada (participação de cada categoria, em %) ----
    private static final double[] PESO_CATEGORIA = new double[Categoria.QUANTIDADE];
    // ---- Distribuição 2: log-normal do valor, por categoria (mediana em R$ e dispersão) ----
    private static final double[] MU_VALOR = new double[Categoria.QUANTIDADE];
    private static final double[] SIGMA_VALOR = new double[Categoria.QUANTIDADE];

    static {
        configurar(Categoria.ALIMENTACAO, 30, 60, 0.8);
        configurar(Categoria.TRANSPORTE, 20, 35, 0.7);
        configurar(Categoria.ELETRONICOS, 8, 1_200, 1.0);
        configurar(Categoria.SAUDE, 10, 180, 0.9);
        configurar(Categoria.EDUCACAO, 6, 600, 0.6);
        configurar(Categoria.VESTUARIO, 10, 150, 0.7);
        configurar(Categoria.LAZER, 10, 120, 0.9);
        configurar(Categoria.MORADIA, 6, 1_500, 0.5);
    }

    private static void configurar(Categoria c, double pesoPercentual, double medianaReais, double sigma) {
        PESO_CATEGORIA[c.ordinal()] = pesoPercentual;
        MU_VALOR[c.ordinal()] = Math.log(medianaReais);
        SIGMA_VALOR[c.ordinal()] = sigma;
    }

    /** Distribuição 1 (categórica): PIX, Crédito, Débito, Boleto. */
    private static final double[] PESO_TIPO = {40, 30, 20, 10};

    /** Distribuição 3 (Pareto): fração de transações atípicas e parâmetros da cauda. */
    private static final double PROB_ATIPICA = 0.005;
    private static final double PARETO_MINIMO_REAIS = 3_000;
    private static final double PARETO_ALFA = 1.6;
    /** Parte das atípicas é arredondada para múltiplos de R$ 1.000 (padrão típico de fraude). */
    private static final double PROB_ATIPICA_REDONDA = 0.4;

    /** Distribuição 5 (mistura de normais): horário. */
    private static final double PROB_HORA_ALEATORIA = 0.04;
    private static final double PROB_PICO_ALMOCO = 0.5;

    private final long semente;
    private final double[] cdfCategoria = Distribuicoes.cdfDePesos(PESO_CATEGORIA);
    private final double[] cdfTipo = Distribuicoes.cdfDePesos(PESO_TIPO);
    private final double[] cdfCidade;
    /** Distribuição 4 (Zipf): tabela acumulada para os clientes. */
    private final double[] cdfCliente = Distribuicoes.cdfZipf(NUM_CLIENTES, 1.07);

    public GeradorTransacoes() {
        this(SEMENTE_PADRAO);
    }

    public GeradorTransacoes(long semente) {
        this.semente = semente;
        double[] pesoCidade = new double[Cidade.QUANTIDADE];
        for (Cidade c : Cidade.VALORES) {
            pesoCidade[c.ordinal()] = c.populacaoMil();
        }
        this.cdfCidade = Distribuicoes.cdfDePesos(pesoCidade);
    }

    /** Gera {@code n} transações usando todos os processadores disponíveis. */
    public ConjuntoTransacoes gerar(int n) {
        return gerar(n, Runtime.getRuntime().availableProcessors());
    }

    /** Gera {@code n} transações usando {@code threads} threads. */
    public ConjuntoTransacoes gerar(int n, int threads) {
        ConjuntoTransacoes conjunto = new ConjuntoTransacoes(n);
        int numBlocos = (n + TAMANHO_BLOCO - 1) / TAMANHO_BLOCO;

        try (ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, threads))) {
            List<Future<Void>> futuros = new ArrayList<>(numBlocos);
            for (int b = 0; b < numBlocos; b++) {
                final int bloco = b;
                Callable<Void> tarefa = () -> {
                    int inicio = bloco * TAMANHO_BLOCO;
                    int fim = Math.min(n, inicio + TAMANHO_BLOCO);
                    gerarBloco(conjunto, inicio, fim, new SplittableRandom(sementeDoBloco(bloco)));
                    return null;
                };
                futuros.add(executor.submit(tarefa));
            }
            for (Future<Void> f : futuros) {
                f.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Geração interrompida", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Falha na geração dos dados", e.getCause());
        }
        return conjunto;
    }

    private void gerarBloco(ConjuntoTransacoes conjunto, int inicio, int fim, SplittableRandom rng) {
        for (int i = inicio; i < fim; i++) {
            int categoria = Distribuicoes.porCdf(rng, cdfCategoria);
            int valor = sortearValorCentavos(rng, categoria);
            int cliente = Distribuicoes.porCdf(rng, cdfCliente) + 1;
            int tipo = Distribuicoes.porCdf(rng, cdfTipo);
            int cidade = Distribuicoes.porCdf(rng, cdfCidade);
            int dia = Distribuicoes.uniforme(rng, 0, ConjuntoTransacoes.DIAS_NO_ANO);
            int hora = sortearHora(rng);
            conjunto.definir(i, valor, cliente, categoria, tipo, cidade, dia, hora);
        }
    }

    /** Valor: log-normal da categoria ou, com probabilidade {@link #PROB_ATIPICA}, cauda de Pareto. */
    private static int sortearValorCentavos(SplittableRandom rng, int categoria) {
        double reais;
        if (rng.nextDouble() < PROB_ATIPICA) {
            reais = Distribuicoes.pareto(rng, PARETO_MINIMO_REAIS, PARETO_ALFA);
            if (rng.nextDouble() < PROB_ATIPICA_REDONDA) {
                reais = Math.max(1_000, Math.round(reais / 1_000) * 1_000.0);
            }
        } else {
            reais = Distribuicoes.logNormal(rng, MU_VALOR[categoria], SIGMA_VALOR[categoria]);
        }
        long centavos = Math.round(reais * 100);
        return (int) Math.clamp(centavos, VALOR_MINIMO_CENTAVOS, VALOR_MAXIMO_CENTAVOS);
    }

    /** Horário: mistura de duas normais (≈12h30 e ≈19h30) mais um ruído uniforme no dia todo. */
    private static int sortearHora(SplittableRandom rng) {
        if (rng.nextDouble() < PROB_HORA_ALEATORIA) {
            return Distribuicoes.uniforme(rng, 0, 24);
        }
        double h = rng.nextDouble() < PROB_PICO_ALMOCO
                ? Distribuicoes.normal(rng, 12.5, 2.0)
                : Distribuicoes.normal(rng, 19.5, 2.5);
        return Math.floorMod((int) Math.floor(h), 24);
    }

    /** Semente do bloco: mistura (SplitMix64) da semente global com o número do bloco. */
    private long sementeDoBloco(int bloco) {
        long z = semente + (bloco + 1L) * 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
