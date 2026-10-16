package br.edu.pgpd.transacoes.agregacao;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;

import java.util.concurrent.Callable;

/**
 * Tarefa da agregação: processa uma faixa {@code [inicio, fim)} e devolve o resultado parcial.
 * Implementa {@link Callable} para que o ExecutorService devolva o parcial via {@code Future}.
 *
 * <p>A tarefa só lê o conjunto compartilhado e escreve em um resultado próprio (local), então
 * não há condição de corrida nem necessidade de sincronização.
 *
 * @author Kawan Mesquita
 */
public final class AgregacaoTarefa implements Callable<ResultadoAgregacao> {

    private final ConjuntoTransacoes conjunto;
    private final int inicio;
    private final int fim;

    public AgregacaoTarefa(ConjuntoTransacoes conjunto, int inicio, int fim) {
        this.conjunto = conjunto;
        this.inicio = inicio;
        this.fim = fim;
    }

    @Override
    public ResultadoAgregacao call() {
        return AgregacaoSequencial.processar(conjunto, inicio, fim);
    }
}
