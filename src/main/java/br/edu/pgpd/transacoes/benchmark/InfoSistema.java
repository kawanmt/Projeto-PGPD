package br.edu.pgpd.transacoes.benchmark;

import br.edu.pgpd.transacoes.paralelo.Topologia;
import br.edu.pgpd.transacoes.util.Formatos;
import net.openhft.affinity.Affinity;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Descrição do ambiente de execução (hardware, sistema operacional e JVM) — necessária para
 * que os experimentos possam ser interpretados e reproduzidos.
 *
 * @author Vitor Abrahão
 */
public final class InfoSistema {

    private InfoSistema() {
    }

    public static String descrever(Topologia topologia) {
        Runtime rt = Runtime.getRuntime();
        String gcs = ManagementFactory.getGarbageCollectorMXBeans().stream()
                .map(GarbageCollectorMXBean::getName).collect(Collectors.joining(", "));
        String afinidade;
        try {
            afinidade = Affinity.getAffinityImpl().getClass().getSimpleName()
                    + " (máscara do processo: " + Affinity.getAffinity() + ")";
        } catch (Throwable e) {
            afinidade = "indisponível: " + e.getMessage();
        }

        return "Data/hora           : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n"
                + "Processador         : " + topologia.descricao().replace("\n", "\n                      ").stripTrailing() + "\n"
                + "Sistema operacional : " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + " (" + System.getProperty("os.arch") + ")\n"
                + "JVM                 : " + System.getProperty("java.vm.name") + " " + System.getProperty("java.version")
                + " (" + System.getProperty("java.vendor") + ")\n"
                + "Heap máximo (-Xmx)  : " + Formatos.bytes(rt.maxMemory()) + "\n"
                + "Coletor de lixo     : " + gcs + "\n"
                + "Biblioteca afinidade: OpenHFT " + afinidade + "\n"
                + "Argumentos da JVM   : " + String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments()) + "\n";
    }
}
