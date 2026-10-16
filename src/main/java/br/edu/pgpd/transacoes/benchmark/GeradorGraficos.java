package br.edu.pgpd.transacoes.benchmark;

import br.edu.pgpd.transacoes.util.Formatos;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Gera os gráficos PNG do benchmark com a biblioteca XChart.
 *
 * <p>Cores: paleta categórica validada para daltonismo, sempre na mesma ordem e sempre
 * associada à mesma entidade (ex.: "Executor" é sempre azul em todos os gráficos). As linhas de
 * referência (speedup ideal, tempo sequencial) usam cinza tracejado, não uma cor de série.
 *
 * @author Vitor Abrahão
 */
public final class GeradorGraficos {

    private static final int LARGURA = 1000;
    private static final int ALTURA = 600;

    /** Paleta categórica (ordem fixa). */
    private static final Color[] PALETA = {
            new Color(0x2a78d6), new Color(0xeb6834), new Color(0x1baf7a), new Color(0xeda100),
            new Color(0xe87ba4), new Color(0x008300), new Color(0x6250d6), new Color(0xe34948)};
    private static final Color REFERENCIA = new Color(0x8a8985);
    private static final Color FUNDO = new Color(0xfcfcfb);
    private static final Color TEXTO = new Color(0x0b0b0b);
    private static final Color TEXTO_SECUNDARIO = new Color(0x52514e);
    private static final Color GRADE = new Color(0xe6e5e1);

    /** Cor fixa por configuração e por análise. */
    private static final Map<Configuracao, Color> COR_CONFIG = Map.of(
            Configuracao.EXECUTOR, PALETA[0],
            Configuracao.AFINIDADE_COMPACTA, PALETA[1],
            Configuracao.AFINIDADE_ESPALHADA, PALETA[2]);
    private static final Map<TipoAnalise, Color> COR_ANALISE = Map.of(
            TipoAnalise.AGREGACAO, PALETA[0],
            TipoAnalise.SUSPEITAS, PALETA[1],
            TipoAnalise.CATEGORIAS, PALETA[2],
            TipoAnalise.TODAS, PALETA[3]);

    private final List<Medicao> medicoes;
    private final List<Medicao> granularidade;
    private final Path pasta;
    private final List<Path> gerados = new ArrayList<>();

    public GeradorGraficos(List<Medicao> medicoes, List<Medicao> granularidade, Path pasta) {
        this.medicoes = medicoes;
        this.granularidade = granularidade;
        this.pasta = pasta;
    }

    /** Gera todos os gráficos possíveis com as medições disponíveis e devolve os arquivos criados. */
    public List<Path> gerarTodos() throws IOException {
        System.setProperty("java.awt.headless", "true");
        TipoAnalise principal = analisePrincipal();
        List<Long> tamanhos = medicoes.stream().map(Medicao::tamanho).distinct().sorted().toList();
        if (tamanhos.isEmpty()) {
            return gerados;
        }
        long maior = tamanhos.getLast();

        speedupPorTamanho(principal, tamanhos);
        eficienciaPorTamanho(principal, tamanhos);
        tempoPorThreads(principal, maior);
        speedupPorConfiguracao(principal, maior);
        eficienciaPorConfiguracao(principal, maior);
        barrasAfinidade(principal, maior);
        speedupPorAnalise(maior);
        tempoPorTamanho(principal, tamanhos);
        granularidade();
        return gerados;
    }

    private TipoAnalise analisePrincipal() {
        return medicoes.stream().anyMatch(m -> m.analise() == TipoAnalise.TODAS)
                ? TipoAnalise.TODAS
                : medicoes.stream().map(Medicao::analise).findFirst().orElse(TipoAnalise.TODAS);
    }

    // ------------------------------------------------------------------ gráficos

    /** Escalabilidade: speedup × threads, uma linha por tamanho do conjunto (Executor sem afinidade). */
    private void speedupPorTamanho(TipoAnalise analise, List<Long> tamanhos) throws IOException {
        XYChart g = grafico("Speedup × threads por tamanho do conjunto — " + analise.rotulo() + " (Executor)",
                "Threads", "Speedup");
        int maxThreads = 1;
        int cor = 0;
        for (long tamanho : tamanhos) {
            List<Medicao> serie = filtrar(analise, tamanho, Configuracao.EXECUTOR);
            if (serie.isEmpty()) {
                continue;
            }
            maxThreads = Math.max(maxThreads, serie.getLast().threads());
            adicionarLinha(g, Formatos.abreviado(tamanho) + " transações", threads(serie),
                    serie.stream().mapToDouble(Medicao::speedup).toArray(), PALETA[cor++ % PALETA.length]);
        }
        linhaIdeal(g, maxThreads);
        salvar(g, "speedup_por_tamanho");
    }

