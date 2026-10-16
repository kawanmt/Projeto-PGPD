package br.edu.pgpd.transacoes.paralelo;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Topologia do processador: quais CPUs lógicas pertencem a cada núcleo físico.
 *
 * <p>Isso importa para a afinidade: em processadores com SMT (Hyper-Threading / AMD SMT) duas
 * CPUs lógicas compartilham o mesmo núcleo físico (unidades de execução e caches L1/L2).
 * No Windows os "irmãos" SMT são numerados de forma adjacente (CPU 0 e 1 = núcleo 0);
 * no Linux costumam ser {@code i} e {@code i + núcleos}.
 *
 * @author Vitor Abrahão
 */
public final class Topologia {

    private static final String OS = System.getProperty("os.name", "").toLowerCase();

    private final int cpusLogicas;
    /** Cada elemento é a lista de CPUs lógicas de um núcleo físico. */
    private final List<int[]> nucleos;
    private final String nomeProcessador;

    private Topologia(int cpusLogicas, List<int[]> nucleos, String nomeProcessador) {
        this.cpusLogicas = cpusLogicas;
        this.nucleos = List.copyOf(nucleos);
        this.nomeProcessador = nomeProcessador;
    }

    public static Topologia detectar() {
        int cpus = Runtime.getRuntime().availableProcessors();
        List<int[]> nucleos = null;
        try {
            if (OS.contains("win")) {
                nucleos = nucleosWindows();
            } else if (OS.contains("linux")) {
                nucleos = nucleosLinux(cpus);
            }
        } catch (Throwable e) {
            nucleos = null;
        }
        if (nucleos == null || nucleos.isEmpty()) {
            nucleos = new ArrayList<>();
            for (int c = 0; c < cpus; c++) {
                nucleos.add(new int[]{c});
            }
        }
        return new Topologia(cpus, nucleos, detectarNome());
    }

    private static List<int[]> nucleosWindows() {
        List<int[]> nucleos = new ArrayList<>();
        for (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION info : Kernel32Util.getLogicalProcessorInformation()) {
            if (info.relationship == WinNT.LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorCore) {
                long mascara = info.processorMask.longValue();
                nucleos.add(bitsDaMascara(mascara));
            }
        }
        nucleos.sort((a, b) -> Integer.compare(a[0], b[0]));
        return nucleos;
    }

    private static List<int[]> nucleosLinux(int cpus) throws IOException {
        Map<String, List<Integer>> porNucleo = new TreeMap<>();
        for (int c = 0; c < cpus; c++) {
            Path base = Path.of("/sys/devices/system/cpu/cpu" + c + "/topology");
            String pacote = Files.readString(base.resolve("physical_package_id")).trim();
            String nucleo = Files.readString(base.resolve("core_id")).trim();
            porNucleo.computeIfAbsent(pacote + ":" + nucleo, k -> new ArrayList<>()).add(c);
        }
        List<int[]> nucleos = new ArrayList<>();
        for (List<Integer> lista : porNucleo.values()) {
            nucleos.add(lista.stream().mapToInt(Integer::intValue).toArray());
        }
        nucleos.sort((a, b) -> Integer.compare(a[0], b[0]));
        return nucleos;
    }

    private static int[] bitsDaMascara(long mascara) {
        return java.util.stream.IntStream.range(0, 64).filter(b -> (mascara & (1L << b)) != 0).toArray();
    }

    private static String detectarNome() {
        try {
            if (OS.contains("win")) {
                return Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
                        "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0", "ProcessorNameString").trim();
            }
            if (OS.contains("linux")) {
                for (String linha : Files.readAllLines(Path.of("/proc/cpuinfo"))) {
                    if (linha.startsWith("model name")) {
                        return linha.substring(linha.indexOf(':') + 1).trim();
                    }
                }
            }
        } catch (Throwable ignorado) {
            // usa o nome genérico abaixo
        }
        return System.getProperty("os.arch") + " (" + Runtime.getRuntime().availableProcessors() + " CPUs)";
    }

    public int cpusLogicas() {
        return cpusLogicas;
    }

    public int nucleosFisicos() {
        return nucleos.size();
    }

    public List<int[]> nucleos() {
        return nucleos;
    }

    public String nomeProcessador() {
        return nomeProcessador;
    }

    public boolean temSmt() {
        return cpusLogicas > nucleos.size();
    }

    /** Ordem COMPACTA (enunciado): 0, 1, 2, 3, ... — preenche um núcleo (com seus irmãos SMT) antes do próximo. */
    public int[] ordemCompacta() {
        int[] ordem = new int[cpusLogicas];
        for (int i = 0; i < cpusLogicas; i++) {
            ordem[i] = i;
        }
        return ordem;
    }

    /** Ordem ESPALHADA: primeiro uma CPU lógica de cada núcleo físico, depois os irmãos SMT. */
    public int[] ordemEspalhada() {
        List<Integer> ordem = new ArrayList<>();
        int maxPorNucleo = nucleos.stream().mapToInt(n -> n.length).max().orElse(1);
        for (int nivel = 0; nivel < maxPorNucleo; nivel++) {
            for (int[] nucleo : nucleos) {
                if (nivel < nucleo.length) {
                    ordem.add(nucleo[nivel]);
                }
            }
        }
        return ordem.stream().mapToInt(Integer::intValue).toArray();
    }

    public String descricao() {
        StringBuilder sb = new StringBuilder();
        sb.append(nomeProcessador).append('\n');
        sb.append("CPUs lógicas: ").append(cpusLogicas)
                .append(" | Núcleos físicos: ").append(nucleosFisicos())
                .append(" | SMT: ").append(temSmt() ? "sim" : "não").append('\n');
        for (int i = 0; i < nucleos.size(); i++) {
            sb.append("  Núcleo ").append(i).append(" -> CPUs ").append(Arrays.toString(nucleos.get(i))).append('\n');
        }
        return sb.toString();
    }
}
