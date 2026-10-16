package br.edu.pgpd.transacoes.categorias;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.paralelo.Intervalo;
import br.edu.pgpd.transacoes.paralelo.Particionador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <b>Versão paralela</b> das estatísticas por categoria com ExecutorService (com ou sem
 * afinidade, conforme o executor recebido): divide, submete uma {@link CategoriaTarefa} por
 * parte e reduz somando os arrays parciais.
 *
 * @author Henrique Mosson
 */
public final class CategoriaParalela {

    private CategoriaParalela() {
    }

    public static ResultadoCategorias calcular(ConjuntoTransacoes conjunto, ExecutorService executor, int numTarefas)
            throws InterruptedException, ExecutionException {

        List<Intervalo> partes = Particionador.dividir(conjunto.tamanho(), numTarefas);

        List<Future<ResultadoCategorias>> parciais = new ArrayList<>(partes.size());
        for (Intervalo parte : partes) {
            parciais.add(executor.submit(new CategoriaTarefa(conjunto, parte.inicio(), parte.fim())));
        }

        ResultadoCategorias total = new ResultadoCategorias();
        for (Future<ResultadoCategorias> parcial : parciais) {
            total.combinar(parcial.get());
        }
        return total;
    }
}
