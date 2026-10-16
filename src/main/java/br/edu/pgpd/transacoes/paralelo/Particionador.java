package br.edu.pgpd.transacoes.paralelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Divisão dos dados em blocos contíguos (decomposição por blocos).
 *
 * <p>{@code n} transações são divididas em {@code partes} intervalos cujo tamanho difere em no
 * máximo 1: os {@code n % partes} primeiros recebem um elemento a mais. Exemplo do enunciado
 * (10.000.000 transações, 4 partes):
 * <pre>
 * Parte 1 → transações 0 até 2.499.999
 * Parte 2 → transações 2.500.000 até 4.999.999
 * Parte 3 → transações 5.000.000 até 7.499.999
 * Parte 4 → transações 7.500.000 até 9.999.999
 * </pre>
 *
 * <p>Blocos contíguos (em vez de intercalar índices entre threads) mantêm o acesso à memória
 * sequencial e evitam que duas threads trabalhem na mesma linha de cache.
 *
 * @author Henrique Mosson
 */
public final class Particionador {

    private Particionador() {
    }

    /**
     * Divide {@code [0, n)} em {@code partes} intervalos balanceados.
     * Se houver mais partes do que elementos, cria apenas {@code n} partes (nenhuma vazia);
     * para {@code n == 0} retorna um único intervalo vazio.
     */
    public static List<Intervalo> dividir(int n, int partes) {
        if (partes < 1) {
            throw new IllegalArgumentException("O número de partes deve ser >= 1: " + partes);
        }
        if (n == 0) {
            return List.of(new Intervalo(0, 0));
        }
        int k = Math.min(partes, n);
        int base = n / k;
        int resto = n % k;

        List<Intervalo> intervalos = new ArrayList<>(k);
        int inicio = 0;
        for (int i = 0; i < k; i++) {
            int tamanho = base + (i < resto ? 1 : 0);
            intervalos.add(new Intervalo(inicio, inicio + tamanho));
            inicio += tamanho;
        }
        return intervalos;
    }
}
