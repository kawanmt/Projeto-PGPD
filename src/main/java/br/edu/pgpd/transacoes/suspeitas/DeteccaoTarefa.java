package br.edu.pgpd.transacoes.suspeitas;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;

import java.util.concurrent.Callable;

/**
 * Tarefa da detecção de suspeitas: analisa a faixa {@code [inicio, fim)} e devolve o parcial.
 *
 * @author Caio Polidoro
 */
public final class DeteccaoTarefa implements Callable<ResultadoDeteccao> {

    private final ConjuntoTransacoes conjunto;
    private final int inicio;
    private final int fim;

    public DeteccaoTarefa(ConjuntoTransacoes conjunto, int inicio, int fim) {
        this.conjunto = conjunto;
        this.inicio = inicio;
        this.fim = fim;
    }

    @Override
    public ResultadoDeteccao call() {
        return DeteccaoSequencial.processar(conjunto, inicio, fim);
    }
}
