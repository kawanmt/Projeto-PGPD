package br.edu.pgpd.transacoes.dados;

import java.util.SplittableRandom;

/**
 * Amostragem das distribuições estatísticas usadas na geração sintética.
 * Todas recebem o gerador aleatório como parâmetro, para que cada bloco de dados use
 * o seu próprio gerador (sem compartilhamento entre threads).
 *
 * <ol>
 *   <li><b>Uniforme</b> — dia do ano;</li>
 *   <li><b>Normal</b> (Box–Muller) — base da log-normal e da mistura de horários;</li>
 *   <li><b>Log-normal</b> — valor das transações comuns (assimétrica à direita);</li>
 *   <li><b>Pareto</b> — cauda pesada: transações atípicas de valor muito alto;</li>
 *   <li><b>Zipf</b> — clientes (poucos clientes concentram muitas transações);</li>
 *   <li><b>Categórica ponderada</b> — categoria, meio de pagamento e cidade;</li>
 *   <li><b>Mistura de normais</b> — horário (picos no almoço e no início da noite).</li>
 * </ol>
 *
 * @author Caio Polidoro
 */
public final class Distribuicoes {

    private Distribuicoes() {
    }

    /** Uniforme discreta em {@code [min, max)}. */
    public static int uniforme(SplittableRandom rng, int min, int max) {
        return rng.nextInt(min, max);
    }

    /** Normal padrão N(0,1) pelo método de Box–Muller. */
    public static double normalPadrao(SplittableRandom rng) {
        double u1 = 1.0 - rng.nextDouble(); // (0, 1] — evita log(0)
        double u2 = rng.nextDouble();
        return Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
    }

    /** Normal N(media, desvio²). */
    public static double normal(SplittableRandom rng, double media, double desvio) {
        return media + desvio * normalPadrao(rng);
    }

    /** Log-normal: exp(N(mu, sigma²)). A mediana é e^mu. */
    public static double logNormal(SplittableRandom rng, double mu, double sigma) {
        return Math.exp(normal(rng, mu, sigma));
    }

    /** Pareto com mínimo {@code xm} e índice de cauda {@code alfa} (inversão da CDF). */
    public static double pareto(SplittableRandom rng, double xm, double alfa) {
        double u = 1.0 - rng.nextDouble(); // (0, 1]
        return xm / Math.pow(u, 1.0 / alfa);
    }

    /**
     * Sorteio por tabela de distribuição acumulada (CDF): busca binária pelo primeiro
     * índice cuja probabilidade acumulada é maior que u. Serve para a categórica e para a Zipf.
     */
    public static int porCdf(SplittableRandom rng, double[] cdf) {
        double u = rng.nextDouble() * cdf[cdf.length - 1];
        int baixo = 0;
        int alto = cdf.length - 1;
        while (baixo < alto) {
            int meio = (baixo + alto) >>> 1;
            if (cdf[meio] > u) {
                alto = meio;
            } else {
                baixo = meio + 1;
            }
        }
        return baixo;
    }

    /** Monta a CDF a partir de pesos arbitrários (não precisam somar 1). */
    public static double[] cdfDePesos(double[] pesos) {
        double[] cdf = new double[pesos.length];
        double acumulado = 0;
        for (int i = 0; i < pesos.length; i++) {
            acumulado += pesos[i];
            cdf[i] = acumulado;
        }
        return cdf;
    }

    /** CDF da distribuição de Zipf com {@code n} elementos e expoente {@code s}: P(k) ∝ 1/k^s. */
    public static double[] cdfZipf(int n, double s) {
        double[] pesos = new double[n];
        for (int k = 1; k <= n; k++) {
            pesos[k - 1] = 1.0 / Math.pow(k, s);
        }
        return cdfDePesos(pesos);
    }
}
