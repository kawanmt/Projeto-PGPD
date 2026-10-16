package br.edu.pgpd.transacoes.paralelo;

import com.sun.jna.Library;
import com.sun.jna.Native;
import net.openhft.affinity.Affinity;

/**
 * Descobre em qual CPU lógica a thread atual está executando.
 *
 * <p>No Linux a OpenHFT Affinity já oferece {@link Affinity#getCpu()} (sched_getcpu). No Windows
 * a biblioteca devolve -1, então chamamos diretamente a API {@code GetCurrentProcessorNumber}
 * do kernel32 via JNA.
 *
 * @author Vitor Abrahão
 */
public final class CpuAtual {

    private static final boolean WINDOWS = System.getProperty("os.name", "").toLowerCase().contains("win");

    private interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        int GetCurrentProcessorNumber();
    }

    private CpuAtual() {
    }

    /** CPU lógica atual, ou -1 se não for possível descobrir. */
    public static int obter() {
        try {
            return WINDOWS ? Kernel32.INSTANCE.GetCurrentProcessorNumber() : Affinity.getCpu();
        } catch (Throwable e) {
            return -1;
        }
    }
}
