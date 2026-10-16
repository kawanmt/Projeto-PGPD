package br.edu.pgpd.transacoes.paralelo;

import net.openhft.affinity.Affinity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fábrica de threads que fixa cada thread do pool em uma CPU específica usando a
 * <b>OpenHFT Affinity</b>.
 *
 * <p>A i-ésima thread criada é presa à CPU {@code cpus[i]}: a primeira coisa que ela faz ao
 * iniciar é chamar {@link Affinity#setAffinity(int)} (no Windows, {@code SetThreadAffinityMask};
 * no Linux, {@code sched_setaffinity}). A partir daí o sistema operacional só a executa naquela
 * CPU, eliminando migrações entre núcleos.
 *
 * <p>Cada thread registra a CPU solicitada, a máscara aplicada e a CPU em que de fato está
 * executando — a prova de que a afinidade funcionou.
 *
 * @author Vitor Abrahão
 */
public final class AffinityThreadFactory implements ThreadFactory {

    private final int[] cpus;
    private final String prefixo;
    private final AtomicInteger contador = new AtomicInteger();
    private final List<RegistroAfinidade> registros = new CopyOnWriteArrayList<>();
    private final CountDownLatch iniciadas;

    public AffinityThreadFactory(int[] cpus, String prefixo) {
        if (cpus.length == 0) {
            throw new IllegalArgumentException("Informe ao menos uma CPU");
        }
        this.cpus = cpus.clone();
        this.prefixo = prefixo;
        this.iniciadas = new CountDownLatch(cpus.length);
    }

    @Override
    public Thread newThread(Runnable tarefa) {
        int indice = contador.getAndIncrement();
        int cpu = cpus[indice % cpus.length];
        String nome = prefixo + "-" + indice + "-cpu" + cpu;

        Thread t = new Thread(() -> {
            fixarNaCpu(nome, cpu);
            tarefa.run();
        }, nome);
        t.setDaemon(true);
        return t;
    }

    private void fixarNaCpu(String nome, int cpu) {
        String mascara;
        try {
            Affinity.setAffinity(cpu);
            Thread.yield(); // dá ao escalonador a chance de mover a thread para a CPU escolhida
            mascara = Affinity.getAffinity().toString();
        } catch (Throwable e) {
            mascara = "FALHOU: " + e.getMessage();
        }
        registros.add(new RegistroAfinidade(nome, cpu, mascara, CpuAtual.obter()));
        iniciadas.countDown();
    }

    /** Aguarda até que as threads iniciais tenham aplicado a afinidade. */
    public boolean aguardarInicializacao(long timeout, TimeUnit unidade) throws InterruptedException {
        return iniciadas.await(timeout, unidade);
    }

    public List<RegistroAfinidade> registros() {
        return List.copyOf(registros);
    }

    public int[] cpus() {
        return cpus.clone();
    }

    /** Registro de uma thread fixada: CPU pedida, máscara de afinidade e CPU observada. */
    public record RegistroAfinidade(String thread, int cpuSolicitada, String mascara, int cpuObservada) {

        public boolean confirmada() {
            return cpuObservada == cpuSolicitada;
        }
    }
}
