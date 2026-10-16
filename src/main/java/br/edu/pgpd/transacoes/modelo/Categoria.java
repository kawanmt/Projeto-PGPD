package br.edu.pgpd.transacoes.modelo;

/**
 * Categorias de transação. O código armazenado no conjunto de dados é o {@link #ordinal()}
 * (um {@code byte}), o que permite indexar arrays de estatísticas diretamente pela categoria.
 *
 * @author Kawan Mesquita
 */
public enum Categoria {
    ALIMENTACAO("Alimentação"),
    TRANSPORTE("Transporte"),
    ELETRONICOS("Eletrônicos"),
    SAUDE("Saúde"),
    EDUCACAO("Educação"),
    VESTUARIO("Vestuário"),
    LAZER("Lazer"),
    MORADIA("Moradia");

    /** Cache de {@code values()} para evitar cópia do array a cada chamada. */
    public static final Categoria[] VALORES = values();
    public static final int QUANTIDADE = VALORES.length;

    private final String nome;

    Categoria(String nome) {
        this.nome = nome;
    }

    public String nome() {
        return nome;
    }

    public static Categoria deCodigo(int codigo) {
        return VALORES[codigo];
    }
}
