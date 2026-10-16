package br.edu.pgpd.transacoes.agregacao;

import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.paralelo.Intervalo;
import br.edu.pgpd.transacoes.paralelo.Particionador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <b>Versão paralela</b> da agregação com ExecutorService.
 *
 * <ol>
 *   <li><b>Divisão:</b> o conjunto é dividido em {@code numTarefas} faixas contíguas;</li>
 *   <li><b>Submissão:</b> cada faixa vira uma {@link AgregacaoTarefa} enviada ao executor;</li>
 *   <li><b>Redução:</b> os resultados parciais ({@code Future.get()}) são combinados.</li>
 * </ol>
 *
 * <p>A mesma classe atende às versões <i>ExecutorService</i> e <i>ExecutorService + Affinity</i>:
 * o que muda é apenas o executor recebido (criado com ou sem afinidade pela
 * {@code ExecutorFactory}).
 *
 * @author Kawan Mesquita
 */
public final class AgregacaoParalela {

    private AgregacaoParalela() {
    }

    public static ResultadoAgregacao calcular(ConjuntoTransacoes conjunto, ExecutorService executor, int numTarefas)
            throws InterruptedException, ExecutionException {

        List<Intervalo> partes = Particionador.dividir(conjunto.tamanho(), numTarefas);

        List<Future<ResultadoAgregacao>> parciais = new ArrayList<>(partes.size());
        for (Intervalo parte : partes) {
            parciais.add(executor.submit(new AgregacaoTarefa(conjunto, parte.inicio(), parte.fim())));
        }

        ResultadoAgregacao total = new ResultadoAgregacao();
        for (Future<ResultadoAgregacao> parcial : parciais) {
            total.combinar(parcial.get()); // aguarda a tarefa e reduz
        }
        return total;
    }
}
