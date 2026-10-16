package br.edu.pgpd.transacoes.benchmark;

import br.edu.pgpd.transacoes.dados.GeradorTransacoes;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.paralelo.ExecutorFactory;
import br.edu.pgpd.transacoes.paralelo.Topologia;
import br.edu.pgpd.transacoes.util.Formatos;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Módulo de experimentação: executa todas as combinações de tamanho × análise × configuração ×
 * threads, mede tempos, calcula speedup e eficiência, valida os resultados e exporta tabelas e
 * gráficos.
 *
 * <p>Metodologia:
 * <ul>
 *   <li>o conjunto de cada tamanho é gerado uma única vez (a geração não é medida);</li>
 *   <li>antes de tudo há um aquecimento global para o JIT compilar os laços das análises;</li>
 *   <li>cada configuração roda {@code aquecimento} vezes sem medir e {@code repeticoes} vezes
 *       medindo; usa-se a mediana;</li>
 *   <li>o pool de threads é criado (e as threads fixadas nas CPUs) <b>antes</b> da medição; o tempo
 *       vai da divisão dos dados até o fim da redução;</li>
 *   <li>o resultado de toda execução paralela é comparado com o sequencial.</li>
 * </ul>
 *
 * @author Vitor Abrahão
 */
public final class BenchmarkRunner {

    /** Folga de memória além dos arrays do conjunto (resultados, tabelas do gerador, JVM). */
    private static final long FOLGA_MEMORIA = 256L * 1024 * 1024;

    private final ConfiguracaoBenchmark cfg;
    private final Topologia topologia;
    private final PrintStream out;
    private final GeradorTransacoes gerador;
    private final List<Medicao> medicoes = new ArrayList<>();
    private final List<Medicao> granularidade = new ArrayList<>();

    public BenchmarkRunner(ConfiguracaoBenchmark cfg, Topologia topologia, PrintStream out) {
        this.cfg = cfg;
        this.topologia = topologia;
        this.out = out;
        this.gerador = new GeradorTransacoes(cfg.semente());
    }

    public List<Medicao> medicoes() {
        return List.copyOf(medicoes);
    }

    public List<Medicao> granularidade() {
        return List.copyOf(granularidade);
    }

    public void executar() throws Exception {
        long inicioTotal = System.nanoTime();
        Files.createDirectories(cfg.saida());
        String info = InfoSistema.descrever(topologia);

        RelatorioConsole.cabecalho(out);
        out.println("                 BENCHMARK DE DESEMPENHO");
        out.println();
        out.print(info);
        out.println("Tamanhos   : " + cfg.tamanhos().stream().map(Formatos::abreviado).toList());
        out.println("Threads    : " + cfg.threads());
        out.println("Repetições : " + cfg.repeticoes() + " (+ " + cfg.aquecimento() + " de aquecimento)");
        out.println("Tarefas    : threads × " + cfg.tarefasPorThread());
        Files.writeString(cfg.saida().resolve("info-sistema.txt"), info, StandardCharsets.UTF_8);

        aquecerJit();

        ConjuntoTransacoes conjunto = null;
        for (long tamanho : cfg.tamanhos()) {
            conjunto = null; // libera o conjunto anterior antes de alocar o próximo
            System.gc();
            if (!cabeNaMemoria(tamanho)) {
                continue;
            }
            out.println();
            out.print("Gerando " + Formatos.inteiro(tamanho) + " transações ("
                    + Formatos.bytes(ConjuntoTransacoes.memoriaNecessaria(tamanho)) + ")... ");
            long t0 = System.nanoTime();
            conjunto = gerador.gerar((int) tamanho);
            out.println("ok em " + Formatos.ms((System.nanoTime() - t0) / 1e6));

            for (TipoAnalise analise : cfg.analises()) {
                medirAnalise(conjunto, analise);
            }
            TipoAnalise resumo = cfg.analises().contains(TipoAnalise.TODAS) ? TipoAnalise.TODAS : cfg.analises().getFirst();
            RelatorioConsole.formatoEnunciado(out, tamanho, resumo.rotulo(), filtrar(tamanho, resumo));
            salvarParciais(info); // salva a cada tamanho: se o experimento for interrompido, nada se perde
        }

        if (conjunto != null && !cfg.granularidades().isEmpty()) {
            medirGranularidade(conjunto);
        }
        salvarParciais(info);

        List<Path> graficos = new GeradorGraficos(medicoes, granularidade, cfg.saida()).gerarTodos();
        out.println();
        out.println("Resultados salvos em " + cfg.saida().toAbsolutePath());
        out.println("  resultados.csv, resultados.md, granularidade.csv, info-sistema.txt");
        graficos.forEach(g -> out.println("  " + g.getFileName()));
        long invalidas = medicoes.stream().filter(m -> !m.valido()).count();
        out.println(invalidas == 0
                ? "Todas as execuções paralelas produziram resultado idêntico ao sequencial."
                : "ATENÇÃO: " + invalidas + " execuções divergiram do sequencial!");
        out.println("Tempo total do benchmark: " + Formatos.decimal((System.nanoTime() - inicioTotal) / 6e10, 1) + " min");
    }

