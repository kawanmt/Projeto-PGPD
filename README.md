# Análise Paralela de Transações Financeiras

**JCRPGPD — Programação Paralela e Distribuída · 1ª Avaliação (2º semestre/2026)**

Grupo: **Kawan Mesquita**, **Caio Polidoro**, **Henrique Mosson**, **Vitor Abrahão**

Aplicação Java que gera conjuntos sintéticos de transações financeiras (até centenas de milhões de
registros) e as analisa com **paralelismo de dados**. Cada análise existe em três versões:
**sequencial**, **ExecutorService** e **ExecutorService + afinidade de processador**
(com a [OpenHFT Affinity](https://github.com/OpenHFT/Java-Thread-Affinity)). Um módulo de benchmark
mede tempo, speedup e eficiência e gera tabelas e gráficos.

## Requisitos

- Java 21 ou superior (JDK)
- Não é preciso instalar o Maven: o projeto usa o **Maven Wrapper** (`mvnw.cmd` / `mvnw`), que
  baixa o Maven automaticamente na primeira execução.

## Como compilar e executar

```bat
mvnw.cmd package          :: compila, roda os testes e gera target\transacoes-paralelas.jar
run.cmd info              :: processador, topologia (núcleos/SMT), JVM e teste da afinidade
run.cmd demo              :: análises + Sequencial × Executor × Executor+Affinity (10 milhões)
run.cmd benchmark         :: experimento completo -> pasta resultados\
run.cmd amostra           :: exporta 1.000 transações para dados\amostra.csv
```

No Linux/macOS use `./mvnw` e `./run.sh`. O heap padrão é 12 GB; para mudar:
`set HEAP=8g` (Windows) ou `HEAP=8g ./run.sh ...`.

### Opções principais

| Comando | Opções |
|---|---|
| `demo` | `--n 10M` · `--threads 2,4,8` · `--estrategia compacta\|espalhada` · `--tarefas-por-thread 1` · `--repeticoes 3` |
| `benchmark` | `--tamanhos 100K,1M,10M,50M,100M,250M,500M` · `--threads 1,2,4,6,8,12` · `--repeticoes 5` · `--aquecimento 2` · `--analises agregacao,suspeitas,categorias,todas` · `--configuracoes executor,afinidade_compacta,afinidade_espalhada` · `--tarefas-por-thread 1` · `--granularidade 1,2,4,8,16,64` (ou `nao`) · `--saida resultados` · `--rapido` |
| `amostra` | `--n 1000` · `--saida dados/amostra.csv` · `--semente 20262` |

Tamanhos aceitam sufixos `K`, `M` e `B` (ex.: `250M`). Tamanhos que não cabem no heap ou na RAM
física livre são pulados automaticamente, com aviso (seção 10 do enunciado).

## Estrutura e contribuição individual

```
src/main/java/br/edu/pgpd/transacoes/
├── App.java, Demonstracao.java, Analises.java, Argumentos.java   integração (todos)
├── modelo/      Categoria, TipoTransacao, Estado, Cidade,
│                ConjuntoTransacoes (Structure of Arrays), Transacao   Kawan Mesquita
├── agregacao/   Aluno 1 — soma, média, maior, menor, desvio, histograma   Kawan Mesquita
├── dados/       GeradorTransacoes, Distribuicoes, ExportadorAmostra     Caio Polidoro
├── suspeitas/   Aluno 2 — regras R1..R5, score, top-10                  Caio Polidoro
├── paralelo/    Particionador, Intervalo                                Henrique Mosson
├── categorias/  Aluno 3 — por categoria, estado, mês, meio de pagamento Henrique Mosson
├── paralelo/    ExecutorFactory, AffinityThreadFactory, Topologia,
│                EstrategiaAfinidade, CpuAtual                           Vitor Abrahão
├── benchmark/   Aluno 4 — BenchmarkRunner, métricas, CSV/Markdown,
│                gráficos (XChart)                                       Vitor Abrahão
└── util/        Formatos                                                todos
src/test/java/   testes JUnit (equivalência paralelo × sequencial,
                 particionador, gerador)                                 todos
```

Cada módulo de análise segue o mesmo padrão:

| Classe | Papel |
|---|---|
| `XxxSequencial` | `calcular(conjunto)` e o algoritmo `processar(conjunto, inicio, fim)` |
| `XxxTarefa` | `Callable<ResultadoXxx>`: aplica o **mesmo** algoritmo a uma faixa |
| `XxxParalela` | divide (`Particionador`), submete as tarefas ao `ExecutorService` e **reduz** os parciais |
| `ResultadoXxx` | resultado parcial/final com `combinar(...)` (redução) e `equivalente(...)` (validação) |

A versão **com afinidade** é a mesma `XxxParalela` recebendo um executor criado por
`ExecutorFactory.criar(threads, EstrategiaAfinidade.COMPACTA|ESPALHADA, topologia)`.

## Dados sintéticos

- **Valor**: log-normal por categoria + 0,5% de cauda **Pareto** (transações atípicas)
- **Cliente**: **Zipf** (500 mil clientes; poucos concentram muitas transações)
- **Categoria / meio de pagamento / cidade**: **categórica ponderada**
- **Dia**: **uniforme** no ano de 2025
- **Hora**: **mistura de normais** (picos às 12h30 e 19h30) + ruído uniforme

A geração é determinística (semente fixa, um gerador por bloco de 65.536 transações),
independentemente do número de threads.

## Saídas do benchmark (`resultados/`)

- `resultados.csv`, `granularidade.csv`: todas as medições (separador `;`, vírgula decimal)
- `resultados.md`: tabelas prontas para o relatório
- `info-sistema.txt`: hardware, SO e JVM usados
- `*.png`: speedup, eficiência, tempo × threads, afinidade (barras), tempo × tamanho, granularidade

## Relatório técnico

- Google Docs: [Relatório PGPD: Análise Paralela de Transações Financeiras](https://docs.google.com/document/d/18vhI1VrDhHzN4-rTsQXbLfou-65YMbgfVOnE5BOCgfc/edit)
- Versão local com os gráficos embutidos: `docs/Relatorio-PGPD.html` (abrir no navegador; imprime em PDF)
- Log completo da execução do benchmark: `benchmark-log.txt`

## Principais resultados (Ryzen 5 5600GT, 6 núcleos / 12 CPUs lógicas)

| 500 milhões de transações, todas as análises | Tempo | Speedup | Eficiência |
|---|---:|---:|---:|
| Sequencial | 8.300 ms | 1,00 | 100% |
| Executor, 6 threads | 1.482 ms | 5,60 | 93% |
| Executor, 12 threads | 975 ms | 8,51 | 71% |
| Executor + Affinity compacta, 2 threads (CPUs 0,1 = mesmo núcleo) | 5.235 ms | 1,59 | 79% |
| Executor + Affinity espalhada, 2 threads (CPUs 0,2) | 4.103 ms | 2,02 | 101% |
