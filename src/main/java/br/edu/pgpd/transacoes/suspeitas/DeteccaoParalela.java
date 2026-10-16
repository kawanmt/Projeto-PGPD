package br.edu.pgpd.transacoes.suspeitas;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.paralelo.Intervalo;
import br.edu.pgpd.transacoes.paralelo.Particionador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <b>Versão paralela</b> da detecção de suspeitas com ExecutorService (com ou sem afinidade,
 * conforme o executor recebido): divide, submete uma {@link DeteccaoTarefa} por parte e reduz.
 *
 * @author Caio Polidoro
 */
public final class DeteccaoParalela {

    private DeteccaoParalela() {
    }

    public static ResultadoDeteccao calcular(ConjuntoTransacoes conjunto, ExecutorService executor, int numTarefas)
            throws InterruptedException, ExecutionException {

        List<Intervalo> partes = Particionador.dividir(conjunto.tamanho(), numTarefas);

        List<Future<ResultadoDeteccao>> parciais = new ArrayList<>(partes.size());
        for (Intervalo parte : partes) {
            parciais.add(executor.submit(new DeteccaoTarefa(conjunto, parte.inicio(), parte.fim())));
        }

        ResultadoDeteccao total = new ResultadoDeteccao();
        for (Future<ResultadoDeteccao> parcial : parciais) {
            total.combinar(parcial.get());
        }
        return total;
    }
}