    private void salvarParciais(String info) throws IOException {
        ExportadorResultados.csv(medicoes, cfg.saida().resolve("resultados.csv"));
        ExportadorResultados.markdown(medicoes, granularidade, info, cfg.saida().resolve("resultados.md"));
        if (!granularidade.isEmpty()) {
            ExportadorResultados.csv(granularidade, cfg.saida().resolve("granularidade.csv"));
        }
    }

    private boolean cabeNaMemoria(long tamanho) {
        if (tamanho > Integer.MAX_VALUE - 8) {
            out.println("\n[pulado] " + Formatos.inteiro(tamanho) + " excede o limite de índices de um array Java.");
            return false;
        }
        Runtime rt = Runtime.getRuntime();
        long livre = rt.maxMemory() - (rt.totalMemory() - rt.freeMemory());
        long necessario = ConjuntoTransacoes.memoriaNecessaria(tamanho) + FOLGA_MEMORIA;
        if (necessario > livre) {
            out.println("\n[pulado] " + Formatos.inteiro(tamanho) + " transações precisam de ~"
                    + Formatos.bytes(necessario) + ", mas só há " + Formatos.bytes(livre)
                    + " disponíveis no heap (aumente -Xmx).");
            return false;
        }
        // Mesmo cabendo no heap, se faltar memória física o SO passa a usar o arquivo de paginação
        // (disco) e os tempos medidos deixam de refletir o processamento.
        // disponível = RAM livre no SO + espaço já reservado pela JVM mas ainda não usado
        long fisicaDisponivel = memoriaFisicaLivre() + rt.freeMemory();
        if (fisicaDisponivel > 0 && necessario > fisicaDisponivel) {
            out.println("\n[pulado] " + Formatos.inteiro(tamanho) + " transações precisam de ~"
                    + Formatos.bytes(necessario) + ", mas só há ~" + Formatos.bytes(fisicaDisponivel)
                    + " de RAM livre: o sistema usaria memória virtual em disco e a medição seria inválida.");
            return false;
        }
        return true;
    }

    private static long memoriaFisicaLivre() {
        if (java.lang.management.ManagementFactory.getOperatingSystemMXBean()
                instanceof com.sun.management.OperatingSystemMXBean os) {
            return os.getFreeMemorySize();
        }
        return -1;
    }

    /** Executa todas as análises algumas vezes num conjunto pequeno para o JIT compilar o código. */
    private void aquecerJit() throws Exception {
        out.print("\nAquecendo a JVM (JIT)... ");
        ConjuntoTransacoes pequeno = gerador.gerar(2_000_000);
        try (ExecutorFactory.Pool pool = ExecutorFactory.semAfinidade(topologia.cpusLogicas())) {
            for (int i = 0; i < 5; i++) {
                for (TipoAnalise a : TipoAnalise.values()) {
                    a.sequencial(pequeno);
                    a.paralela(pequeno, pool.executor(), topologia.cpusLogicas());
                }
            }
        }
        out.println("ok");
    }

