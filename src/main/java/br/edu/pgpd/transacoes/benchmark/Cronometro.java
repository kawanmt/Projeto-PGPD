package br.edu.pgpd.transacoes.benchmark;

/**
 * Mede o tempo de uma ação com {@link System#nanoTime()}, descartando as execuções de
 * aquecimento (durante as quais o JIT ainda está compilando o código).
 *
 * @author Vitor Abrahão
 */
public final class Cronometro {

    /** Ação medida; devolve o resultado para que o JIT não elimine o cálculo. */
    @FunctionalInterface
    public interface Acao {
        Object executar() throws Exception;
    }

    /** Tempos das repetições e o resultado da última execução. */
    public record Resultado(double[] temposMs, Object ultimoResultado) {
    }

    private Cronometro() {
    }

    public static Resultado medir(Acao acao, int aquecimento, int repeticoes) throws Exception {
        Object resultado = null;
        for (int i = 0; i < aquecimento; i++) {
            resultado = acao.executar();
        }
        double[] tempos = new double[repeticoes];
        for (int i = 0; i < repeticoes; i++) {
            long inicio = System.nanoTime();
            resultado = acao.executar();
            tempos[i] = (System.nanoTime() - inicio) / 1_000_000.0;
        }
        return new Resultado(tempos, resultado);
    }
}
