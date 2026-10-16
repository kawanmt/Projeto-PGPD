package br.edu.pgpd.transacoes.modelo;

/**
 * Cidades onde as transações ocorrem. O conjunto de dados guarda apenas o código da cidade
 * (1 byte); o estado é obtido pela tabela {@link #ESTADO_DA_CIDADE}, economizando memória.
 * A população (em milhares, aproximada) é usada pelo gerador como peso da distribuição.
 *
 * @author Kawan Mesquita
 */
public enum Cidade {
    SAO_PAULO("São Paulo", Estado.SP, 11450),
    RIO_DE_JANEIRO("Rio de Janeiro", Estado.RJ, 6211),
    BRASILIA("Brasília", Estado.DF, 2817),
    FORTALEZA("Fortaleza", Estado.CE, 2428),
    SALVADOR("Salvador", Estado.BA, 2418),
    BELO_HORIZONTE("Belo Horizonte", Estado.MG, 2315),
    MANAUS("Manaus", Estado.AM, 2063),
    CURITIBA("Curitiba", Estado.PR, 1773),
    RECIFE("Recife", Estado.PE, 1488),
    GOIANIA("Goiânia", Estado.GO, 1437),
    PORTO_ALEGRE("Porto Alegre", Estado.RS, 1332),
    BELEM("Belém", Estado.PA, 1303),
    GUARULHOS("Guarulhos", Estado.SP, 1291),
    CAMPINAS("Campinas", Estado.SP, 1139),
    SAO_LUIS("São Luís", Estado.MA, 1037),
    MACEIO("Maceió", Estado.AL, 957),
    CAMPO_GRANDE("Campo Grande", Estado.MS, 898),
    SAO_GONCALO("São Gonçalo", Estado.RJ, 896),
    TERESINA("Teresina", Estado.PI, 866),
    JOAO_PESSOA("João Pessoa", Estado.PB, 833),
    RIBEIRAO_PRETO("Ribeirão Preto", Estado.SP, 698),
    UBERLANDIA("Uberlândia", Estado.MG, 713),
    NATAL("Natal", Estado.RN, 751),
    CUIABA("Cuiabá", Estado.MT, 650),
    ARACAJU("Aracaju", Estado.SE, 602),
    FEIRA_DE_SANTANA("Feira de Santana", Estado.BA, 616),
    JOINVILLE("Joinville", Estado.SC, 616),
    LONDRINA("Londrina", Estado.PR, 555),
    PORTO_VELHO("Porto Velho", Estado.RO, 460),
    FLORIANOPOLIS("Florianópolis", Estado.SC, 537),
    VITORIA("Vitória", Estado.ES, 322),
    VILA_VELHA("Vila Velha", Estado.ES, 467),
    MACAPA("Macapá", Estado.AP, 442),
    CAXIAS_DO_SUL("Caxias do Sul", Estado.RS, 463),
    RIO_BRANCO("Rio Branco", Estado.AC, 364),
    BOA_VISTA("Boa Vista", Estado.RR, 413),
    PALMAS("Palmas", Estado.TO, 302),
    SANTOS("Santos", Estado.SP, 418),
    PETROLINA("Petrolina", Estado.PE, 386),
    MARINGA("Maringá", Estado.PR, 409);

    public static final Cidade[] VALORES = values();
    public static final int QUANTIDADE = VALORES.length;

    /** Tabela de consulta: código da cidade -> código (ordinal) do estado. */
    public static final byte[] ESTADO_DA_CIDADE = new byte[QUANTIDADE];

    static {
        for (Cidade c : VALORES) {
            ESTADO_DA_CIDADE[c.ordinal()] = (byte) c.estado.ordinal();
        }
    }

    private final String nome;
    private final Estado estado;
    private final int populacaoMil;

    Cidade(String nome, Estado estado, int populacaoMil) {
        this.nome = nome;
        this.estado = estado;
        this.populacaoMil = populacaoMil;
    }

    public String nome() {
        return nome;
    }

    public Estado estado() {
        return estado;
    }

    public int populacaoMil() {
        return populacaoMil;
    }

    public static Cidade deCodigo(int codigo) {
        return VALORES[codigo];
    }
}
