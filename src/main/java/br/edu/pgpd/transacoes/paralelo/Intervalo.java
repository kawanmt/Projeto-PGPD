package br.edu.pgpd.transacoes.paralelo;

/**
 * Faixa de índices {@code [inicio, fim)} do conjunto de transações processada por uma tarefa.
 *
 * @author Henrique Mosson
 */
public record Intervalo(int inicio, int fim) {

    public Intervalo {
        if (inicio < 0 || fim < inicio) {
            throw new IllegalArgumentException("Intervalo inválido: [" + inicio + ", " + fim + ")");
        }
    }

    public int tamanho() {
        return fim - inicio;
    }

    @Override
    public String toString() {
        return "transações " + inicio + " até " + (fim - 1);
    }
}
