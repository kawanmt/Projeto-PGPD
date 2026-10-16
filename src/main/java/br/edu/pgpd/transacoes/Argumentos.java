package br.edu.pgpd.transacoes;

import br.edu.pgpd.transacoes.util.Formatos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Leitura simples de argumentos no formato {@code comando --chave valor --flag}.
 *
 * @author Kawan Mesquita, Caio Polidoro, Henrique Mosson, Vitor Abrahão
 */
final class Argumentos {

    private final String comando;
    private final Map<String, String> opcoes = new HashMap<>();

    Argumentos(String[] args) {
        this.comando = args.length == 0 ? "ajuda" : args[0].toLowerCase();
        for (int i = 1; i < args.length; i++) {
            String a = args[i];
            if (!a.startsWith("--")) {
                throw new IllegalArgumentException("Argumento inesperado: " + a);
            }
            String chave = a.substring(2);
            String valor = "true";
            int igual = chave.indexOf('=');
            if (igual >= 0) {
                valor = chave.substring(igual + 1);
                chave = chave.substring(0, igual);
            } else if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                valor = args[++i];
            }
            opcoes.put(chave.toLowerCase(), valor);
        }
    }

    String comando() {
        return comando;
    }

    boolean tem(String chave) {
        return opcoes.containsKey(chave);
    }

    String texto(String chave, String padrao) {
        return opcoes.getOrDefault(chave, padrao);
    }

    int inteiro(String chave, int padrao) {
        return tem(chave) ? (int) Formatos.lerTamanho(opcoes.get(chave)) : padrao;
    }

    long tamanho(String chave, long padrao) {
        return tem(chave) ? Formatos.lerTamanho(opcoes.get(chave)) : padrao;
    }

    <T> List<T> lista(String chave, List<T> padrao, Function<String, T> conversor) {
        if (!tem(chave)) {
            return padrao;
        }
        return Arrays.stream(opcoes.get(chave).split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).map(conversor).toList();
    }
}
