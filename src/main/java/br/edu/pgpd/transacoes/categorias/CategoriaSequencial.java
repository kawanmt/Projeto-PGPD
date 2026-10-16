package br.edu.pgpd.transacoes.categorias;

import br.edu.pgpd.transacoes.modelo.Cidade;
import br.edu.pgpd.transacoes.modelo.ConjuntoTransacoes;

/**
 * <b>Versão sequencial</b> das estatísticas por categoria (Análise 4), por estado, por mês e
 * por meio de pagamento.
 *
 * <p>{@link #processar} é o algoritmo sobre uma faixa {@code [inicio, fim)}; a versão paralela
 * ({@link CategoriaTarefa}) aplica o mesmo algoritmo a cada parte dos dados.
 *
 * @author Henrique Mosson
 */
public final class CategoriaSequencial {

    private CategoriaSequencial() {
    }

    public static ResultadoCategorias calcular(ConjuntoTransacoes conjunto) {
        return processar(conjunto, 0, conjunto.tamanho());
    }

    public static ResultadoCategorias processar(ConjuntoTransacoes conjunto, int inicio, int fim) {
        final int[] valores = conjunto.valores();
        final byte[] categorias = conjunto.categorias();
        final byte[] cidades = conjunto.cidades();
        final short[] dias = conjunto.dias();
        final byte[] tipos = conjunto.tipos();
        final byte[] estadoDaCidade = Cidade.ESTADO_DA_CIDADE;
        final byte[] mesDoDia = ConjuntoTransacoes.MES_DO_DIA;

        ResultadoCategorias r = new ResultadoCategorias();
        final long[] quantidade = r.quantidade;
        final long[] total = r.totalCentavos;
        final int[] maior = r.maior;
        final int[] menor = r.menor;
        final long[] qtdEstado = r.quantidadeEstado;
        final long[] totEstado = r.totalEstado;
        final long[] qtdMes = r.quantidadeMes;
        final long[] totMes = r.totalMes;
        final long[] qtdTipo = r.quantidadeTipo;
        final long[] totTipo = r.totalTipo;

        for (int i = inicio; i < fim; i++) {
            int v = valores[i];
            int c = categorias[i];

            quantidade[c]++;
            total[c] += v;
            if (v > maior[c]) {
                maior[c] = v;
            }
            if (v < menor[c]) {
                menor[c] = v;
            }

            int estado = estadoDaCidade[cidades[i]];
            qtdEstado[estado]++;
            totEstado[estado] += v;

            int mes = mesDoDia[dias[i]];
            qtdMes[mes]++;
            totMes[mes] += v;

            int tipo = tipos[i];
            qtdTipo[tipo]++;
            totTipo[tipo] += v;
        }
        return r;
    }
}
