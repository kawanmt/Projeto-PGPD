package br.edu.pgpd.transacoes.paralelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Henrique Mosson
 */
class ParticionadorTest {

    @Test
    void exemploDoEnunciado() {
        List<Intervalo> p = Particionador.dividir(10_000_000, 4);
        assertEquals(List.of(
                new Intervalo(0, 2_500_000),
                new Intervalo(2_500_000, 5_000_000),
                new Intervalo(5_000_000, 7_500_000),
                new Intervalo(7_500_000, 10_000_000)), p);
    }

    @ParameterizedTest
    @CsvSource({"1,1", "7,3", "100,7", "1000003,12", "5,8", "64,64", "999,1000"})
    void cobreTudoSemSobreposicaoEBalanceado(int n, int partes) {
        List<Intervalo> p = Particionador.dividir(n, partes);
        assertEquals(Math.min(n, partes), p.size());
        int esperadoInicio = 0;
        int menor = Integer.MAX_VALUE;
        int maior = 0;
        for (Intervalo i : p) {
            assertEquals(esperadoInicio, i.inicio(), "intervalos devem ser contíguos");
            assertTrue(i.tamanho() > 0, "nenhuma parte vazia");
            esperadoInicio = i.fim();
            menor = Math.min(menor, i.tamanho());
            maior = Math.max(maior, i.tamanho());
        }
        assertEquals(n, esperadoInicio, "deve cobrir [0, n)");
        assertTrue(maior - menor <= 1, "tamanhos diferem no máximo em 1");
    }

    @Test
    void conjuntoVazio() {
        assertEquals(List.of(new Intervalo(0, 0)), Particionador.dividir(0, 4));
    }

    @Test
    void partesInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> Particionador.dividir(10, 0));
    }
}
