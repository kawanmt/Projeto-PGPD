package br.edu.pgpd.transacoes.benchmark;

import br.edu.pgpd.transacoes.Analises;
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

import java.util.concurrent.ExecutorService;

/**
 * Análises que o benchmark sabe medir, com acesso uniforme às versões sequencial e paralela.
 *
 * @author Vitor Abrahão
 */
public enum TipoAnalise {

    AGREGACAO("Agregação financeira") {
        @Override
        public Object sequencial(ConjuntoTransacoes ds) {
            return AgregacaoSequencial.calcular(ds);
        }

        @Override
        public Object paralela(ConjuntoTransacoes ds, ExecutorService ex, int tarefas) throws Exception {
            return AgregacaoParalela.calcular(ds, ex, tarefas);
        }

        @Override
        public boolean equivalentes(Object a, Object b) {
            return ((ResultadoAgregacao) a).equivalente((ResultadoAgregacao) b);
        }
    },

    SUSPEITAS("Transações suspeitas") {
        @Override
        public Object sequencial(ConjuntoTransacoes ds) {
            return DeteccaoSequencial.calcular(ds);
        }

        @Override
        public Object paralela(ConjuntoTransacoes ds, ExecutorService ex, int tarefas) throws Exception {
            return DeteccaoParalela.calcular(ds, ex, tarefas);
        }

        @Override
        public boolean equivalentes(Object a, Object b) {
            return ((ResultadoDeteccao) a).equivalente((ResultadoDeteccao) b);
        }
    },

    CATEGORIAS("Estatísticas por categoria") {
        @Override
        public Object sequencial(ConjuntoTransacoes ds) {
            return CategoriaSequencial.calcular(ds);
        }

        @Override
        public Object paralela(ConjuntoTransacoes ds, ExecutorService ex, int tarefas) throws Exception {
            return CategoriaParalela.calcular(ds, ex, tarefas);
        }

        @Override
        public boolean equivalentes(Object a, Object b) {
            return ((ResultadoCategorias) a).equivalente((ResultadoCategorias) b);
        }
    },

    TODAS("Todas as análises") {
        @Override
        public Object sequencial(ConjuntoTransacoes ds) {
            return Analises.sequencial(ds);
        }

        @Override
        public Object paralela(ConjuntoTransacoes ds, ExecutorService ex, int tarefas) throws Exception {
            return Analises.paralela(ds, ex, tarefas);
        }

        @Override
        public boolean equivalentes(Object a, Object b) {
            return ((Analises.ResultadoCompleto) a).equivalente((Analises.ResultadoCompleto) b);
        }
    };

    private final String rotulo;

    TipoAnalise(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public abstract Object sequencial(ConjuntoTransacoes ds);

    public abstract Object paralela(ConjuntoTransacoes ds, ExecutorService ex, int tarefas) throws Exception;

    public abstract boolean equivalentes(Object a, Object b);
}
