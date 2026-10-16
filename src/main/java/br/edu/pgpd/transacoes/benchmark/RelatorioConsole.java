package br.edu.pgpd.transacoes.benchmark;

import br.edu.pgpd.transacoes.util.Formatos;

import java.io.PrintStream;
import java.util.List;

/**
 * Impressão dos resultados no console, no formato da seção 18 do enunciado e em tabelas.
 *
 * @author Vitor Abrahão
 */
public final class RelatorioConsole {

    private static final String LINHA_DUPLA = "=".repeat(60);

    private RelatorioConsole() {
    }

    public static void cabecalho(PrintStream out) {
        out.println(LINHA_DUPLA);
        out.println("        ANÁLISE PARALELA DE TRANSAÇÕES FINANCEIRAS");
        out.println(LINHA_DUPLA);
    }

    public static void secao(PrintStream out, String titulo) {
        int lado = Math.max(3, (58 - titulo.length()) / 2);
        out.println();
        out.println("-".repeat(lado) + " " + titulo + " " + "-".repeat(Math.max(3, 58 - lado - titulo.length())));
        out.println();
    }

    /** Linha de progresso exibida a cada medição do benchmark. */
    public static void linha(PrintStream out, Medicao m) {
        String cfg = m.configuracao() == Configuracao.SEQUENCIAL ? "Sequencial" : m.configuracao().rotulo();
        out.printf("  [%5s] %-27s %-31s thr=%-3d CPUs=%-24s mediana=%12s  speedup=%6s  efic=%7s  %s%n",
                Formatos.abreviado(m.tamanho()), m.analise().rotulo(), cfg, m.threads(), m.cpus(),
                Formatos.ms(m.mediana()), Formatos.decimal(m.speedup(), 2), Formatos.percentual(m.eficiencia()),
                m.valido() ? "OK" : "RESULTADO DIVERGENTE!");
    }

    /**
     * Resultado no formato da seção 18 do enunciado para um tamanho de conjunto.
     *
     * @param medicoes medições de uma única análise e de um único tamanho
     */
    public static void formatoEnunciado(PrintStream out, long tamanho, String analise, List<Medicao> medicoes) {
        out.println();
        cabecalho(out);
        out.println();
        out.println("Quantidade de transações: " + Formatos.inteiro(tamanho));
        out.println("Análise: " + analise);

        for (Configuracao cfg : Configuracao.values()) {
            List<Medicao> desta = medicoes.stream().filter(m -> m.configuracao() == cfg).toList();
            if (desta.isEmpty()) {
                continue;
            }
            secao(out, cfg == Configuracao.SEQUENCIAL ? "SEQUENCIAL" : cfg.rotulo().toUpperCase());
            for (Medicao m : desta) {
                if (cfg == Configuracao.SEQUENCIAL) {
                    out.println("Tempo: " + Formatos.ms(m.mediana()));
                    continue;
                }
                out.println("Threads: " + m.threads());
                if (cfg != Configuracao.EXECUTOR) {
                    out.println("CPUs: " + m.cpus());
                }
                out.println("Tempo: " + Formatos.ms(m.mediana()));
                out.println("Speedup: " + Formatos.decimal(m.speedup(), 2));
                out.println("Eficiência: " + Formatos.percentual(m.eficiencia()));
                out.println();
            }
        }
    }

    /** Tabela de métricas (seção 11 do enunciado). */
    public static void tabela(PrintStream out, List<Medicao> medicoes) {
        String formato = "  %-32s %7s %8s %7s %14s %10s %9s %11s%n";
        out.printf(formato, "Configuração", "Threads", "Tarefas", "Dados", "Tempo (med.)", "Desvio", "Speedup", "Eficiência");
        out.println("  " + "-".repeat(104));
        for (Medicao m : medicoes) {
            out.printf(formato, m.configuracao().rotulo(), m.threads(), m.tarefas(),
                    Formatos.abreviado(m.tamanho()), Formatos.ms(m.mediana()), Formatos.ms(m.desvio()),
                    Formatos.decimal(m.speedup(), 2), Formatos.percentual(m.eficiencia()));
        }
    }
}