    private void eficienciaPorTamanho(TipoAnalise analise, List<Long> tamanhos) throws IOException {
        XYChart g = grafico("Eficiência paralela × threads por tamanho — " + analise.rotulo() + " (Executor)",
                "Threads", "Eficiência (%)");
        int cor = 0;
        for (long tamanho : tamanhos) {
            List<Medicao> serie = filtrar(analise, tamanho, Configuracao.EXECUTOR);
            if (serie.isEmpty()) {
                continue;
            }
            adicionarLinha(g, Formatos.abreviado(tamanho) + " transações", threads(serie),
                    serie.stream().mapToDouble(m -> m.eficiencia() * 100).toArray(), PALETA[cor++ % PALETA.length]);
        }
        g.getStyler().setYAxisMin(0.0);
        salvar(g, "eficiencia_por_tamanho");
    }

    /** Tempo × threads no maior conjunto: sequencial (referência) e as três configurações paralelas. */
    private void tempoPorThreads(TipoAnalise analise, long tamanho) throws IOException {
        XYChart g = grafico("Tempo de execução × threads — " + analise.rotulo() + " — "
                + Formatos.inteiro(tamanho) + " transações", "Threads", "Tempo mediano (ms)");
        int maxThreads = 1;
        for (Configuracao cfg : COR_CONFIG.keySet().stream().sorted().toList()) {
            List<Medicao> serie = filtrar(analise, tamanho, cfg);
            if (serie.isEmpty()) {
                continue;
            }
            maxThreads = Math.max(maxThreads, serie.getLast().threads());
            adicionarLinha(g, cfg.rotulo(), threads(serie),
                    serie.stream().mapToDouble(Medicao::mediana).toArray(), COR_CONFIG.get(cfg));
        }
        Optional<Medicao> seq = filtrar(analise, tamanho, Configuracao.SEQUENCIAL).stream().findFirst();
        if (seq.isPresent()) {
            double t = seq.get().mediana();
            XYSeries s = g.addSeries("Sequencial", new double[]{1, maxThreads}, new double[]{t, t});
            estiloReferencia(s);
        }
        g.getStyler().setYAxisMin(0.0);
        salvar(g, "tempo_threads_" + Formatos.abreviado(tamanho));
    }

    /** Sem afinidade × com afinidade (compacta e espalhada): speedup no maior conjunto. */
    private void speedupPorConfiguracao(TipoAnalise analise, long tamanho) throws IOException {
        XYChart g = grafico("Speedup: sem afinidade × com afinidade — " + analise.rotulo() + " — "
                + Formatos.inteiro(tamanho) + " transações", "Threads", "Speedup");
        int maxThreads = 1;
        for (Configuracao cfg : COR_CONFIG.keySet().stream().sorted().toList()) {
            List<Medicao> serie = filtrar(analise, tamanho, cfg);
            if (serie.isEmpty()) {
                continue;
            }
            maxThreads = Math.max(maxThreads, serie.getLast().threads());
            adicionarLinha(g, cfg.rotulo(), threads(serie),
                    serie.stream().mapToDouble(Medicao::speedup).toArray(), COR_CONFIG.get(cfg));
        }
        linhaIdeal(g, maxThreads);
        salvar(g, "speedup_configuracoes_" + Formatos.abreviado(tamanho));
    }

    private void eficienciaPorConfiguracao(TipoAnalise analise, long tamanho) throws IOException {
        XYChart g = grafico("Eficiência: sem afinidade × com afinidade — " + analise.rotulo() + " — "
                + Formatos.inteiro(tamanho) + " transações", "Threads", "Eficiência (%)");
        for (Configuracao cfg : COR_CONFIG.keySet().stream().sorted().toList()) {
            List<Medicao> serie = filtrar(analise, tamanho, cfg);
            if (serie.isEmpty()) {
                continue;
            }
            adicionarLinha(g, cfg.rotulo(), threads(serie),
                    serie.stream().mapToDouble(m -> m.eficiencia() * 100).toArray(), COR_CONFIG.get(cfg));
        }
        g.getStyler().setYAxisMin(0.0);
        salvar(g, "eficiencia_configuracoes_" + Formatos.abreviado(tamanho));
    }

