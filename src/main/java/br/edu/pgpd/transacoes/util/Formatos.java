package br.edu.pgpd.transacoes.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formatação de números no padrão brasileiro (ponto como separador de milhar, vírgula decimal).
 *
 * @author Kawan Mesquita, Caio Polidoro, Henrique Mosson, Vitor Abrahão
 */
public final class Formatos {

    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final DecimalFormatSymbols SIMBOLOS = DecimalFormatSymbols.getInstance(PT_BR);

    private Formatos() {
    }

    /** 10000000 -> "10.000.000". */
    public static String inteiro(long valor) {
        return new DecimalFormat("#,##0", SIMBOLOS).format(valor);
    }

    /** 123456 centavos -> "R$ 1.234,56". */
    public static String reais(long centavos) {
        return "R$ " + new DecimalFormat("#,##0.00", SIMBOLOS).format(centavos / 100.0);
    }

    /** Valor em reais (double) -> "R$ 1.234,56". */
    public static String reais(double reais) {
        return "R$ " + new DecimalFormat("#,##0.00", SIMBOLOS).format(reais);
    }

    /** Número com {@code casas} casas decimais: decimal(1.8432, 2) -> "1,84". */
    public static String decimal(double valor, int casas) {
        String padrao = casas == 0 ? "#,##0" : "#,##0." + "0".repeat(casas);
        return new DecimalFormat(padrao, SIMBOLOS).format(valor);
    }

    /** 0.92 -> "92,0%". */
    public static String percentual(double fracao) {
        return decimal(fracao * 100.0, 1) + "%";
    }

    /** Milissegundos com 1 casa: "4.632,1 ms". */
    public static String ms(double milissegundos) {
        return decimal(milissegundos, 1) + " ms";
    }

    /** Bytes em formato legível: "6,5 GB". */
    public static String bytes(long bytes) {
        double gb = bytes / (1024.0 * 1024 * 1024);
        if (gb >= 1) {
            return decimal(gb, 1) + " GB";
        }
        return decimal(bytes / (1024.0 * 1024), 1) + " MB";
    }

    /** Abreviação de tamanhos: 1000000 -> "1M", 250000000 -> "250M", 100000 -> "100K". */
    public static String abreviado(long n) {
        if (n >= 1_000_000_000 && n % 1_000_000_000 == 0) {
            return n / 1_000_000_000 + "B";
        }
        if (n >= 1_000_000 && n % 1_000_000 == 0) {
            return n / 1_000_000 + "M";
        }
        if (n >= 1_000 && n % 1_000 == 0) {
            return n / 1_000 + "K";
        }
        return Long.toString(n);
    }

    /** Converte "10M", "500k", "1.5M", "100000" em número. */
    public static long lerTamanho(String texto) {
        String t = texto.trim().toUpperCase(Locale.ROOT).replace("_", "");
        long multiplicador = 1;
        if (t.endsWith("K")) {
            multiplicador = 1_000;
        } else if (t.endsWith("M")) {
            multiplicador = 1_000_000;
        } else if (t.endsWith("B") || t.endsWith("G")) {
            multiplicador = 1_000_000_000;
        }
        if (multiplicador > 1) {
            t = t.substring(0, t.length() - 1);
        }
        return Math.round(Double.parseDouble(t) * multiplicador);
    }
}
