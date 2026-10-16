package br.edu.pgpd.transacoes.benchmark;

import br.edu.pgpd.transacoes.paralelo.EstrategiaAfinidade;

/**
 * Configurações comparadas no benchmark.
 *
 * @author Vitor Abrahão
 */
public enum Configuracao {
    SEQUENCIAL("Sequencial", null),
    EXECUTOR("Executor", EstrategiaAfinidade.NENHUMA),
    AFINIDADE_COMPACTA("Executor + Affinity (compacta)", EstrategiaAfinidade.COMPACTA),
    AFINIDADE_ESPALHADA("Executor + Affinity (espalhada)", EstrategiaAfinidade.ESPALHADA);

    private final String rotulo;
    private final EstrategiaAfinidade estrategia;

    Configuracao(String rotulo, EstrategiaAfinidade estrategia) {
        this.rotulo = rotulo;
        this.estrategia = estrategia;
    }

    public String rotulo() {
        return rotulo;
    }

    public EstrategiaAfinidade estrategia() {
        return estrategia;
    }

    public static Configuracao de(EstrategiaAfinidade estrategia) {
        for (Configuracao c : values()) {
            if (c.estrategia == estrategia) {
                return c;
            }
        }
        throw new IllegalArgumentException(String.valueOf(estrategia));
    }
}
