package br.edu.pgpd.transacoes.suspeitas;

import br.edu.pgpd.transacoes.modelo.Categoria;
import br.edu.pgpd.transacoes.modelo.TipoTransacao;

/**
 * Regras (definidas pelo grupo) que tornam uma transação suspeita.
 *
 * <p>Cada regra ocupa um bit; {@link #avaliar} devolve a máscara com as regras atingidas
 * (0 = transação normal). O <i>score</i> de risco é a quantidade de regras atingidas.
 *
 * <p>Para criar uma nova regra: acrescente a constante aqui e a condição em {@link #avaliar}.
 *
 * @author Caio Polidoro
 */
public enum RegraSuspeita {

    R1_VALOR_MUITO_ALTO("Valor acima de R$ 10.000"),
    R2_ELETRONICO_CARO("Eletrônicos acima de R$ 5.000"),
    R3_MADRUGADA("Madrugada (0h às 5h) acima de R$ 2.000"),
    R4_VALOR_REDONDO("Valor redondo (múltiplo de R$ 1.000) a partir de R$ 3.000"),
    R5_CREDITO_LAZER("Crédito acima de R$ 8.000 em Lazer");

    public static final RegraSuspeita[] VALORES = values();
    public static final int QUANTIDADE = VALORES.length;

    // Limites em centavos
    static final int LIMITE_R1 = 1_000_000;   // R$ 10.000
    static final int LIMITE_R2 = 500_000;     // R$ 5.000
    static final int LIMITE_R3 = 200_000;     // R$ 2.000
    static final int MULTIPLO_R4 = 100_000;   // R$ 1.000
    static final int MINIMO_R4 = 300_000;     // R$ 3.000
    static final int LIMITE_R5 = 800_000;     // R$ 8.000
    static final int HORA_FIM_MADRUGADA = 5;

    private static final int ELETRONICOS = Categoria.ELETRONICOS.ordinal();
    private static final int LAZER = Categoria.LAZER.ordinal();
    private static final int CREDITO = TipoTransacao.CREDITO.ordinal();

    private final String descricao;

    RegraSuspeita(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }

    public int bit() {
        return 1 << ordinal();
    }

    /** Avalia todas as regras para uma transação e devolve a máscara de regras atingidas. */
    public static int avaliar(int valorCentavos, int categoria, int tipo, int hora) {
        int mascara = 0;
        if (valorCentavos > LIMITE_R1) {
            mascara |= 1;                                   // R1
        }
        if (valorCentavos > LIMITE_R2 && categoria == ELETRONICOS) {
            mascara |= 1 << 1;                              // R2
        }
        if (valorCentavos > LIMITE_R3 && hora <= HORA_FIM_MADRUGADA) {
            mascara |= 1 << 2;                              // R3
        }
        if (valorCentavos >= MINIMO_R4 && valorCentavos % MULTIPLO_R4 == 0) {
            mascara |= 1 << 3;                              // R4
        }
        if (valorCentavos > LIMITE_R5 && tipo == CREDITO && categoria == LAZER) {
            mascara |= 1 << 4;                              // R5
        }
        return mascara;
    }
}
