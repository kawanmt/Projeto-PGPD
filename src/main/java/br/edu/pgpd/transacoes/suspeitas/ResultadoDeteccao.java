package br.edu.pgpd.transacoes.suspeitas;

import br.edu.pgpd.transacoes.modelo.Categoria;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.modelo.Transacao;
import br.edu.pgpd.transacoes.util.Formatos;

import java.util.Arrays;
import java.util.List;

/**
 * Resultado (parcial ou final) da detecção de transações suspeitas.
 *
 * <p>Não guarda a lista completa de IDs suspeitos (com 500 milhões de transações seriam milhões
 * de IDs); guarda contadores e o top-N das maiores. Todos os contadores são somados na redução.
 *
 * @author Caio Polidoro
 */
public final class ResultadoDeteccao {

    public static final int TOP_N = 10;

    long analisadas;
    long totalSuspeitas;
    long valorSuspeitoCentavos;
    /** Quantas transações atingiram cada regra (uma transação pode atingir várias). */
    final long[] porRegra = new long[RegraSuspeita.QUANTIDADE];
    /** porScore[k] = transações que atingiram exatamente k regras (k = 0 → normais). */
    final long[] porScore = new long[RegraSuspeita.QUANTIDADE + 1];
    /** Suspeitas por categoria. */
    final long[] porCategoria = new long[Categoria.QUANTIDADE];
    final MaioresSuspeitas maiores = new MaioresSuspeitas(TOP_N);

    /** Redução: incorpora o parcial {@code outro}. */
    public ResultadoDeteccao combinar(ResultadoDeteccao outro) {
        analisadas += outro.analisadas;
        totalSuspeitas += outro.totalSuspeitas;
        valorSuspeitoCentavos += outro.valorSuspeitoCentavos;
        somar(porRegra, outro.porRegra);
        somar(porScore, outro.porScore);
        somar(porCategoria, outro.porCategoria);
        maiores.combinar(outro.maiores);
        return this;
    }

    private static void somar(long[] destino, long[] origem) {
        for (int i = 0; i < destino.length; i++) {
            destino[i] += origem[i];
        }
    }

    public long analisadas() {
        return analisadas;
    }

    public long totalSuspeitas() {
        return totalSuspeitas;
    }

    public long valorSuspeitoCentavos() {
        return valorSuspeitoCentavos;
    }

    public long[] porRegra() {
        return porRegra.clone();
    }

    public long[] porScore() {
        return porScore.clone();
    }

    public List<MaioresSuspeitas.Suspeita> maioresSuspeitas() {
        return maiores.emOrdem();
    }

    public boolean equivalente(ResultadoDeteccao o) {
        return analisadas == o.analisadas
                && totalSuspeitas == o.totalSuspeitas
                && valorSuspeitoCentavos == o.valorSuspeitoCentavos
                && Arrays.equals(porRegra, o.porRegra)
                && Arrays.equals(porScore, o.porScore)
                && Arrays.equals(porCategoria, o.porCategoria)
                && maioresSuspeitas().equals(o.maioresSuspeitas());
    }

    public String formatar(ConjuntoTransacoes conjunto) {
        StringBuilder sb = new StringBuilder();
        double pct = analisadas == 0 ? 0 : (double) totalSuspeitas / analisadas;
        sb.append("  Transações analisadas : ").append(Formatos.inteiro(analisadas)).append('\n');
        sb.append("  Transações suspeitas  : ").append(Formatos.inteiro(totalSuspeitas))
                .append(" (").append(Formatos.percentual(pct)).append(")\n");
        sb.append("  Valor total suspeito  : ").append(Formatos.reais(valorSuspeitoCentavos)).append('\n');

        sb.append("  Por regra:\n");
        for (RegraSuspeita r : RegraSuspeita.VALORES) {
            sb.append(String.format("    %-3s %-58s %12s%n", r.name().substring(0, 2), r.descricao(),
                    Formatos.inteiro(porRegra[r.ordinal()])));
        }
        sb.append("  Por score de risco (nº de regras atingidas):\n");
        for (int k = 1; k < porScore.length; k++) {
            if (porScore[k] > 0) {
                sb.append(String.format("    score %d: %12s%n", k, Formatos.inteiro(porScore[k])));
            }
        }
        sb.append("  Por categoria:\n");
        for (Categoria c : Categoria.VALORES) {
            sb.append(String.format("    %-12s %12s%n", c.nome(), Formatos.inteiro(porCategoria[c.ordinal()])));
        }
        sb.append("  ").append(TOP_N).append(" maiores transações suspeitas:\n");
        for (MaioresSuspeitas.Suspeita s : maioresSuspeitas()) {
            Transacao t = conjunto.transacao(ConjuntoTransacoes.indiceDoId(s.id()));
            sb.append(String.format("    ID %-11d %18s  %-12s %-8s %02dh  %-16s regras: %s%n",
                    s.id(), Formatos.reais(s.valorCentavos()), t.categoria().nome(), t.tipo().nome(),
                    t.hora(), t.cidade().nome() + "/" + t.estado().sigla(), regras(s.mascara())));
        }
        return sb.toString();
    }

    static String regras(int mascara) {
        StringBuilder sb = new StringBuilder();
        for (RegraSuspeita r : RegraSuspeita.VALORES) {
            if ((mascara & r.bit()) != 0) {
                if (!sb.isEmpty()) {
                    sb.append(',');
                }
                sb.append(r.name(), 0, 2);
            }
        }
        return sb.toString();
    }
}
