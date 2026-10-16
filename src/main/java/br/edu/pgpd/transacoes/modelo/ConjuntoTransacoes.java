package br.edu.pgpd.transacoes.modelo;

import java.time.LocalDate;

/**
 * Conjunto de transações armazenado em formato <b>Structure of Arrays</b> (SoA).
 *
 * <p>Em vez de um array de objetos {@code Transacao} (cada objeto teria cabeçalho de 12–16 bytes,
 * referência de 4–8 bytes e campos espalhados pela memória), cada atributo fica em um array
 * primitivo próprio. Vantagens para o paralelismo de dados:
 * <ul>
 *   <li>memória compacta ({@value #BYTES_POR_TRANSACAO} bytes por transação) — 500 milhões
 *       de transações ocupam cerca de 7 GB;</li>
 *   <li>acesso sequencial e contíguo: cada thread percorre a sua faixa {@code [inicio, fim)}
 *       de cada array, aproveitando cache e pré-busca (prefetch) do processador;</li>
 *   <li>os arrays são somente leitura durante as análises, portanto podem ser compartilhados
 *       entre threads sem sincronização.</li>
 * </ul>
 *
 * <p>O identificador da transação é implícito: {@code id = índice + 1}.
 * As datas pertencem ao ano {@value #ANO_BASE} e são guardadas como dia do ano (0..364).
 *
 * @author Kawan Mesquita
 */
public final class ConjuntoTransacoes {

    /** valor (4) + cliente (4) + categoria (1) + tipo (1) + cidade (1) + dia (2) + hora (1). */
    public static final int BYTES_POR_TRANSACAO = 14;

    public static final int ANO_BASE = 2025;
    public static final int DIAS_NO_ANO = 365;

    /** Tabela de consulta: dia do ano (0..364) -> mês (0..11). */
    public static final byte[] MES_DO_DIA = new byte[DIAS_NO_ANO];

    static {
        for (int d = 0; d < DIAS_NO_ANO; d++) {
            MES_DO_DIA[d] = (byte) (LocalDate.ofYearDay(ANO_BASE, d + 1).getMonthValue() - 1);
        }
    }

    private final int tamanho;

    /** Valor em centavos (inteiro): somas exatas, sem erro de arredondamento de ponto flutuante. */
    private final int[] valorCentavos;
    private final int[] cliente;
    private final byte[] categoria;
    private final byte[] tipo;
    private final byte[] cidade;
    private final short[] dia;
    private final byte[] hora;

    public ConjuntoTransacoes(int tamanho) {
        if (tamanho < 0) {
            throw new IllegalArgumentException("tamanho negativo: " + tamanho);
        }
        this.tamanho = tamanho;
        this.valorCentavos = new int[tamanho];
        this.cliente = new int[tamanho];
        this.categoria = new byte[tamanho];
        this.tipo = new byte[tamanho];
        this.cidade = new byte[tamanho];
        this.dia = new short[tamanho];
        this.hora = new byte[tamanho];
    }

    /** Memória (em bytes) necessária para armazenar {@code n} transações. */
    public static long memoriaNecessaria(long n) {
        return n * BYTES_POR_TRANSACAO;
    }

    public int tamanho() {
        return tamanho;
    }

    /** Grava a transação de índice {@code i}. Usado pelo gerador de dados. */
    public void definir(int i, int valorCentavos, int cliente, int categoria, int tipo,
                        int cidade, int dia, int hora) {
        this.valorCentavos[i] = valorCentavos;
        this.cliente[i] = cliente;
        this.categoria[i] = (byte) categoria;
        this.tipo[i] = (byte) tipo;
        this.cidade[i] = (byte) cidade;
        this.dia[i] = (short) dia;
        this.hora[i] = (byte) hora;
    }

    // ---- Acesso direto aos arrays (usado pelos laços das análises) ----

    public int[] valores() {
        return valorCentavos;
    }

    public int[] clientes() {
        return cliente;
    }

    public byte[] categorias() {
        return categoria;
    }

    public byte[] tipos() {
        return tipo;
    }

    public byte[] cidades() {
        return cidade;
    }

    public short[] dias() {
        return dia;
    }

    public byte[] horas() {
        return hora;
    }

    // ---- Visão "linha a linha" (para exibição, exportação e testes) ----

    public static long idDoIndice(int indice) {
        return indice + 1L;
    }

    public static int indiceDoId(long id) {
        return (int) (id - 1);
    }

    public Transacao transacao(int i) {
        return new Transacao(
                idDoIndice(i),
                cliente[i],
                LocalDate.ofYearDay(ANO_BASE, dia[i] + 1),
                hora[i],
                valorCentavos[i],
                Categoria.deCodigo(categoria[i]),
                TipoTransacao.deCodigo(tipo[i]),
                Cidade.deCodigo(cidade[i]));
    }
}
