package br.edu.pgpd.transacoes.suspeitas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Mantém as N transações suspeitas de maior valor (top-N) usando um heap de mínimo de tamanho N:
 * o topo do heap é a "pior" das N melhores, e só é substituído quando aparece uma maior.
 *
 * <p>Cada tarefa mantém o seu próprio top-N (sem compartilhamento); na redução os heaps são
 * mesclados. Como a ordem é total (valor, depois menor ID), o resultado não depende da divisão.
 *
 * @author Caio Polidoro
 */
public final class MaioresSuspeitas {

    /** Uma transação suspeita: ID, valor e máscara de regras atingidas. */
    public record Suspeita(long id, int valorCentavos, int mascara) {
    }

    /** Ordem crescente de "importância": menor valor primeiro; empate: maior ID primeiro. */
    private static final Comparator<Suspeita> ORDEM = Comparator
            .comparingInt(Suspeita::valorCentavos)
            .thenComparing(Comparator.comparingLong(Suspeita::id).reversed());

    private final int capacidade;
    private final PriorityQueue<Suspeita> heap;

    public MaioresSuspeitas(int capacidade) {
        this.capacidade = capacidade;
        this.heap = new PriorityQueue<>(capacidade + 1, ORDEM);
    }

    public void oferecer(long id, int valorCentavos, int mascara) {
        if (heap.size() < capacidade) {
            heap.add(new Suspeita(id, valorCentavos, mascara));
            return;
        }
        Suspeita menor = heap.peek();
        if (valorCentavos > menor.valorCentavos() || (valorCentavos == menor.valorCentavos() && id < menor.id())) {
            heap.poll();
            heap.add(new Suspeita(id, valorCentavos, mascara));
        }
    }

    public void combinar(MaioresSuspeitas outro) {
        for (Suspeita s : outro.heap) {
            oferecer(s.id(), s.valorCentavos(), s.mascara());
        }
    }

    /** Lista ordenada da maior para a menor. */
    public List<Suspeita> emOrdem() {
        List<Suspeita> lista = new ArrayList<>(heap);
        lista.sort(ORDEM.reversed());
        return lista;
    }
}
