package br.edu.pgpd.transacoes.benchmark;

import br.edu.pgpd.transacoes.dados.GeradorTransacoes;

import java.nio.file.Path;
import java.util.List;

/**
 * Parâmetros de um experimento do benchmark.
 *
 * @param tamanhos          quantidades de transações a testar (em ordem crescente)
 * @param threads           quantidades de threads a testar
 * @param repeticoes        execuções medidas por configuração (usa-se a mediana)
 * @param aquecimento       execuções descartadas antes de medir (aquecimento do JIT)
 * @param analises          análises a medir
 * @param paralelas         configurações paralelas a comparar com o sequencial
 * @param tarefasPorThread  tarefas = threads × tarefasPorThread
 * @param granularidades    valores de tarefas por thread do experimento de granularidade (vazio = não executa)
 * @param saida             pasta onde ficam CSV, Markdown e gráficos
 * @param semente           semente do gerador de dados
 * @author Vitor Abrahão
 */
public record ConfiguracaoBenchmark(
        List<Long> tamanhos,
        List<Integer> threads,
        int repeticoes,
        int aquecimento,
        List<TipoAnalise> analises,
        List<Configuracao> paralelas,
        int tarefasPorThread,
        List<Integer> granularidades,
        Path saida,
        long semente) {

    public static ConfiguracaoBenchmark padrao() {
        return new ConfiguracaoBenchmark(
                List.of(100_000L, 1_000_000L, 10_000_000L, 50_000_000L, 100_000_000L, 250_000_000L, 500_000_000L),
                List.of(1, 2, 4, 6, 8, 12),
                5,
                2,
                List.of(TipoAnalise.values()),
                List.of(Configuracao.EXECUTOR, Configuracao.AFINIDADE_COMPACTA, Configuracao.AFINIDADE_ESPALHADA),
                1,
                List.of(1, 2, 4, 8, 16, 64),
                Path.of("resultados"),
                GeradorTransacoes.SEMENTE_PADRAO);
    }
}
