package br.edu.pgpd.transacoes;

import br.edu.pgpd.transacoes.agregacao.AgregacaoParalela;
import br.edu.pgpd.transacoes.agregacao.AgregacaoSequencial;
import br.edu.pgpd.transacoes.agregacao.ResultadoAgregacao;
import br.edu.pgpd.transacoes.categorias.CategoriaParalela;
import br.edu.pgpd.transacoes.categorias.CategoriaSequencial;
import br.edu.pgpd.transacoes.categorias.ResultadoCategorias;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.suspeitas.DeteccaoParalela;
import br.edu.pgpd.transacoes.suspeitas.DeteccaoSequencial;
import br.edu.pgpd.transacoes.suspeitas.ResultadoDeteccao;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Integração dos três módulos de análise: executa todas as análises sobre o mesmo conjunto,
 * na versão sequencial ou paralela.
 *
 * @author Kawan Mesquita, Caio Polidoro, Henrique Mosson, Vitor Abrahão
 */
public final class Analises {

    private Analises() {
    }

    /** Resultado das três análises. */
    public record ResultadoCompleto(ResultadoAgregacao agregacao,
                                    ResultadoDeteccao suspeitas,
                                    ResultadoCategorias categorias) {

        public boolean equivalente(ResultadoCompleto o) {
            return agregacao.equivalente(o.agregacao)
                    && suspeitas.equivalente(o.suspeitas)
                    && categorias.equivalente(o.categorias);
        }
    }

    public static ResultadoCompleto sequencial(ConjuntoTransacoes conjunto) {
        return new ResultadoCompleto(
                AgregacaoSequencial.calcular(conjunto),
                DeteccaoSequencial.calcular(conjunto),
                CategoriaSequencial.calcular(conjunto));
    }

    public static ResultadoCompleto paralela(ConjuntoTransacoes conjunto, ExecutorService executor, int numTarefas)
            throws InterruptedException, ExecutionException {
        return new ResultadoCompleto(
                AgregacaoParalela.calcular(conjunto, executor, numTarefas),
                DeteccaoParalela.calcular(conjunto, executor, numTarefas),
                CategoriaParalela.calcular(conjunto, executor, numTarefas));
    }
}
