package br.edu.pgpd.transacoes.benchmark;

import java.util.Arrays;

/**
 * Uma medição do benchmark: uma configuração executada várias vezes sobre um conjunto.
 *
 * <p><b>Speedup</b> = T<sub>sequencial</sub> / T<sub>paralelo</sub>;
 * <b>Eficiência</b> = Speedup / número de threads. Usa-se a <b>mediana</b> das repetições, que é
 * menos sensível a interferências pontuais (GC, outros processos) do que a média.
 *
 * @param tamanho         quantidade de transações
 * @param analise         análise medida
 * @param configuracao    sequencial, executor ou executor + afinidade
 * @param threads         threads do pool (1 para o sequencial)
 * @param tarefas         quantidade de tarefas (partes) em que os dados foram divididos
 * @param cpus            CPUs usadas ("SO" quando o sistema operacional escolhe)
 * @param temposMs        tempo de cada repetição, em milissegundos
 * @param sequencialMs    mediana do sequencial correspondente (referência do speedup)
 * @param valido          resultado idêntico ao da versão sequencial
 * @author Vitor Abrahão
 */
public record Medicao(long tamanho, TipoAnalise analise, Configuracao configuracao, int threads, int tarefas,
                      String cpus, double[] temposMs, double sequencialMs, boolean valido) {

    public double mediana() {
        double[] t = temposMs.clone();
        Arrays.sort(t);
        int n = t.length;
        return n % 2 == 1 ? t[n / 2] : (t[n / 2 - 1] + t[n / 2]) / 2.0;
    }

    public double media() {
        return Arrays.stream(temposMs).average().orElse(0);
    }

    public double desvio() {
        double m = media();
        return Math.sqrt(Arrays.stream(temposMs).map(t -> (t - m) * (t - m)).average().orElse(0));
    }

    public double minimo() {
        return Arrays.stream(temposMs).min().orElse(0);
    }

    public double speedup() {
        double t = mediana();
        return t == 0 ? 0 : sequencialMs / t;
    }

    public double eficiencia() {
        return speedup() / threads;
    }
}
