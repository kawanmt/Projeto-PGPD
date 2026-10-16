package br.edu.pgpd.transacoes;

import br.edu.pgpd.transacoes.benchmark.BenchmarkRunner;
import br.edu.pgpd.transacoes.benchmark.Configuracao;
import br.edu.pgpd.transacoes.benchmark.ConfiguracaoBenchmark;
import br.edu.pgpd.transacoes.benchmark.InfoSistema;
import br.edu.pgpd.transacoes.benchmark.TipoAnalise;
import br.edu.pgpd.transacoes.dados.ExportadorAmostra;
import br.edu.pgpd.transacoes.dados.GeradorTransacoes;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.paralelo.EstrategiaAfinidade;
import br.edu.pgpd.transacoes.paralelo.ExecutorFactory;
import br.edu.pgpd.transacoes.paralelo.Topologia;
import br.edu.pgpd.transacoes.util.Formatos;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Ponto de entrada: Análise Paralela de Transações Financeiras.
 *
 * <pre>
 * info                                   ambiente, topologia da CPU e teste de afinidade
 * demo      [--n 10M] [--threads 2,4,8]  análises + comparação Sequencial × Executor × Affinity
 * benchmark [--tamanhos ...] [...]       experimento completo: CSV, Markdown e gráficos PNG
 * amostra   [--n 1000] [--saida ...]     exporta transações geradas em CSV
 * </pre>
 *
 * @author Kawan Mesquita, Caio Polidoro, Henrique Mosson, Vitor Abrahão
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Argumentos a;
        try {
            a = new Argumentos(args);
        } catch (IllegalArgumentException e) {
            out.println(e.getMessage());
            ajuda(out);
            System.exit(2);
            return;
        }
        Topologia topologia = Topologia.detectar();

        switch (a.comando()) {
            case "info" -> info(topologia, out);
            case "demo" -> new Demonstracao(
                    (int) a.tamanho("n", 10_000_000),
                    a.lista("threads", List.of(2, 4, 8), Integer::parseInt),
                    EstrategiaAfinidade.valueOf(a.texto("estrategia", "compacta").toUpperCase(Locale.ROOT)),
                    a.inteiro("tarefas-por-thread", 1),
                    a.inteiro("repeticoes", 3),
                    topologia, out).executar();
            case "benchmark" -> new BenchmarkRunner(configuracaoBenchmark(a), topologia, out).executar();
            case "amostra" -> amostra(a, out);
            default -> ajuda(out);
        }
    }

    private static ConfiguracaoBenchmark configuracaoBenchmark(Argumentos a) {
        ConfiguracaoBenchmark p = ConfiguracaoBenchmark.padrao();
        if (a.tem("rapido")) {
            p = new ConfiguracaoBenchmark(List.of(100_000L, 1_000_000L, 10_000_000L), p.threads(), 3, 1,
                    p.analises(), p.paralelas(), p.tarefasPorThread(), List.of(1, 4, 16), p.saida(), p.semente());
        }
        List<Integer> granularidades = a.texto("granularidade", "").equalsIgnoreCase("nao")
                ? List.of()
                : a.lista("granularidade", p.granularidades(), Integer::parseInt);
        return new ConfiguracaoBenchmark(
                a.lista("tamanhos", p.tamanhos(), Formatos::lerTamanho),
                a.lista("threads", p.threads(), Integer::parseInt),
                a.inteiro("repeticoes", p.repeticoes()),
                a.inteiro("aquecimento", p.aquecimento()),
                a.lista("analises", p.analises(), s -> TipoAnalise.valueOf(s.toUpperCase(Locale.ROOT))),
                a.lista("configuracoes", p.paralelas(), s -> Configuracao.valueOf(s.toUpperCase(Locale.ROOT))),
                a.inteiro("tarefas-por-thread", p.tarefasPorThread()),
                granularidades,
                Path.of(a.texto("saida", p.saida().toString())),
                a.tamanho("semente", p.semente()));
    }

    private static void info(Topologia topologia, PrintStream out) throws Exception {
        out.print(InfoSistema.descrever(topologia));
        out.println();
        out.println("Ordem COMPACTA  : " + java.util.Arrays.toString(topologia.ordemCompacta()));
        out.println("Ordem ESPALHADA : " + java.util.Arrays.toString(topologia.ordemEspalhada()));
        out.println();
        out.println("Teste de afinidade (uma thread em cada CPU lógica):");
        try (ExecutorFactory.Pool pool = ExecutorFactory.criar(topologia.cpusLogicas(), EstrategiaAfinidade.COMPACTA, topologia)) {
            // mantém as threads ocupadas um instante para observar em que CPU executam
            List<java.util.concurrent.Future<Integer>> f = new java.util.ArrayList<>();
            for (int i = 0; i < topologia.cpusLogicas(); i++) {
                f.add(pool.executor().submit(() -> {
                    double x = 0;
                    for (int k = 0; k < 20_000_000; k++) {
                        x += Math.sqrt(k);
                    }
                    return x > 0 ? br.edu.pgpd.transacoes.paralelo.CpuAtual.obter() : -1;
                }));
            }
            for (var fut : f) {
                fut.get();
            }
            pool.registros().stream()
                    .sorted(java.util.Comparator.comparingInt(r -> r.cpuSolicitada()))
                    .forEach(r -> out.printf("  %-12s CPU solicitada %-2d máscara %-6s executando na CPU %-2d %s%n",
                            r.thread(), r.cpuSolicitada(), r.mascara(), r.cpuObservada(), r.confirmada() ? "✔" : "✘"));
        }
        Runtime rt = Runtime.getRuntime();
        long maxTransacoes = (rt.maxMemory() - 256L * 1024 * 1024) / ConjuntoTransacoes.BYTES_POR_TRANSACAO;
        out.println();
        out.println("Maior conjunto que cabe no heap atual: ~" + Formatos.inteiro(maxTransacoes) + " transações");
    }

    private static void amostra(Argumentos a, PrintStream out) throws Exception {
        int n = (int) a.tamanho("n", 1_000);
        Path saida = Path.of(a.texto("saida", "dados/amostra.csv"));
        ConjuntoTransacoes conjunto = new GeradorTransacoes(a.tamanho("semente", GeradorTransacoes.SEMENTE_PADRAO)).gerar(n);
        ExportadorAmostra.exportar(conjunto, n, saida);
        out.println(Formatos.inteiro(n) + " transações exportadas para " + saida.toAbsolutePath());
    }

    private static void ajuda(PrintStream out) {
        out.println("""
                Análise Paralela de Transações Financeiras — JCRPGPD
                Grupo: Kawan Mesquita, Caio Polidoro, Henrique Mosson, Vitor Abrahão

                Uso: run.cmd <comando> [opções]

                  info
                      Mostra processador, topologia (núcleos/SMT), JVM e testa a afinidade.

                  demo [--n 10M] [--threads 2,4,8] [--estrategia compacta|espalhada]
                       [--tarefas-por-thread 1] [--repeticoes 3]
                      Executa as análises e compara Sequencial × Executor × Executor + Affinity.

                  benchmark [--tamanhos 100K,1M,10M,50M,100M,250M,500M] [--threads 1,2,4,6,8,12]
                            [--repeticoes 5] [--aquecimento 2] [--tarefas-por-thread 1]
                            [--analises agregacao,suspeitas,categorias,todas]
                            [--configuracoes executor,afinidade_compacta,afinidade_espalhada]
                            [--granularidade 1,2,4,8,16,64 | nao] [--saida resultados] [--rapido]
                      Experimento completo; gera resultados.csv, resultados.md e gráficos PNG.

                  amostra [--n 1000] [--saida dados/amostra.csv] [--semente 20262]
                      Exporta transações sintéticas para CSV.
                """);
    }
}