    /** Barras agrupadas: tempo de cada configuração para cada quantidade de threads. */
    private void barrasAfinidade(TipoAnalise analise, long tamanho) throws IOException {
        CategoryChart g = new CategoryChartBuilder().width(LARGURA).height(ALTURA)
                .title("Tempo por configuração — " + analise.rotulo() + " — " + Formatos.inteiro(tamanho) + " transações")
                .xAxisTitle("Threads").yAxisTitle("Tempo mediano (ms)").build();
        estilizar(g);
        g.getStyler().setAvailableSpaceFill(0.8);
        g.getStyler().setOverlapped(false);
        List<String> rotulosThreads = filtrar(analise, tamanho, Configuracao.EXECUTOR).stream()
                .map(m -> Integer.toString(m.threads())).toList();
        if (rotulosThreads.isEmpty()) {
            return;
        }
        for (Configuracao cfg : COR_CONFIG.keySet().stream().sorted().toList()) {
            List<Double> tempos = new ArrayList<>();
            for (String t : rotulosThreads) {
                tempos.add(filtrar(analise, tamanho, cfg).stream()
                        .filter(m -> m.threads() == Integer.parseInt(t))
                        .map(Medicao::mediana).findFirst().orElse(0.0));
            }
            if (tempos.stream().allMatch(t -> t == 0.0)) {
                continue;
            }
            CategorySeries s = g.addSeries(cfg.rotulo(), rotulosThreads, tempos);
            s.setFillColor(COR_CONFIG.get(cfg));
        }
        salvar(g, "afinidade_barras_" + Formatos.abreviado(tamanho));
    }

    /** Speedup de cada análise individual no maior conjunto (Executor sem afinidade). */
    private void speedupPorAnalise(long tamanho) throws IOException {
        XYChart g = grafico("Speedup por análise — " + Formatos.inteiro(tamanho) + " transações (Executor)",
                "Threads", "Speedup");
        int maxThreads = 1;
        for (TipoAnalise a : TipoAnalise.values()) {
            List<Medicao> serie = filtrar(a, tamanho, Configuracao.EXECUTOR);
            if (serie.isEmpty()) {
                continue;
            }
            maxThreads = Math.max(maxThreads, serie.getLast().threads());
            adicionarLinha(g, a.rotulo(), threads(serie),
                    serie.stream().mapToDouble(Medicao::speedup).toArray(), COR_ANALISE.get(a));
        }
        linhaIdeal(g, maxThreads);
        salvar(g, "speedup_por_analise_" + Formatos.abreviado(tamanho));
    }

    /** Tempo × tamanho (escalas logarítmicas): sequencial e melhor configuração com o máximo de threads. */
    private void tempoPorTamanho(TipoAnalise analise, List<Long> tamanhos) throws IOException {
        XYChart g = grafico("Tempo × tamanho do conjunto — " + analise.rotulo() + " (escala log)",
                "Transações", "Tempo mediano (ms)");
        g.getStyler().setXAxisLogarithmic(true);
        g.getStyler().setYAxisLogarithmic(true);
        g.getStyler().setXAxisDecimalPattern("#,##0");

        List<Configuracao> configs = new ArrayList<>(List.of(Configuracao.SEQUENCIAL));
        configs.addAll(COR_CONFIG.keySet().stream().sorted().toList());
        for (Configuracao cfg : configs) {
            List<Double> xs = new ArrayList<>();
            List<Double> ys = new ArrayList<>();
            String rotulo = cfg.rotulo();
            for (long tamanho : tamanhos) {
                Optional<Medicao> m = filtrar(analise, tamanho, cfg).stream()
                        .max(Comparator.comparingInt(Medicao::threads));
                if (m.isPresent()) {
                    xs.add((double) tamanho);
                    ys.add(m.get().mediana());
                    if (cfg != Configuracao.SEQUENCIAL) {
                        rotulo = cfg.rotulo() + " (" + m.get().threads() + " threads)";
                    }
                }
            }
            if (xs.isEmpty()) {
                continue;
            }
            XYSeries s = g.addSeries(rotulo, xs, ys);
            if (cfg == Configuracao.SEQUENCIAL) {
                s.setLineColor(TEXTO_SECUNDARIO);
                s.setMarkerColor(TEXTO_SECUNDARIO);
                s.setLineWidth(2f);
                s.setMarker(SeriesMarkers.SQUARE);
            } else {
                estiloSerie(s, COR_CONFIG.get(cfg));
            }
        }
        salvar(g, "tempo_por_tamanho");
    }

