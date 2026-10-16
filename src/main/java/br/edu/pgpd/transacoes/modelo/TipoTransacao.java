package br.edu.pgpd.transacoes.modelo;

/**
 * Meio de pagamento da transação. Armazenado como {@code byte} (ordinal) no conjunto de dados.
 *
 * @author Kawan Mesquita
 */
public enum TipoTransacao {
    PIX("PIX"),
    CREDITO("Crédito"),
    DEBITO("Débito"),
    BOLETO("Boleto");

    public static final TipoTransacao[] VALORES = values();
    public static final int QUANTIDADE = VALORES.length;

    private final String nome;

    TipoTransacao(String nome) {
        this.nome = nome;
    }

    public String nome() {
        return nome;
    }

    public static TipoTransacao deCodigo(int codigo) {
        return VALORES[codigo];
    }
}
