package br.edu.pgpd.transacoes.agregacao;

import br.edu.pgpd.transacoes.util.Formatos;

import java.util.Arrays;

/**
 * Resultado (parcial ou final) da agregação financeira.
 *
 * <p>Cada tarefa produz um {@code ResultadoAgregacao} para a sua faixa de dados; o resultado final
 * é obtido pela <b>redução</b> {@link #combinar(ResultadoAgregacao)} de todos os parciais:
 * <ul>
 *   <li>quantidade, soma e histograma: somam-se (operações associativas e exatas, pois os
 *       valores são inteiros em centavos);</li>
 *   <li>maior/menor: fica o maior/menor dos parciais (empate: menor ID, para ser determinístico);</li>
 *   <li>média e variância: combinação de Chan et al. para variâncias parciais — não basta somar
 *       as variâncias, é preciso corrigir pela diferença entre as médias de cada parte.</li>
 * </ul>
 *
 * @author Kawan Mesquita
 */
public final class ResultadoAgregacao {

    /** Limites superiores (exclusivos) das faixas de valor do histograma, em centavos. */
    static final int[] LIMITES_FAIXAS = {5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000};
    public static final String[] ROTULOS_FAIXAS = {
            "até R$ 50", "R$ 50 a R$ 100", "R$ 100 a R$ 500", "R$ 500 a R$ 1.000",
            "R$ 1.000 a R$ 5.000", "R$ 5.000 a R$ 10.000", "acima de R$ 10.000"};

    long quantidade;
    long somaCentavos;
    int maiorValor = Integer.MIN_VALUE;
    long idMaior = -1;
    int menorValor = Integer.MAX_VALUE;
    long idMenor = -1;
    /** Média e soma dos quadrados dos desvios (algoritmo de Welford), em reais. */
    double mediaWelford;
    double m2;
    final long[] histograma = new long[LIMITES_FAIXAS.length + 1];

    /** Índice da faixa do histograma para um valor em centavos. */
    static int faixa(int valorCentavos) {
        int f = 0;
        while (f < LIMITES_FAIXAS.length && valorCentavos >= LIMITES_FAIXAS[f]) {
            f++;
        }
        return f;
    }

    /** Redução: incorpora o resultado parcial {@code outro} neste resultado. */
    public ResultadoAgregacao combinar(ResultadoAgregacao outro) {
        if (outro.quantidade == 0) {
            return this;
        }
        if (quantidade == 0) {
            mediaWelford = outro.mediaWelford;
            m2 = outro.m2;
        } else {
            // Combinação de Chan: M2 = M2a + M2b + delta² · na · nb / n
            double na = quantidade;
            double nb = outro.quantidade;
            double n = na + nb;
            double delta = outro.mediaWelford - mediaWelford;
            mediaWelford += delta * nb / n;
            m2 += outro.m2 + delta * delta * na * nb / n;
        }
        quantidade += outro.quantidade;
        somaCentavos += outro.somaCentavos;

        if (outro.maiorValor > maiorValor || (outro.maiorValor == maiorValor && outro.idMaior < idMaior)) {
            maiorValor = outro.maiorValor;
            idMaior = outro.idMaior;
        }
        if (outro.menorValor < menorValor || (outro.menorValor == menorValor && outro.idMenor < idMenor)) {
            menorValor = outro.menorValor;
            idMenor = outro.idMenor;
        }
        for (int f = 0; f < histograma.length; f++) {
            histograma[f] += outro.histograma[f];
        }
        return this;
    }

    // ---- Consultas ----

    public long quantidade() {
        return quantidade;
    }

    public long somaCentavos() {
        return somaCentavos;
    }

    /** Média exata em reais, calculada a partir da soma inteira. */
    public double mediaReais() {
        return quantidade == 0 ? 0 : somaCentavos / 100.0 / quantidade;
    }

    /** Desvio padrão populacional, em reais. */
    public double desvioPadraoReais() {
        return quantidade == 0 ? 0 : Math.sqrt(m2 / quantidade);
    }

    public int maiorValorCentavos() {
        return maiorValor;
    }

    public long idMaior() {
        return idMaior;
    }

    public int menorValorCentavos() {
        return menorValor;
    }

    public long idMenor() {
        return idMenor;
    }

    public long[] histograma() {
        return histograma.clone();
    }

    /**
     * Igualdade usada para validar o paralelo contra o sequencial: campos inteiros devem ser
     * idênticos; o desvio padrão (ponto flutuante, cuja soma não é associativa) admite erro
     * relativo de 1e-9.
     */
    public boolean equivalente(ResultadoAgregacao o) {
        return quantidade == o.quantidade
                && somaCentavos == o.somaCentavos
                && maiorValor == o.maiorValor && idMaior == o.idMaior
                && menorValor == o.menorValor && idMenor == o.idMenor
                && Arrays.equals(histograma, o.histograma)
                && quaseIgual(desvioPadraoReais(), o.desvioPadraoReais());
    }

    private static boolean quaseIgual(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
    }

    public String formatar() {
        StringBuilder sb = new StringBuilder();
        sb.append("  Quantidade de transações : ").append(Formatos.inteiro(quantidade)).append('\n');
        sb.append("  Soma dos valores         : ").append(Formatos.reais(somaCentavos)).append('\n');
        sb.append("  Valor médio              : ").append(Formatos.reais(mediaReais())).append('\n');
        sb.append("  Desvio padrão            : ").append(Formatos.reais(desvioPadraoReais())).append('\n');
        if (quantidade > 0) {
            sb.append("  Maior transação          : ").append(Formatos.reais(maiorValor))
                    .append("  (ID ").append(idMaior).append(")\n");
            sb.append("  Menor transação          : ").append(Formatos.reais(menorValor))
                    .append("  (ID ").append(idMenor).append(")\n");
        }
        sb.append("  Distribuição por faixa de valor:\n");
        for (int f = 0; f < histograma.length; f++) {
            double pct = quantidade == 0 ? 0 : (double) histograma[f] / quantidade;
            sb.append(String.format("    %-22s %15s  (%s)%n", ROTULOS_FAIXAS[f],
                    Formatos.inteiro(histograma[f]), Formatos.percentual(pct)));
        }
        return sb.toString();
    }
}
