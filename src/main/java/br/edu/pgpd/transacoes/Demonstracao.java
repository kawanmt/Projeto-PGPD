package br.edu.pgpd.transacoes;

import br.edu.pgpd.transacoes.benchmark.Cronometro;
import br.edu.pgpd.transacoes.benchmark.RelatorioConsole;
import br.edu.pgpd.transacoes.dados.GeradorTransacoes;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.paralelo.AffinityThreadFactory;
import br.edu.pgpd.transacoes.paralelo.EstrategiaAfinidade;
import br.edu.pgpd.transacoes.paralelo.ExecutorFactory;
import br.edu.pgpd.transacoes.paralelo.Intervalo;
import br.edu.pgpd.transacoes.paralelo.Particionador;
import br.edu.pgpd.transacoes.paralelo.Topologia;
import br.edu.pgpd.transacoes.util.Formatos;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstração completa (formato da seção 18 do enunciado): gera o conjunto, mostra os
 * resultados das três análises e compara Sequencial × Executor × Executor + Affinity.
 *
 * @author Kawan Mesquita, Caio Polidoro, Henrique Mosson, Vitor Abrahão
 */
final class Demonstracao {

    private final int n;
    private final List<Integer> listaThreads;
    private final EstrategiaAfinidade estrategia;
    private final int tarefasPorThread;
    private final int repeticoes;
    private final Topologia topologia;
    private final PrintStream out;

    Demonstracao(int n, List<Integer> listaThreads, EstrategiaAfinidade estrategia, int tarefasPorThread,
                 int repeticoes, Topologia topologia, PrintStream out) {
        this.n = n;
        this.listaThreads = listaThreads;
        this.estrategia = estrategia;
        this.tarefasPorThread = tarefasPorThread;
        this.repeticoes = repeticoes;
        this.topologia = topologia;
        this.out = out;
    }

    void executar() throws Exception {
        RelatorioConsole.cabecalho(out);
        out.println();
        out.println("Processador: " + topologia.nomeProcessador() + " (" + topologia.nucleosFisicos()
                + " núcleos / " + topologia.cpusLogicas() + " CPUs lógicas)");
        out.print("Gerando dados sintéticos... ");
        long t0 = System.nanoTime();
        ConjuntoTransacoes conjunto = new GeradorTransacoes().gerar(n);
        out.println("ok (" + Formatos.ms((System.nanoTime() - t0) / 1e6) + ", "
                + Formatos.bytes(ConjuntoTransacoes.memoriaNecessaria(n)) + ")");
        out.println();
        out.println("Quantidade de transações: " + Formatos.inteiro(n));

        // ---- Resultados das análises (versão sequencial) ----
        Analises.ResultadoCompleto r = Analises.sequencial(conjunto);
        RelatorioConsole.secao(out, "ALUNO 1 - AGREGAÇÃO FINANCEIRA");
        out.print(r.agregacao().formatar());
        RelatorioConsole.secao(out, "ALUNO 2 - TRANSAÇÕES SUSPEITAS");
        out.print(r.suspeitas().formatar(conjunto));
        RelatorioConsole.secao(out, "ALUNO 3 - ESTATÍSTICAS POR CATEGORIA");
        out.print(r.categorias().formatar());

        // ---- Desempenho ----
        RelatorioConsole.secao(out, "DIVISÃO DOS DADOS (" + listaThreads.getLast() + " threads)");
        List<Intervalo> partes = Particionador.dividir(n, listaThreads.getLast() * tarefasPorThread);
        for (int i = 0; i < partes.size() && i < 16; i++) {
            out.printf("Tarefa %2d → transações %s até %s%n", i + 1,
                    Formatos.inteiro(partes.get(i).inicio()), Formatos.inteiro(partes.get(i).fim() - 1L));
        }

        out.println();
        out.println("Medindo (mediana de " + repeticoes + " execuções de todas as análises, após aquecimento)...");

        RelatorioConsole.secao(out, "SEQUENCIAL");
        Cronometro.Resultado seq = Cronometro.medir(() -> Analises.sequencial(conjunto), 1, repeticoes);
        double tSeq = mediana(seq.temposMs());
        out.println("Tempo: " + Formatos.ms(tSeq));

        RelatorioConsole.secao(out, "EXECUTOR");
        for (int threads : listaThreads) {
            try (ExecutorFactory.Pool pool = ExecutorFactory.semAfinidade(threads)) {
                medirParalelo(conjunto, pool, threads, tSeq, r);
            }
        }

        RelatorioConsole.secao(out, "EXECUTOR + AFFINITY (" + estrategia.name().toLowerCase() + ")");
        for (int threads : listaThreads) {
            if (threads > topologia.cpusLogicas()) {
                out.println("Threads: " + threads + " — ignorado (mais threads que CPUs lógicas)\n");
                continue;
            }
            try (ExecutorFactory.Pool pool = ExecutorFactory.criar(threads, estrategia, topologia)) {
                out.println("Threads: " + threads);
                out.println("CPUs: " + pool.cpusDescricao());
                medirParalelo(conjunto, pool, threads, tSeq, r);
                imprimirAfinidade(pool.registros());
            }
        }
    }

    private void medirParalelo(ConjuntoTransacoes conjunto, ExecutorFactory.Pool pool, int threads, double tSeq,
                               Analises.ResultadoCompleto referencia) throws Exception {
        int tarefas = threads * tarefasPorThread;
        Cronometro.Resultado res = Cronometro.medir(
                () -> Analises.paralela(conjunto, pool.executor(), tarefas), 1, repeticoes);
        double t = mediana(res.temposMs());
        double speedup = tSeq / t;
        boolean igual = referencia.equivalente((Analises.ResultadoCompleto) res.ultimoResultado());
        if (!pool.comAfinidade()) {
            out.println("Threads: " + threads);
        }
        out.println("Tarefas: " + tarefas);
        out.println("Tempo: " + Formatos.ms(t));
        out.println("Speedup: " + Formatos.decimal(speedup, 2));
        out.println("Eficiência: " + Formatos.percentual(speedup / threads));
        out.println("Resultado idêntico ao sequencial: " + (igual ? "sim" : "NÃO"));
        out.println();
    }

    private void imprimirAfinidade(List<AffinityThreadFactory.RegistroAfinidade> registros) {
        out.println("Verificação da afinidade (thread → CPU):");
        List<AffinityThreadFactory.RegistroAfinidade> ordenados = registros.stream()
                .sorted(java.util.Comparator.comparingInt(AffinityThreadFactory.RegistroAfinidade::cpuSolicitada))
                .toList();
        for (AffinityThreadFactory.RegistroAfinidade reg : ordenados) {
            out.printf("  %-14s solicitada=CPU %-2d máscara=%-6s em execução na CPU %d %s%n",
                    reg.thread(), reg.cpuSolicitada(), reg.mascara(), reg.cpuObservada(),
                    reg.confirmada() ? "✔" : "(aguardando migração)");
        }
        out.println();
    }

    private static double mediana(double[] tempos) {
        double[] t = tempos.clone();
        Arrays.sort(t);
        return t.length % 2 == 1 ? t[t.length / 2] : (t[t.length / 2 - 1] + t[t.length / 2]) / 2;
    }
}
