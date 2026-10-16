package br.edu.pgpd.transacoes.dados;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.modelo.Transacao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Exporta as primeiras transações de um conjunto para CSV (separador ";" e vírgula decimal,
 * compatível com o Excel em português).
 *
 * @author Caio Polidoro
 */
public final class ExportadorAmostra {

    private ExportadorAmostra() {
    }

    public static void exportar(ConjuntoTransacoes conjunto, int quantidade, Path arquivo) throws IOException {
        if (arquivo.getParent() != null) {
            Files.createDirectories(arquivo.getParent());
        }
        int n = Math.min(quantidade, conjunto.tamanho());
        try (BufferedWriter out = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
            out.write('﻿'); // BOM: o Excel reconhece o UTF-8 e exibe os acentos corretamente
            out.write("id;cliente;data;hora;valor;categoria;tipo;cidade;estado");
            out.newLine();
            for (int i = 0; i < n; i++) {
                Transacao t = conjunto.transacao(i);
                out.write(t.id() + ";" + t.codigoCliente() + ";" + t.data() + ";"
                        + String.format("%02d:00", t.hora()) + ";"
                        + String.format("%d,%02d", t.valorCentavos() / 100, t.valorCentavos() % 100) + ";"
                        + t.categoria().nome() + ";" + t.tipo().nome() + ";"
                        + t.cidade().nome() + ";" + t.estado().sigla());
                out.newLine();
            }
        }
    }
}