    /** Experimento de granularidade: tempo × tarefas por thread. */
    private void granularidade() throws IOException {
        if (granularidade.isEmpty()) {
            return;
        }
        Medicao primeira = granularidade.getFirst();
        XYChart g = grafico("Granularidade: tempo × tarefas por thread — " + primeira.threads() + " threads — "
                + Formatos.inteiro(primeira.tamanho()) + " transações", "Tarefas por thread (escala log)",
                "Tempo mediano (ms)");
        g.getStyler().setXAxisLogarithmic(true);
        g.getStyler().setXAxisDecimalPattern("#,##0");
        for (Configuracao cfg : COR_CONFIG.keySet().stream().sorted().toList()) {
            List<Medicao> serie = granularidade.stream().filter(m -> m.configuracao() == cfg).toList();
            if (serie.isEmpty()) {
                continue;
            }
            adicionarLinha(g, cfg.rotulo(),
                    serie.stream().mapToDouble(m -> (double) m.tarefas() / m.threads()).toArray(),
                    serie.stream().mapToDouble(Medicao::mediana).toArray(), COR_CONFIG.get(cfg));
        }
        g.getStyler().setYAxisMin(0.0);
        salvar(g, "granularidade");
    }

    // ------------------------------------------------------------------ utilitários

    private List<Medicao> filtrar(TipoAnalise analise, long tamanho, Configuracao cfg) {
        return medicoes.stream()
                .filter(m -> m.analise() == analise && m.tamanho() == tamanho && m.configuracao() == cfg)
                .sorted(Comparator.comparingInt(Medicao::threads))
                .toList();
    }

    private static double[] threads(List<Medicao> serie) {
        return serie.stream().mapToDouble(Medicao::threads).toArray();
    }

    private XYChart grafico(String titulo, String eixoX, String eixoY) {
        XYChart g = new XYChartBuilder().width(LARGURA).height(ALTURA)
                .title(titulo).xAxisTitle(eixoX).yAxisTitle(eixoY).build();
        estilizar(g);
        g.getStyler().setDefaultSeriesRenderStyle(org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Line);
        g.getStyler().setXAxisDecimalPattern("#0");
        g.getStyler().setYAxisDecimalPattern("#,##0.##");
        return g;
    }

    private static void estilizar(Chart<?, ?> grafico) {
        Styler st = grafico.getStyler();
        st.setChartBackgroundColor(FUNDO);
        st.setChartFontColor(TEXTO);
        st.setChartTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        st.setChartTitleBoxVisible(false);
        st.setLegendPosition(Styler.LegendPosition.OutsideS);
        st.setLegendLayout(Styler.LegendLayout.Horizontal);
        st.setLegendBorderColor(FUNDO);
        st.setLegendBackgroundColor(FUNDO);
        st.setLegendFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        st.setChartPadding(16);
        if (st instanceof org.knowm.xchart.style.AxesChartStyler ax) {
            ax.setPlotBackgroundColor(FUNDO);
            ax.setPlotBorderVisible(false);
            ax.setPlotGridLinesColor(GRADE);
            ax.setPlotGridLinesStroke(new BasicStroke(1f));
            ax.setAxisTickLabelsColor(TEXTO_SECUNDARIO);
            ax.setAxisTickMarksColor(GRADE);
            ax.setAxisTitleFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            ax.setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            ax.setMarkerSize(9);
        }
    }

    private static void adicionarLinha(XYChart g, String nome, double[] x, double[] y, Color cor) {
        estiloSerie(g.addSeries(nome, x, y), cor);
    }

    private static void estiloSerie(XYSeries s, Color cor) {
        s.setLineColor(cor);
        s.setMarkerColor(cor);
        s.setLineWidth(2f);
        s.setMarker(SeriesMarkers.CIRCLE);
    }

    private static void estiloReferencia(XYSeries s) {
        s.setLineColor(REFERENCIA);
        s.setLineStyle(SeriesLines.DASH_DASH);
        s.setLineWidth(1.5f);
        s.setMarker(SeriesMarkers.NONE);
    }

    private static void linhaIdeal(XYChart g, int maxThreads) {
        estiloReferencia(g.addSeries("Speedup ideal (linear)", new double[]{1, maxThreads},
                new double[]{1, maxThreads}));
    }

    private void salvar(Chart<?, ?> grafico, String nome) throws IOException {
        Path arquivo = pasta.resolve(nome + ".png");
        BitmapEncoder.saveBitmap(grafico, arquivo.toString(), BitmapEncoder.BitmapFormat.PNG);
        gerados.add(arquivo);
    }
}
