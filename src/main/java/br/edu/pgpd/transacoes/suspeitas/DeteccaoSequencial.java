package br.edu.pgpd.transacoes.suspeitas;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;

/**
 * <b>Versão sequencial</b> da detecção de transações suspeitas (Análise 3).
 *
 * <p>{@link #processar} é o algoritmo sobre uma faixa {@code [inicio, fim)}; a versão paralela
 * ({@link DeteccaoTarefa}) aplica o mesmo algoritmo a cada parte dos dados.
 *
 * @author Caio Polidoro
 */
public final class DeteccaoSequencial {

    private DeteccaoSequencial() {
    }

    public static ResultadoDeteccao calcular(ConjuntoTransacoes conjunto) {
        return processar(conjunto, 0, conjunto.tamanho());
    }

    public static ResultadoDeteccao processar(ConjuntoTransacoes conjunto, int inicio, int fim) {
        final int[] valores = conjunto.valores();
        final byte[] categorias = conjunto.categorias();
        final byte[] tipos = conjunto.tipos();
        final byte[] horas = conjunto.horas();

        ResultadoDeteccao r = new ResultadoDeteccao();
        final long[] porRegra = r.porRegra;
        final long[] porScore = r.porScore;
        final long[] porCategoria = r.porCategoria;
        long suspeitas = 0;
        long valorSuspeito = 0;

        for (int i = inicio; i < fim; i++) {
            int v = valores[i];
            int categoria = categorias[i];
            int mascara = RegraSuspeita.avaliar(v, categoria, tipos[i], horas[i]);
            porScore[Integer.bitCount(mascara)]++;

            if (mascara != 0) {
                suspeitas++;
                valorSuspeito += v;
                porCategoria[categoria]++;
                for (int b = 0; b < porRegra.length; b++) {
                    if ((mascara & (1 << b)) != 0) {
                        porRegra[b]++;
                    }
                }
                r.maiores.oferecer(ConjuntoTransacoes.idDoIndice(i), v, mascara);
            }
        }

        r.analisadas = fim - inicio;
        r.totalSuspeitas = suspeitas;
        r.valorSuspeitoCentavos = valorSuspeito;
        return r;
    }
}
