package br.edu.pgpd.transacoes.paralelo;

/**
 * Como as threads do pool são associadas às CPUs.
 *
 * @author Vitor Abrahão
 */
public enum EstrategiaAfinidade {

    /** Sem afinidade: o escalonador do sistema operacional decide (e pode migrar as threads). */
    NENHUMA("Executor"),

    /** Thread i → CPU i (como no enunciado). Com SMT, threads vizinhas dividem o mesmo núcleo físico. */
    COMPACTA("Executor + Affinity (compacta)"),

    /** Uma thread por núcleo físico primeiro; os irmãos SMT só são usados depois. */
    ESPALHADA("Executor + Affinity (espalhada)");

    private final String rotulo;

    EstrategiaAfinidade(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    /** CPUs atribuídas às {@code threads} threads do pool (vazio para {@link #NENHUMA}). */
    public int[] cpus(int threads, Topologia topologia) {
        if (this == NENHUMA) {
            return new int[0];
        }
        int[] ordem = this == COMPACTA ? topologia.ordemCompacta() : topologia.ordemEspalhada();
        int[] cpus = new int[threads];
        for (int i = 0; i < threads; i++) {
            cpus[i] = ordem[i % ordem.length]; // mais threads que CPUs: volta ao início
        }
        return cpus;
    }
}
