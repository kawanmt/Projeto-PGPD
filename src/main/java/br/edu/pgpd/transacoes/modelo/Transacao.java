package br.edu.pgpd.transacoes.modelo;

import java.time.LocalDate;

/**
 * Visão de uma única transação (uma "linha" do conjunto). Usada apenas para exibição e
 * exportação — as análises percorrem diretamente os arrays de {@link ConjuntoTransacoes}.
 *
 * @author Kawan Mesquita
 */
public record Transacao(
        long id,
        int cliente,
        LocalDate data,
        int hora,
        long valorCentavos,
        Categoria categoria,
        TipoTransacao tipo,
        Cidade cidade) {

    public Estado estado() {
        return cidade.estado();
    }

    public String codigoCliente() {
        return String.format("C%06d", cliente);
    }
}
