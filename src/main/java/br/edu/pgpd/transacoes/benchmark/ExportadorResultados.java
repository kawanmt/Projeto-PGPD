package br.edu.pgpd.transacoes.benchmark;

import br.edu.pgpd.transacoes.util.Formatos;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Exporta as medições em CSV (para planilhas) e em Markdown (tabelas prontas para o relatório).
 *
 * @author Vitor Abrahão
 */
public final class ExportadorResultados {

    private ExportadorResultados() {
    }

    /** CSV com separador ";" e vírgula decimal (Excel em português). */
    public static void csv(List<Medicao> medicoes, Path arquivo) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8))) {
            out.print('﻿');
            out.println("tamanho;analise;configuracao;threads;tarefas;cpus;mediana_ms;media_ms;desvio_ms;"
                    + "minimo_ms;sequencial_ms;speedup;eficiencia;valido;tempos_ms");
            for (Medicao m : medicoes) {
                out.println(String.join(";",
                        Long.toString(m.tamanho()),
                        m.analise().rotulo(),
                        m.configuracao().rotulo(),
                        Integer.toString(m.threads()),
                        Integer.toString(m.tarefas()),
                        m.cpus(),
                        num(m.mediana()), num(m.media()), num(m.desvio()), num(m.minimo()),
                        num(m.sequencialMs()), num(m.speedup()), num(m.eficiencia()),
                        m.valido() ? "sim" : "nao",
                        Arrays.stream(m.temposMs()).mapToObj(ExportadorResultados::num).collect(Collectors.joining(" | "))));
            }
        }
    }

    private static String num(double v) {
        return String.format(Locale.ROOT, "%.4f", v).replace('.', ',');
    }

    /** Tabelas Markdown: uma por análise e tamanho. */
    public static void markdown(List<Medicao> medicoes, List<Medicao> granularidade, String infoSistema, Path arquivo)
            throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8))) {
            out.println("# Resultados do benchmark");
            out.println();
            out.println("```");
            out.print(infoSistema);
            out.println("```");

            List<Long> tamanhos = medicoes.stream().map(Medicao::tamanho).distinct().sorted().toList();
            for (TipoAnalise analise : TipoAnalise.values()) {
                if (medicoes.stream().noneMatch(m -> m.analise() == analise)) {
                    continue;
                }
                out.println();
                out.println("## " + analise.rotulo());
                for (long tamanho : tamanhos) {
                    List<Medicao> grupo = medicoes.stream()
                            .filter(m -> m.analise() == analise && m.tamanho() == tamanho).toList();
                    if (grupo.isEmpty()) {
                        continue;
                    }
                    out.println();
                    out.println("### " + Formatos.inteiro(tamanho) + " transações");
                    out.println();
                    tabela(out, grupo);
                }
            }

            if (!granularidade.isEmpty()) {
                out.println();
                out.println("## Granularidade (tarefas por thread)");
                out.println();
                tabela(out, granularidade);
            }
        }
    }

    private static void tabela(PrintWriter out, List<Medicao> grupo) {
        out.println("| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |");
        out.println("|---|---:|---:|---|---:|---:|---:|---:|:---:|");
        for (Medicao m : grupo) {
            out.printf("| %s | %d | %d | %s | %s | %s | %s | %s | %s |%n",
                    m.configuracao().rotulo(), m.threads(), m.tarefas(), m.cpus(),
                    Formatos.ms(m.mediana()), Formatos.ms(m.desvio()),
                    Formatos.decimal(m.speedup(), 2), Formatos.percentual(m.eficiencia()),
                    m.valido() ? "✔" : "✘");
        }
    }
}
