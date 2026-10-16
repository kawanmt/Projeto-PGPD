package br.edu.pgpd.transacoes;

import br.edu.pgpd.transacoes.agregacao.AgregacaoParalela;
import br.edu.pgpd.transacoes.agregacao.AgregacaoSequencial;
import br.edu.pgpd.transacoes.agregacao.ResultadoAgregacao;
import br.edu.pgpd.transacoes.categorias.CategoriaParalela;
import br.edu.pgpd.transacoes.categorias.CategoriaSequencial;
import br.edu.pgpd.transacoes.categorias.ResultadoCategorias;
import br.edu.pgpd.transacoes.dados.GeradorTransacoes;
import br.edu.pgpd.transacoes.modelo.Categoria;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import br.edu.pgpd.transacoes.paralelo.EstrategiaAfinidade;
import br.edu.pgpd.transacoes.paralelo.ExecutorFactory;
import br.edu.pgpd.transacoes.paralelo.Topologia;
import br.edu.pgpd.transacoes.suspeitas.DeteccaoParalela;
import br.edu.pgpd.transacoes.suspeitas.DeteccaoSequencial;
import br.edu.pgpd.transacoes.suspeitas.RegraSuspeita;
import br.edu.pgpd.transacoes.suspeitas.ResultadoDeteccao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A propriedade central do paralelismo de dados: para qualquer número de threads, de tarefas e
 * qualquer estratégia de afinidade, o resultado paralelo é idêntico ao sequencial.
 *
 * @author Kawan Mesquita, Caio Polidoro, Henrique Mosson, Vitor Abrahão
 */
class EquivalenciaParaleloSequencialTest {

    private static final Topologia TOPOLOGIA = Topologia.detectar();
    private static ConjuntoTransacoes conjunto;
    private static ResultadoAgregacao refAgregacao;
    private static ResultadoDeteccao refSuspeitas;
    private static ResultadoCategorias refCategorias;

    @BeforeAll
    static void preparar() {
        conjunto = new GeradorTransacoes(7).gerar(1_000_003);
        refAgregacao = AgregacaoSequencial.calcular(conjunto);
        refSuspeitas = DeteccaoSequencial.calcular(conjunto);
        refCategorias = CategoriaSequencial.calcular(conjunto);
    }

    static Stream<Arguments> configuracoes() {
        int cpus = TOPOLOGIA.cpusLogicas();
        return Stream.of(
                Arguments.of(1, 1, EstrategiaAfinidade.NENHUMA),
                Arguments.of(2, 2, EstrategiaAfinidade.NENHUMA),
                Arguments.of(3, 7, EstrategiaAfinidade.NENHUMA),
                Arguments.of(4, 64, EstrategiaAfinidade.NENHUMA),
                Arguments.of(16, 16, EstrategiaAfinidade.NENHUMA),
                Arguments.of(Math.min(2, cpus), 2, EstrategiaAfinidade.COMPACTA),
                Arguments.of(cpus, cpus, EstrategiaAfinidade.COMPACTA),
                Arguments.of(cpus, cpus * 4, EstrategiaAfinidade.ESPALHADA));
    }

    @ParameterizedTest(name = "{0} threads, {1} tarefas, afinidade {2}")
    @MethodSource("configuracoes")
    void paraleloIgualAoSequencial(int threads, int tarefas, EstrategiaAfinidade estrategia) throws Exception {
        try (ExecutorFactory.Pool pool = ExecutorFactory.criar(threads, estrategia, TOPOLOGIA)) {
            assertTrue(refAgregacao.equivalente(AgregacaoParalela.calcular(conjunto, pool.executor(), tarefas)));
            assertTrue(refSuspeitas.equivalente(DeteccaoParalela.calcular(conjunto, pool.executor(), tarefas)));
            assertTrue(refCategorias.equivalente(CategoriaParalela.calcular(conjunto, pool.executor(), tarefas)));
        }
    }

    @Test
    void agregacaoConfereComCalculoDireto() {
        long soma = 0;
        int maior = Integer.MIN_VALUE;
        int menor = Integer.MAX_VALUE;
        for (int v : conjunto.valores()) {
            soma += v;
            maior = Math.max(maior, v);
            menor = Math.min(menor, v);
        }
        assertEquals(conjunto.tamanho(), refAgregacao.quantidade());
        assertEquals(soma, refAgregacao.somaCentavos());
        assertEquals(maior, refAgregacao.maiorValorCentavos());
        assertEquals(menor, refAgregacao.menorValorCentavos());
        assertEquals(conjunto.tamanho(), java.util.Arrays.stream(refAgregacao.histograma()).sum());
    }

    @Test
    void categoriasSomamOTotal() {
        assertEquals(refAgregacao.somaCentavos(), refCategorias.totalGeralCentavos());
        assertEquals(refAgregacao.quantidade(), refCategorias.quantidadeGeral());
    }

    @Test
    void regrasDeSuspeita() {
        int eletronicos = Categoria.ELETRONICOS.ordinal();
        int alimentacao = Categoria.ALIMENTACAO.ordinal();
        assertEquals(0, RegraSuspeita.avaliar(5_000, alimentacao, 0, 12));
        assertEquals(RegraSuspeita.R1_VALOR_MUITO_ALTO.bit(), RegraSuspeita.avaliar(1_000_001, alimentacao, 0, 12));
        assertEquals(RegraSuspeita.R2_ELETRONICO_CARO.bit(), RegraSuspeita.avaliar(600_001, eletronicos, 0, 12));
        assertEquals(RegraSuspeita.R3_MADRUGADA.bit(), RegraSuspeita.avaliar(250_001, alimentacao, 0, 3));
        assertEquals(RegraSuspeita.R4_VALOR_REDONDO.bit(), RegraSuspeita.avaliar(400_000, alimentacao, 0, 12));
        long semSuspeita = refSuspeitas.porScore()[0];
        assertEquals(conjunto.tamanho(), semSuspeita + refSuspeitas.totalSuspeitas());
    }

    @Test
    void conjuntoVazioEMinusculo() throws Exception {
        try (ExecutorFactory.Pool pool = ExecutorFactory.semAfinidade(4)) {
            for (int n : new int[]{0, 1, 3}) {
                ConjuntoTransacoes c = new GeradorTransacoes().gerar(n);
                assertTrue(AgregacaoSequencial.calcular(c).equivalente(AgregacaoParalela.calcular(c, pool.executor(), 8)));
                assertTrue(DeteccaoSequencial.calcular(c).equivalente(DeteccaoParalela.calcular(c, pool.executor(), 8)));
                assertTrue(CategoriaSequencial.calcular(c).equivalente(CategoriaParalela.calcular(c, pool.executor(), 8)));
            }
        }
    }
}
