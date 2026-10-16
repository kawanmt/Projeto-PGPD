package br.edu.pgpd.transacoes.agregacao;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;

/**
 * <b>Versão sequencial</b> da agregação financeira: soma, média, maior e menor transação,
 * desvio padrão e histograma por faixa de valor (Análises 1, 2 e 5).
 *
 * <p>O método {@link #processar} é o algoritmo propriamente dito sobre uma faixa
 * {@code [inicio, fim)}. A versão sequencial o aplica ao conjunto inteiro; a versão paralela
 * ({@link AgregacaoTarefa}) aplica <i>o mesmo algoritmo</i> a cada parte — paralelismo de dados.
 *
 * @author Kawan Mesquita
 */
public final class AgregacaoSequencial {

    private AgregacaoSequencial() {
    }

    /** Agrega todo o conjunto em uma única thread. */
    public static ResultadoAgregacao calcular(ConjuntoTransacoes conjunto) {
        return processar(conjunto, 0, conjunto.tamanho());
    }

    /** Algoritmo de agregação sobre as transações {@code [inicio, fim)}. */
    public static ResultadoAgregacao processar(ConjuntoTransacoes conjunto, int inicio, int fim) {
        final int[] valores = conjunto.valores();
        ResultadoAgregacao r = new ResultadoAgregacao();
        final long[] histograma = r.histograma; // array próprio da tarefa: sem compartilhamento

        // Acumuladores em variáveis locais (registradores) — mais rápido que atualizar campos.
        long soma = 0;
        int maior = Integer.MIN_VALUE;
        int indiceMaior = -1;
        int menor = Integer.MAX_VALUE;
        int indiceMenor = -1;
        double media = 0;
        double m2 = 0;
        long n = 0;

        for (int i = inicio; i < fim; i++) {
            int v = valores[i];
            soma += v;
            if (v > maior) {
                maior = v;
                indiceMaior = i;
            }
            if (v < menor) {
                menor = v;
                indiceMenor = i;
            }
            // Welford: média e variância em uma única passada, numericamente estável
            n++;
            double x = v / 100.0;
            double delta = x - media;
            media += delta / n;
            m2 += delta * (x - media);

            histograma[ResultadoAgregacao.faixa(v)]++;
        }

        r.quantidade = n;
        r.somaCentavos = soma;
        r.maiorValor = maior;
        r.idMaior = indiceMaior < 0 ? -1 : ConjuntoTransacoes.idDoIndice(indiceMaior);
        r.menorValor = menor;
        r.idMenor = indiceMenor < 0 ? -1 : ConjuntoTransacoes.idDoIndice(indiceMenor);
        r.mediaWelford = media;
        r.m2 = m2;
        return r;
    }
}
