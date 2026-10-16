package br.edu.pgpd.transacoes.dados;

import br.edu.pgpd.transacoes.modelo.Categoria;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Caio Polidoro
 */
class GeradorTransacoesTest {

    private static final int N = 300_001; // não múltiplo do tamanho do bloco

    @Test
    void deterministicoIndependenteDoNumeroDeThreads() {
        GeradorTransacoes g = new GeradorTransacoes(42);
        ConjuntoTransacoes a = g.gerar(N, 1);
        ConjuntoTransacoes b = g.gerar(N, 7);
        assertArrayEquals(a.valores(), b.valores());
        assertArrayEquals(a.clientes(), b.clientes());
        assertArrayEquals(a.categorias(), b.categorias());
        assertArrayEquals(a.tipos(), b.tipos());
        assertArrayEquals(a.cidades(), b.cidades());
        assertArrayEquals(a.dias(), b.dias());
        assertArrayEquals(a.horas(), b.horas());
    }

    @Test
    void sementesDiferentesGeramDadosDiferentes() {
        assertFalse(Arrays.equals(new GeradorTransacoes(1).gerar(1000).valores(),
                new GeradorTransacoes(2).gerar(1000).valores()));
    }

    @Test
    void valoresDentroDosLimites() {
        ConjuntoTransacoes c = new GeradorTransacoes().gerar(N);
        for (int i = 0; i < N; i++) {
            int v = c.valores()[i];
            assertTrue(v >= GeradorTransacoes.VALOR_MINIMO_CENTAVOS && v <= GeradorTransacoes.VALOR_MAXIMO_CENTAVOS);
            assertTrue(c.horas()[i] >= 0 && c.horas()[i] < 24);
            assertTrue(c.dias()[i] >= 0 && c.dias()[i] < ConjuntoTransacoes.DIAS_NO_ANO);
            assertTrue(c.clientes()[i] >= 1 && c.clientes()[i] <= GeradorTransacoes.NUM_CLIENTES);
        }
    }

    @Test
    void distribuicaoDasCategoriasSegueOsPesos() {
        ConjuntoTransacoes c = new GeradorTransacoes().gerar(N);
        long[] contagem = new long[Categoria.QUANTIDADE];
        for (byte cat : c.categorias()) {
            contagem[cat]++;
        }
        // Alimentação tem peso 30% e Eletrônicos 8%
        assertEquals(0.30, contagem[Categoria.ALIMENTACAO.ordinal()] / (double) N, 0.01);
        assertEquals(0.08, contagem[Categoria.ELETRONICOS.ordinal()] / (double) N, 0.01);
    }

    @Test
    void clientesSeguemZipf() {
        ConjuntoTransacoes c = new GeradorTransacoes().gerar(N);
        long cliente1 = Arrays.stream(c.clientes()).filter(x -> x == 1).count();
        long cliente1000 = Arrays.stream(c.clientes()).filter(x -> x == 1000).count();
        assertTrue(cliente1 > 100 * Math.max(1, cliente1000), "o cliente mais frequente domina");
    }
}
