package br.edu.pgpd.transacoes.categorias;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;

import java.util.concurrent.Callable;

/**
 * Tarefa das estatísticas por categoria: processa {@code [inicio, fim)} e devolve o parcial.
 *
 * @author Henrique Mosson
 */
public final class CategoriaTarefa implements Callable<ResultadoCategorias> {

    private final ConjuntoTransacoes conjunto;
    private final int inicio;
    private final int fim;

    public CategoriaTarefa(ConjuntoTransacoes conjunto, int inicio, int fim) {
        this.conjunto = conjunto;
        this.inicio = inicio;
        this.fim = fim;
    }

    @Override
    public ResultadoCategorias call() {
        return CategoriaSequencial.processar(conjunto, inicio, fim);
    }
}
