package br.edu.pgpd.transacoes.modelo;

/**
 * Unidades federativas do Brasil.
 *
 * @author Kawan Mesquita
 */
public enum Estado {
    AC("Acre"), AL("Alagoas"), AP("Amapá"), AM("Amazonas"), BA("Bahia"), CE("Ceará"),
    DF("Distrito Federal"), ES("Espírito Santo"), GO("Goiás"), MA("Maranhão"),
    MT("Mato Grosso"), MS("Mato Grosso do Sul"), MG("Minas Gerais"), PA("Pará"),
    PB("Paraíba"), PR("Paraná"), PE("Pernambuco"), PI("Piauí"), RJ("Rio de Janeiro"),
    RN("Rio Grande do Norte"), RS("Rio Grande do Sul"), RO("Rondônia"), RR("Roraima"),
    SC("Santa Catarina"), SP("São Paulo"), SE("Sergipe"), TO("Tocantins");

    public static final Estado[] VALORES = values();
    public static final int QUANTIDADE = VALORES.length;

    private final String nome;

    Estado(String nome) {
        this.nome = nome;
    }

    public String nome() {
        return nome;
    }

    public String sigla() {
        return name();
    }
}