    private void medirAnalise(ConjuntoTransacoes conjunto, TipoAnalise analise) throws Exception {
        long n = conjunto.tamanho();
        Cronometro.Resultado seq = Cronometro.medir(() -> analise.sequencial(conjunto), cfg.aquecimento(), cfg.repeticoes());
        Object referencia = seq.ultimoResultado();
        double medianaSeq = new Medicao(n, analise, Configuracao.SEQUENCIAL, 1, 1, "-", seq.temposMs(), 0, true).mediana();
        registrar(new Medicao(n, analise, Configuracao.SEQUENCIAL, 1, 1, "-", seq.temposMs(), medianaSeq, true));

        for (int threads : cfg.threads()) {
            for (Configuracao config : cfg.paralelas()) {
                if (!aplicavel(config, threads)) {
                    continue;
                }
                int tarefas = threads * cfg.tarefasPorThread();
                try (ExecutorFactory.Pool pool = ExecutorFactory.criar(threads, config.estrategia(), topologia)) {
                    Cronometro.Resultado r = Cronometro.medir(
                            () -> analise.paralela(conjunto, pool.executor(), tarefas),
                            cfg.aquecimento(), cfg.repeticoes());
                    boolean valido = analise.equivalentes(referencia, r.ultimoResultado());
                    registrar(new Medicao(n, analise, config, threads, tarefas, pool.cpusDescricao(),
                            r.temposMs(), medianaSeq, valido));
                }
            }
        }
    }

    /**
     * Afinidade só faz sentido com no máximo uma thread por CPU; a estratégia espalhada só difere
     * da compacta quando há SMT.
     */
    private boolean aplicavel(Configuracao config, int threads) {
        if (config == Configuracao.EXECUTOR) {
            return true;
        }
        if (threads > topologia.cpusLogicas()) {
            return false;
        }
        return config != Configuracao.AFINIDADE_ESPALHADA || topologia.temSmt();
    }

    /** Varia a quantidade de tarefas por thread para justificar a granularidade escolhida. */
    private void medirGranularidade(ConjuntoTransacoes conjunto) throws Exception {
        TipoAnalise analise = cfg.analises().contains(TipoAnalise.TODAS) ? TipoAnalise.TODAS : cfg.analises().getFirst();
        int threads = cfg.threads().stream().filter(t -> t <= topologia.cpusLogicas())
                .max(Comparator.naturalOrder()).orElse(topologia.cpusLogicas());
        long n = conjunto.tamanho();
        double medianaSeq = filtrar(n, analise).stream()
                .filter(m -> m.configuracao() == Configuracao.SEQUENCIAL)
                .mapToDouble(Medicao::mediana).findFirst().orElse(0);
        Object referencia = analise.sequencial(conjunto);

        RelatorioConsole.secao(out, "GRANULARIDADE (" + threads + " threads, " + Formatos.abreviado(n) + ")");
        for (int porThread : cfg.granularidades()) {
            for (Configuracao config : List.of(Configuracao.EXECUTOR, Configuracao.AFINIDADE_COMPACTA)) {
                if (!cfg.paralelas().contains(config)) {
                    continue;
                }
                int tarefas = threads * porThread;
                try (ExecutorFactory.Pool pool = ExecutorFactory.criar(threads, config.estrategia(), topologia)) {
                    Cronometro.Resultado r = Cronometro.medir(
                            () -> analise.paralela(conjunto, pool.executor(), tarefas),
                            cfg.aquecimento(), cfg.repeticoes());
                    Medicao m = new Medicao(n, analise, config, threads, tarefas, pool.cpusDescricao(),
                            r.temposMs(), medianaSeq, analise.equivalentes(referencia, r.ultimoResultado()));
                    granularidade.add(m);
                    out.printf("  %-31s tarefas=%-5d (%3d por thread)  mediana=%12s  desvio=%10s  speedup=%6s%n",
                            config.rotulo(), tarefas, porThread, Formatos.ms(m.mediana()), Formatos.ms(m.desvio()),
                            Formatos.decimal(m.speedup(), 2));
                }
            }
        }
    }

    private void registrar(Medicao m) {
        medicoes.add(m);
        RelatorioConsole.linha(out, m);
    }

    private List<Medicao> filtrar(long tamanho, TipoAnalise analise) {
        return medicoes.stream().filter(m -> m.tamanho() == tamanho && m.analise() == analise).toList();
    }
}
