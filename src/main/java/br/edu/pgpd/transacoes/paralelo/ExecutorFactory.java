package br.edu.pgpd.transacoes.paralelo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Cria os pools de threads usados pelas versões paralelas.
 *
 * <p>As duas variantes usam exatamente o mesmo {@code Executors.newFixedThreadPool(n, fabrica)};
 * a única diferença é a {@link ThreadFactory}: sem afinidade, threads comuns; com afinidade,
 * {@link AffinityThreadFactory}. Assim a comparação isola o efeito da afinidade.
 *
 * <p>As threads são pré-iniciadas ({@code prestartAllCoreThreads}) para que o custo de criá-las
 * e fixá-las nas CPUs não seja contado no tempo das análises.
 *
 * @author Vitor Abrahão
 */
public final class ExecutorFactory {

    private ExecutorFactory() {
    }

    /** ExecutorService sem afinidade (o SO escolhe as CPUs). */
    public static Pool semAfinidade(int threads) {
        return criar(threads, EstrategiaAfinidade.NENHUMA, null);
    }

    /** ExecutorService com cada thread presa à CPU correspondente em {@code cpus}. */
    public static Pool comAfinidade(int[] cpus) {
        AffinityThreadFactory fabrica = new AffinityThreadFactory(cpus, "afin");
        return iniciar(cpus.length, fabrica, fabrica, EstrategiaAfinidade.COMPACTA);
    }

    public static Pool criar(int threads, EstrategiaAfinidade estrategia, Topologia topologia) {
        if (threads < 1) {
            throw new IllegalArgumentException("threads deve ser >= 1");
        }
        if (estrategia == EstrategiaAfinidade.NENHUMA) {
            return iniciar(threads, new FabricaNomeada("exec"), null, estrategia);
        }
        AffinityThreadFactory fabrica = new AffinityThreadFactory(estrategia.cpus(threads, topologia), "afin");
        return iniciar(threads, fabrica, fabrica, estrategia);
    }

    private static Pool iniciar(int threads, ThreadFactory fabrica, AffinityThreadFactory afinidade,
                                EstrategiaAfinidade estrategia) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads, fabrica);
        executor.prestartAllCoreThreads();
        if (afinidade != null) {
            try {
                afinidade.aguardarInicializacao(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return new Pool(executor, estrategia, afinidade);
    }

    /** Threads comuns (daemon) com nome legível, para facilitar a depuração. */
    private static final class FabricaNomeada implements ThreadFactory {
        private final String prefixo;
        private final AtomicInteger contador = new AtomicInteger();

        FabricaNomeada(String prefixo) {
            this.prefixo = prefixo;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, prefixo + "-" + contador.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }

    /** Um pool pronto para uso; feche-o com try-with-resources. */
    public static final class Pool implements AutoCloseable {
        private final ExecutorService executor;
        private final EstrategiaAfinidade estrategia;
        private final AffinityThreadFactory afinidade;

        private Pool(ExecutorService executor, EstrategiaAfinidade estrategia, AffinityThreadFactory afinidade) {
            this.executor = executor;
            this.estrategia = estrategia;
            this.afinidade = afinidade;
        }

        public ExecutorService executor() {
            return executor;
        }

        public EstrategiaAfinidade estrategia() {
            return estrategia;
        }

        public boolean comAfinidade() {
            return afinidade != null;
        }

        public List<AffinityThreadFactory.RegistroAfinidade> registros() {
            return afinidade == null ? List.of() : afinidade.registros();
        }

        /** "0,1,2,3" ou "SO" (sem afinidade). */
        public String cpusDescricao() {
            if (afinidade == null) {
                return "SO";
            }
            return Arrays.stream(afinidade.cpus()).mapToObj(Integer::toString).collect(Collectors.joining(","));
        }

        @Override
        public void close() {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
