package br.edu.pgpd.transacoes.categorias;

import br.edu.pgpd.transacoes.modelo.Categoria;
import br.edu.pgpd.transacoes.modelo.Estado;
import br.edu.pgpd.transacoes.modelo.TipoTransacao;
import br.edu.pgpd.transacoes.util.Formatos;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Resultado (parcial ou final) das estatísticas agrupadas.
 *
 * <p>Cada agrupamento é um conjunto de arrays indexados pelo código do grupo (categoria, estado,
 * mês, tipo). Cada tarefa tem os seus próprios arrays — não há escrita compartilhada (nem
 * <i>false sharing</i>) — e a redução apenas soma os arrays posição a posição.
 *
 * @author Henrique Mosson
 */
public final class ResultadoCategorias {

    private static final Locale PT_BR = Locale.of("pt", "BR");

    // Por categoria
    final long[] quantidade = new long[Categoria.QUANTIDADE];
    final long[] totalCentavos = new long[Categoria.QUANTIDADE];
    final int[] maior = new int[Categoria.QUANTIDADE];
    final int[] menor = new int[Categoria.QUANTIDADE];
    // Por estado, mês e tipo de pagamento
    final long[] quantidadeEstado = new long[Estado.QUANTIDADE];
    final long[] totalEstado = new long[Estado.QUANTIDADE];
    final long[] quantidadeMes = new long[12];
    final long[] totalMes = new long[12];
    final long[] quantidadeTipo = new long[TipoTransacao.QUANTIDADE];
    final long[] totalTipo = new long[TipoTransacao.QUANTIDADE];

    public ResultadoCategorias() {
        Arrays.fill(maior, Integer.MIN_VALUE);
        Arrays.fill(menor, Integer.MAX_VALUE);
    }

    /** Redução: soma os contadores e totais; mantém maior/menor por categoria. */
    public ResultadoCategorias combinar(ResultadoCategorias outro) {
        for (int c = 0; c < quantidade.length; c++) {
            quantidade[c] += outro.quantidade[c];
            totalCentavos[c] += outro.totalCentavos[c];
            maior[c] = Math.max(maior[c], outro.maior[c]);
            menor[c] = Math.min(menor[c], outro.menor[c]);
        }
        somar(quantidadeEstado, outro.quantidadeEstado);
        somar(totalEstado, outro.totalEstado);
        somar(quantidadeMes, outro.quantidadeMes);
        somar(totalMes, outro.totalMes);
        somar(quantidadeTipo, outro.quantidadeTipo);
        somar(totalTipo, outro.totalTipo);
        return this;
    }

    private static void somar(long[] destino, long[] origem) {
        for (int i = 0; i < destino.length; i++) {
            destino[i] += origem[i];
        }
    }

    // ---- Consultas ----

    public long quantidade(Categoria c) {
        return quantidade[c.ordinal()];
    }

    public long totalCentavos(Categoria c) {
        return totalCentavos[c.ordinal()];
    }

    public double mediaReais(Categoria c) {
        long q = quantidade(c);
        return q == 0 ? 0 : totalCentavos(c) / 100.0 / q;
    }

    public int maiorCentavos(Categoria c) {
        return maior[c.ordinal()];
    }

    public int menorCentavos(Categoria c) {
        return menor[c.ordinal()];
    }

    public long totalGeralCentavos() {
        return Arrays.stream(totalCentavos).sum();
    }

    public long quantidadeGeral() {
        return Arrays.stream(quantidade).sum();
    }

    public boolean equivalente(ResultadoCategorias o) {
        return Arrays.equals(quantidade, o.quantidade)
                && Arrays.equals(totalCentavos, o.totalCentavos)
                && Arrays.equals(maior, o.maior)
                && Arrays.equals(menor, o.menor)
                && Arrays.equals(quantidadeEstado, o.quantidadeEstado)
                && Arrays.equals(totalEstado, o.totalEstado)
                && Arrays.equals(quantidadeMes, o.quantidadeMes)
                && Arrays.equals(totalMes, o.totalMes)
                && Arrays.equals(quantidadeTipo, o.quantidadeTipo)
                && Arrays.equals(totalTipo, o.totalTipo);
    }

    public String formatar() {
        long totalGeral = totalGeralCentavos();
        long qtdGeral = quantidadeGeral();
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("  %-12s %13s %22s %14s %16s %10s %8s%n",
                "Categoria", "Quantidade", "Valor total", "Valor médio", "Maior transação", "Menor", "% valor"));
        sb.append("  ").append("-".repeat(102)).append('\n');
        for (Categoria c : Categoria.VALORES) {
            int i = c.ordinal();
            boolean vazia = quantidade[i] == 0;
            sb.append(String.format("  %-12s %13s %22s %14s %16s %10s %8s%n",
                    c.nome(), Formatos.inteiro(quantidade[i]), Formatos.reais(totalCentavos[i]),
                    Formatos.reais(mediaReais(c)),
                    vazia ? "-" : Formatos.reais(maior[i]), vazia ? "-" : Formatos.reais(menor[i]),
                    Formatos.percentual(totalGeral == 0 ? 0 : (double) totalCentavos[i] / totalGeral)));
        }

        sb.append("\n  Por meio de pagamento:\n");
        for (TipoTransacao t : TipoTransacao.VALORES) {
            int i = t.ordinal();
            sb.append(String.format("    %-8s %13s %22s  (%s das transações)%n", t.nome(),
                    Formatos.inteiro(quantidadeTipo[i]), Formatos.reais(totalTipo[i]),
                    Formatos.percentual(qtdGeral == 0 ? 0 : (double) quantidadeTipo[i] / qtdGeral)));
        }

        sb.append("\n  Por mês:\n");
        for (int m = 0; m < 12; m++) {
            String nome = Month.of(m + 1).getDisplayName(TextStyle.SHORT, PT_BR);
            sb.append(String.format("    %-5s %13s %22s%n", nome,
                    Formatos.inteiro(quantidadeMes[m]), Formatos.reais(totalMes[m])));
        }

        sb.append("\n  Estados com maior valor movimentado (top 10):\n");
        IntStream.range(0, Estado.QUANTIDADE).boxed()
                .filter(e -> quantidadeEstado[e] > 0)
                .sorted(Comparator.comparingLong((Integer e) -> totalEstado[e]).reversed())
                .limit(10)
                .forEach(e -> sb.append(String.format("    %-3s %-20s %13s %22s%n",
                        Estado.VALORES[e].sigla(), Estado.VALORES[e].nome(),
                        Formatos.inteiro(quantidadeEstado[e]), Formatos.reais(totalEstado[e]))));
        return sb.toString();
    }
}
