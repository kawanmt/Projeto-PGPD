# Resultados do benchmark

```
Data/hora           : 16/10/2026 15:52:27
Processador         : AMD Ryzen 5 5600GT with Radeon Graphics
                      CPUs lógicas: 12 | Núcleos físicos: 6 | SMT: sim
                        Núcleo 0 -> CPUs [0, 1]
                        Núcleo 1 -> CPUs [2, 3]
                        Núcleo 2 -> CPUs [4, 5]
                        Núcleo 3 -> CPUs [6, 7]
                        Núcleo 4 -> CPUs [8, 9]
                        Núcleo 5 -> CPUs [10, 11]
Sistema operacional : Windows 11 10.0 (amd64)
JVM                 : OpenJDK 64-Bit Server VM 21.0.12.1 (Eclipse Adoptium)
Heap máximo (-Xmx)  : 10,7 GB
Coletor de lixo     : PS MarkSweep, PS Scavenge
Biblioteca afinidade: OpenHFT WindowsJNAAffinity (máscara do processo: {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
Argumentos da JVM   : -Xmx12g -XX:+UseParallelGC -Dstdout.encoding=UTF-8 -Dfile.encoding=UTF-8
```

## Agregação financeira

### 100.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 0,8 ms | 0,0 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 0,9 ms | 0,0 ms | 0,90 | 90,4% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 0,9 ms | 0,0 ms | 0,96 | 95,9% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 0,9 ms | 0,0 ms | 0,94 | 94,3% | ✔ |
| Executor | 2 | 2 | SO | 0,5 ms | 0,0 ms | 1,70 | 85,1% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 0,5 ms | 0,0 ms | 1,74 | 86,8% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 0,5 ms | 0,0 ms | 1,64 | 82,0% | ✔ |
| Executor | 4 | 4 | SO | 0,3 ms | 0,0 ms | 2,95 | 73,7% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 0,4 ms | 0,1 ms | 1,86 | 46,6% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 0,4 ms | 0,1 ms | 2,34 | 58,5% | ✔ |
| Executor | 6 | 6 | SO | 0,2 ms | 0,0 ms | 3,77 | 62,8% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 0,2 ms | 0,0 ms | 3,64 | 60,7% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 0,4 ms | 0,1 ms | 2,09 | 34,8% | ✔ |
| Executor | 8 | 8 | SO | 0,2 ms | 0,0 ms | 4,25 | 53,1% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 0,4 ms | 0,1 ms | 2,13 | 26,6% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 0,3 ms | 0,1 ms | 2,94 | 36,7% | ✔ |
| Executor | 12 | 12 | SO | 0,2 ms | 0,0 ms | 4,15 | 34,6% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 0,3 ms | 0,1 ms | 2,66 | 22,2% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 0,3 ms | 0,3 ms | 2,57 | 21,4% | ✔ |

### 1.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 8,5 ms | 0,0 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 8,6 ms | 0,3 ms | 0,99 | 98,6% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 8,7 ms | 0,3 ms | 0,98 | 97,7% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 8,6 ms | 0,1 ms | 0,98 | 98,2% | ✔ |
| Executor | 2 | 2 | SO | 4,3 ms | 0,0 ms | 1,97 | 98,5% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 4,5 ms | 0,0 ms | 1,89 | 94,3% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 4,3 ms | 0,1 ms | 1,96 | 98,0% | ✔ |
| Executor | 4 | 4 | SO | 2,2 ms | 0,0 ms | 3,89 | 97,3% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 2,3 ms | 0,1 ms | 3,68 | 92,0% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 2,2 ms | 0,0 ms | 3,85 | 96,2% | ✔ |
| Executor | 6 | 6 | SO | 1,5 ms | 0,0 ms | 5,74 | 95,6% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 1,6 ms | 0,0 ms | 5,40 | 90,0% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 1,5 ms | 0,0 ms | 5,69 | 94,8% | ✔ |
| Executor | 8 | 8 | SO | 1,2 ms | 0,0 ms | 7,17 | 89,6% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 1,2 ms | 14,3 ms | 6,78 | 84,8% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 1,2 ms | 0,0 ms | 7,31 | 91,4% | ✔ |
| Executor | 12 | 12 | SO | 0,8 ms | 0,0 ms | 10,32 | 86,0% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 0,8 ms | 0,0 ms | 10,37 | 86,4% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 0,8 ms | 0,2 ms | 10,25 | 85,4% | ✔ |

### 10.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 84,5 ms | 0,1 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 84,6 ms | 0,3 ms | 1,00 | 99,9% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 84,9 ms | 0,1 ms | 1,00 | 99,6% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 85,3 ms | 1,4 ms | 0,99 | 99,1% | ✔ |
| Executor | 2 | 2 | SO | 42,4 ms | 0,0 ms | 1,99 | 99,7% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 44,1 ms | 0,1 ms | 1,92 | 95,9% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 42,4 ms | 0,1 ms | 1,99 | 99,7% | ✔ |
| Executor | 4 | 4 | SO | 21,8 ms | 0,3 ms | 3,89 | 97,1% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 22,3 ms | 0,1 ms | 3,80 | 94,9% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 21,5 ms | 0,1 ms | 3,93 | 98,3% | ✔ |
| Executor | 6 | 6 | SO | 14,8 ms | 0,2 ms | 5,71 | 95,2% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 15,0 ms | 0,0 ms | 5,62 | 93,7% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 14,5 ms | 0,0 ms | 5,83 | 97,2% | ✔ |
| Executor | 8 | 8 | SO | 11,4 ms | 0,0 ms | 7,44 | 93,0% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 11,5 ms | 0,0 ms | 7,37 | 92,1% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 11,4 ms | 0,7 ms | 7,39 | 92,4% | ✔ |
| Executor | 12 | 12 | SO | 8,4 ms | 1,3 ms | 10,01 | 83,4% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 7,8 ms | 0,4 ms | 10,83 | 90,2% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 8,0 ms | 0,9 ms | 10,54 | 87,9% | ✔ |

### 50.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 424,2 ms | 1,3 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 422,6 ms | 2,1 ms | 1,00 | 100,4% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 425,3 ms | 2,0 ms | 1,00 | 99,7% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 426,8 ms | 1,5 ms | 0,99 | 99,4% | ✔ |
| Executor | 2 | 2 | SO | 211,6 ms | 1,2 ms | 2,00 | 100,2% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 222,0 ms | 2,0 ms | 1,91 | 95,5% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 212,9 ms | 6,7 ms | 1,99 | 99,6% | ✔ |
| Executor | 4 | 4 | SO | 108,8 ms | 0,6 ms | 3,90 | 97,5% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 111,6 ms | 0,5 ms | 3,80 | 95,0% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 108,6 ms | 0,2 ms | 3,91 | 97,7% | ✔ |
| Executor | 6 | 6 | SO | 73,9 ms | 0,7 ms | 5,74 | 95,7% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 75,6 ms | 0,4 ms | 5,61 | 93,5% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 74,8 ms | 5,8 ms | 5,67 | 94,5% | ✔ |
| Executor | 8 | 8 | SO | 57,5 ms | 0,1 ms | 7,37 | 92,2% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 57,2 ms | 0,1 ms | 7,41 | 92,6% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 57,5 ms | 0,1 ms | 7,38 | 92,2% | ✔ |
| Executor | 12 | 12 | SO | 38,8 ms | 0,5 ms | 10,93 | 91,1% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 39,2 ms | 0,3 ms | 10,82 | 90,2% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 44,7 ms | 4,5 ms | 9,49 | 79,1% | ✔ |

### 100.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 848,7 ms | 1,4 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 844,2 ms | 2,7 ms | 1,01 | 100,5% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 844,4 ms | 2,3 ms | 1,01 | 100,5% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 856,3 ms | 3,3 ms | 0,99 | 99,1% | ✔ |
| Executor | 2 | 2 | SO | 431,6 ms | 1,3 ms | 1,97 | 98,3% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 446,9 ms | 1,7 ms | 1,90 | 94,9% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 434,6 ms | 1,8 ms | 1,95 | 97,6% | ✔ |
| Executor | 4 | 4 | SO | 221,2 ms | 2,2 ms | 3,84 | 95,9% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 230,1 ms | 1,0 ms | 3,69 | 92,2% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 229,9 ms | 8,8 ms | 3,69 | 92,3% | ✔ |
| Executor | 6 | 6 | SO | 153,3 ms | 0,5 ms | 5,54 | 92,3% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 163,2 ms | 4,7 ms | 5,20 | 86,7% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 156,1 ms | 5,5 ms | 5,44 | 90,6% | ✔ |
| Executor | 8 | 8 | SO | 116,5 ms | 0,2 ms | 7,29 | 91,1% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 118,1 ms | 9,2 ms | 7,19 | 89,8% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 118,7 ms | 2,4 ms | 7,15 | 89,4% | ✔ |
| Executor | 12 | 12 | SO | 107,2 ms | 7,9 ms | 7,92 | 66,0% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 94,4 ms | 6,8 ms | 8,99 | 74,9% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 91,4 ms | 9,2 ms | 9,28 | 77,3% | ✔ |

### 250.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 2.149,9 ms | 21,4 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 2.126,1 ms | 8,8 ms | 1,01 | 101,1% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 2.128,4 ms | 6,0 ms | 1,01 | 101,0% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 2.131,3 ms | 9,3 ms | 1,01 | 100,9% | ✔ |
| Executor | 2 | 2 | SO | 1.078,0 ms | 3,9 ms | 1,99 | 99,7% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 1.102,3 ms | 5,5 ms | 1,95 | 97,5% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 1.069,4 ms | 4,4 ms | 2,01 | 100,5% | ✔ |
| Executor | 4 | 4 | SO | 552,4 ms | 3,1 ms | 3,89 | 97,3% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 550,5 ms | 3,5 ms | 3,91 | 97,6% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 552,6 ms | 17,5 ms | 3,89 | 97,3% | ✔ |
| Executor | 6 | 6 | SO | 376,0 ms | 3,2 ms | 5,72 | 95,3% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 384,1 ms | 2,1 ms | 5,60 | 93,3% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 374,1 ms | 13,4 ms | 5,75 | 95,8% | ✔ |
| Executor | 8 | 8 | SO | 293,8 ms | 1,1 ms | 7,32 | 91,5% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 327,6 ms | 13,6 ms | 6,56 | 82,0% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 303,1 ms | 15,1 ms | 7,09 | 88,6% | ✔ |
| Executor | 12 | 12 | SO | 229,7 ms | 16,8 ms | 9,36 | 78,0% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 222,1 ms | 18,5 ms | 9,68 | 80,7% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 213,8 ms | 40,6 ms | 10,05 | 83,8% | ✔ |

### 500.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 4.341,7 ms | 9,8 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 4.341,7 ms | 7,6 ms | 1,00 | 100,0% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 4.254,9 ms | 10,6 ms | 1,02 | 102,0% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 4.248,3 ms | 7,0 ms | 1,02 | 102,2% | ✔ |
| Executor | 2 | 2 | SO | 2.132,4 ms | 44,3 ms | 2,04 | 101,8% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 2.194,3 ms | 7,3 ms | 1,98 | 98,9% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 2.129,1 ms | 4,7 ms | 2,04 | 102,0% | ✔ |
| Executor | 4 | 4 | SO | 1.094,8 ms | 2,6 ms | 3,97 | 99,1% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 1.104,6 ms | 2,7 ms | 3,93 | 98,3% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 1.101,1 ms | 13,7 ms | 3,94 | 98,6% | ✔ |
| Executor | 6 | 6 | SO | 749,8 ms | 3,1 ms | 5,79 | 96,5% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 761,8 ms | 1,0 ms | 5,70 | 95,0% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 750,0 ms | 2,4 ms | 5,79 | 96,5% | ✔ |
| Executor | 8 | 8 | SO | 581,8 ms | 1,4 ms | 7,46 | 93,3% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 578,8 ms | 2,2 ms | 7,50 | 93,8% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 583,6 ms | 0,4 ms | 7,44 | 93,0% | ✔ |
| Executor | 12 | 12 | SO | 396,8 ms | 19,9 ms | 10,94 | 91,2% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 409,5 ms | 30,1 ms | 10,60 | 88,3% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 400,0 ms | 44,8 ms | 10,85 | 90,4% | ✔ |

## Transações suspeitas

### 100.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 0,3 ms | 0,1 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 0,3 ms | 0,0 ms | 1,00 | 100,1% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 0,4 ms | 0,1 ms | 0,77 | 77,2% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 0,3 ms | 0,0 ms | 1,05 | 104,7% | ✔ |
| Executor | 2 | 2 | SO | 2,6 ms | 0,8 ms | 0,12 | 6,2% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 0,4 ms | 0,0 ms | 0,74 | 37,0% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 0,6 ms | 0,0 ms | 0,60 | 30,0% | ✔ |
| Executor | 4 | 4 | SO | 1,0 ms | 0,2 ms | 0,32 | 8,1% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 0,1 ms | 0,0 ms | 2,30 | 57,4% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 0,2 ms | 0,1 ms | 1,73 | 43,3% | ✔ |
| Executor | 6 | 6 | SO | 0,1 ms | 0,0 ms | 2,45 | 40,8% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 0,2 ms | 0,0 ms | 1,60 | 26,6% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 0,2 ms | 0,0 ms | 1,92 | 32,0% | ✔ |
| Executor | 8 | 8 | SO | 0,1 ms | 0,0 ms | 2,55 | 31,9% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 0,3 ms | 0,1 ms | 1,11 | 13,9% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 0,3 ms | 0,1 ms | 1,13 | 14,1% | ✔ |
| Executor | 12 | 12 | SO | 0,1 ms | 0,0 ms | 2,56 | 21,3% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 0,3 ms | 0,4 ms | 1,05 | 8,7% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 0,4 ms | 0,3 ms | 0,83 | 6,9% | ✔ |

### 1.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 2,7 ms | 0,0 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 2,7 ms | 0,0 ms | 1,00 | 100,2% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 2,7 ms | 0,1 ms | 1,00 | 100,0% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 2,8 ms | 0,0 ms | 0,99 | 98,7% | ✔ |
| Executor | 2 | 2 | SO | 1,4 ms | 0,0 ms | 1,98 | 99,1% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 2,2 ms | 0,0 ms | 1,23 | 61,5% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 1,4 ms | 0,0 ms | 2,00 | 99,9% | ✔ |
| Executor | 4 | 4 | SO | 0,7 ms | 0,2 ms | 3,84 | 95,9% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 1,1 ms | 0,1 ms | 2,40 | 60,1% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 0,7 ms | 0,0 ms | 3,85 | 96,2% | ✔ |
| Executor | 6 | 6 | SO | 0,8 ms | 0,1 ms | 3,50 | 58,4% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 0,8 ms | 0,0 ms | 3,52 | 58,7% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 0,5 ms | 0,0 ms | 5,48 | 91,3% | ✔ |
| Executor | 8 | 8 | SO | 0,6 ms | 0,0 ms | 4,59 | 57,3% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 0,6 ms | 0,0 ms | 4,40 | 55,1% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 0,6 ms | 0,0 ms | 4,41 | 55,1% | ✔ |
| Executor | 12 | 12 | SO | 0,5 ms | 0,0 ms | 6,06 | 50,5% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 0,5 ms | 0,0 ms | 5,99 | 49,9% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 0,5 ms | 0,0 ms | 5,97 | 49,8% | ✔ |

### 10.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 29,1 ms | 0,2 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 29,2 ms | 0,1 ms | 1,00 | 99,5% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 29,2 ms | 0,1 ms | 0,99 | 99,5% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 29,1 ms | 0,2 ms | 1,00 | 99,9% | ✔ |
| Executor | 2 | 2 | SO | 14,7 ms | 0,2 ms | 1,98 | 99,1% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 23,4 ms | 0,1 ms | 1,24 | 62,1% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 14,8 ms | 0,2 ms | 1,96 | 97,9% | ✔ |
| Executor | 4 | 4 | SO | 7,6 ms | 0,2 ms | 3,85 | 96,2% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 12,0 ms | 0,1 ms | 2,43 | 60,6% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 7,5 ms | 0,2 ms | 3,86 | 96,6% | ✔ |
| Executor | 6 | 6 | SO | 6,9 ms | 1,0 ms | 4,22 | 70,3% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 8,0 ms | 0,1 ms | 3,63 | 60,4% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 5,1 ms | 0,3 ms | 5,70 | 95,1% | ✔ |
| Executor | 8 | 8 | SO | 6,1 ms | 0,0 ms | 4,77 | 59,6% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 6,1 ms | 0,0 ms | 4,73 | 59,1% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 6,1 ms | 0,0 ms | 4,74 | 59,3% | ✔ |
| Executor | 12 | 12 | SO | 4,4 ms | 0,7 ms | 6,64 | 55,3% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 4,2 ms | 0,0 ms | 6,94 | 57,9% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 4,2 ms | 0,0 ms | 6,93 | 57,8% | ✔ |

### 50.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 145,8 ms | 0,3 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 146,6 ms | 51,3 ms | 0,99 | 99,5% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 145,8 ms | 0,6 ms | 1,00 | 100,0% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 146,0 ms | 0,6 ms | 1,00 | 99,9% | ✔ |
| Executor | 2 | 2 | SO | 73,2 ms | 0,5 ms | 1,99 | 99,6% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 116,4 ms | 0,1 ms | 1,25 | 62,7% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 72,8 ms | 0,3 ms | 2,00 | 100,2% | ✔ |
| Executor | 4 | 4 | SO | 37,9 ms | 0,3 ms | 3,85 | 96,3% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 58,5 ms | 4,7 ms | 2,49 | 62,3% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 37,6 ms | 0,2 ms | 3,88 | 97,0% | ✔ |
| Executor | 6 | 6 | SO | 25,6 ms | 0,1 ms | 5,70 | 94,9% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 40,2 ms | 0,2 ms | 3,63 | 60,4% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 25,6 ms | 0,1 ms | 5,69 | 94,9% | ✔ |
| Executor | 8 | 8 | SO | 30,4 ms | 0,6 ms | 4,80 | 60,0% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 30,8 ms | 0,1 ms | 4,74 | 59,2% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 30,6 ms | 0,1 ms | 4,77 | 59,6% | ✔ |
| Executor | 12 | 12 | SO | 21,0 ms | 0,5 ms | 6,93 | 57,8% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 21,1 ms | 0,3 ms | 6,91 | 57,6% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 21,1 ms | 0,5 ms | 6,91 | 57,6% | ✔ |

### 100.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 295,1 ms | 1,2 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 294,4 ms | 1,9 ms | 1,00 | 100,2% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 299,5 ms | 2,0 ms | 0,99 | 98,5% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 296,1 ms | 2,6 ms | 1,00 | 99,7% | ✔ |
| Executor | 2 | 2 | SO | 153,1 ms | 4,4 ms | 1,93 | 96,4% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 238,9 ms | 0,7 ms | 1,24 | 61,8% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 150,8 ms | 3,1 ms | 1,96 | 97,9% | ✔ |
| Executor | 4 | 4 | SO | 104,2 ms | 12,5 ms | 2,83 | 70,8% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 122,3 ms | 2,1 ms | 2,41 | 60,3% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 84,9 ms | 9,9 ms | 3,48 | 86,9% | ✔ |
| Executor | 6 | 6 | SO | 71,7 ms | 2,0 ms | 4,12 | 68,6% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 82,8 ms | 1,7 ms | 3,56 | 59,4% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 68,0 ms | 2,5 ms | 4,34 | 72,3% | ✔ |
| Executor | 8 | 8 | SO | 59,6 ms | 1,3 ms | 4,95 | 61,9% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 74,4 ms | 5,6 ms | 3,97 | 49,6% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 64,5 ms | 1,8 ms | 4,57 | 57,2% | ✔ |
| Executor | 12 | 12 | SO | 53,3 ms | 3,6 ms | 5,53 | 46,1% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 56,3 ms | 5,5 ms | 5,24 | 43,7% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 57,0 ms | 10,7 ms | 5,18 | 43,1% | ✔ |

### 250.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 743,3 ms | 11,3 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 734,4 ms | 6,2 ms | 1,01 | 101,2% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 744,0 ms | 11,9 ms | 1,00 | 99,9% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 775,3 ms | 17,9 ms | 0,96 | 95,9% | ✔ |
| Executor | 2 | 2 | SO | 372,4 ms | 6,0 ms | 2,00 | 99,8% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 595,6 ms | 2,4 ms | 1,25 | 62,4% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 372,5 ms | 2,0 ms | 2,00 | 99,8% | ✔ |
| Executor | 4 | 4 | SO | 276,6 ms | 68,5 ms | 2,69 | 67,2% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 310,5 ms | 1,0 ms | 2,39 | 59,8% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 193,5 ms | 2,4 ms | 3,84 | 96,0% | ✔ |
| Executor | 6 | 6 | SO | 171,5 ms | 22,3 ms | 4,33 | 72,2% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 209,9 ms | 2,7 ms | 3,54 | 59,0% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 149,2 ms | 23,3 ms | 4,98 | 83,0% | ✔ |
| Executor | 8 | 8 | SO | 139,9 ms | 4,1 ms | 5,31 | 66,4% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 170,8 ms | 6,0 ms | 4,35 | 54,4% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 175,1 ms | 34,3 ms | 4,24 | 53,1% | ✔ |
| Executor | 12 | 12 | SO | 123,4 ms | 4,8 ms | 6,02 | 50,2% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 165,9 ms | 29,1 ms | 4,48 | 37,3% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 122,7 ms | 5,3 ms | 6,06 | 50,5% | ✔ |

### 500.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 1.459,6 ms | 2,6 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 1.457,5 ms | 2,7 ms | 1,00 | 100,1% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 1.457,9 ms | 14,8 ms | 1,00 | 100,1% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 1.457,2 ms | 0,8 ms | 1,00 | 100,2% | ✔ |
| Executor | 2 | 2 | SO | 734,9 ms | 3,8 ms | 1,99 | 99,3% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 1.164,7 ms | 1,3 ms | 1,25 | 62,7% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 735,9 ms | 14,9 ms | 1,98 | 99,2% | ✔ |
| Executor | 4 | 4 | SO | 381,7 ms | 7,5 ms | 3,82 | 95,6% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 584,7 ms | 0,4 ms | 2,50 | 62,4% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 381,8 ms | 0,6 ms | 3,82 | 95,6% | ✔ |
| Executor | 6 | 6 | SO | 270,1 ms | 15,9 ms | 5,40 | 90,1% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 410,6 ms | 0,3 ms | 3,55 | 59,2% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 260,7 ms | 0,4 ms | 5,60 | 93,3% | ✔ |
| Executor | 8 | 8 | SO | 275,7 ms | 3,0 ms | 5,29 | 66,2% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 314,3 ms | 2,1 ms | 4,64 | 58,1% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 310,1 ms | 1,5 ms | 4,71 | 58,8% | ✔ |
| Executor | 12 | 12 | SO | 215,7 ms | 6,2 ms | 6,77 | 56,4% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 216,9 ms | 8,1 ms | 6,73 | 56,1% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 222,2 ms | 19,0 ms | 6,57 | 54,7% | ✔ |

## Estatísticas por categoria

### 100.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 0,5 ms | 0,0 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 0,5 ms | 0,0 ms | 0,94 | 93,8% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 0,5 ms | 0,0 ms | 0,97 | 96,5% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 0,5 ms | 0,0 ms | 0,97 | 96,5% | ✔ |
| Executor | 2 | 2 | SO | 0,3 ms | 0,0 ms | 1,66 | 83,1% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 0,5 ms | 0,0 ms | 1,12 | 55,8% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 0,4 ms | 0,0 ms | 1,29 | 64,7% | ✔ |
| Executor | 4 | 4 | SO | 0,2 ms | 0,0 ms | 2,09 | 52,3% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 0,3 ms | 0,1 ms | 1,45 | 36,4% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 0,3 ms | 5,4 ms | 1,63 | 40,8% | ✔ |
| Executor | 6 | 6 | SO | 0,2 ms | 0,0 ms | 2,76 | 46,0% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 0,2 ms | 0,0 ms | 2,90 | 48,4% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 0,2 ms | 0,0 ms | 3,34 | 55,7% | ✔ |
| Executor | 8 | 8 | SO | 0,1 ms | 0,0 ms | 3,58 | 44,7% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 0,2 ms | 0,0 ms | 3,17 | 39,7% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 0,2 ms | 0,0 ms | 3,33 | 41,7% | ✔ |
| Executor | 12 | 12 | SO | 0,1 ms | 0,0 ms | 3,61 | 30,1% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 0,1 ms | 0,2 ms | 3,42 | 28,5% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 0,1 ms | 0,0 ms | 3,59 | 29,9% | ✔ |

### 1.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 5,0 ms | 0,2 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 4,9 ms | 0,0 ms | 1,01 | 101,2% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 4,9 ms | 0,0 ms | 1,01 | 100,5% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 5,1 ms | 0,1 ms | 0,98 | 98,1% | ✔ |
| Executor | 2 | 2 | SO | 2,5 ms | 0,0 ms | 2,01 | 100,7% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 3,7 ms | 0,1 ms | 1,33 | 66,6% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 2,6 ms | 0,1 ms | 1,94 | 97,1% | ✔ |
| Executor | 4 | 4 | SO | 1,3 ms | 0,0 ms | 3,93 | 98,2% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 1,9 ms | 0,0 ms | 2,61 | 65,2% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 1,3 ms | 0,2 ms | 3,91 | 97,7% | ✔ |
| Executor | 6 | 6 | SO | 0,9 ms | 0,0 ms | 5,61 | 93,5% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 1,3 ms | 0,1 ms | 3,88 | 64,7% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 0,9 ms | 0,2 ms | 5,69 | 94,9% | ✔ |
| Executor | 8 | 8 | SO | 1,0 ms | 0,0 ms | 5,05 | 63,1% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 1,0 ms | 0,1 ms | 5,09 | 63,6% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 1,0 ms | 0,0 ms | 5,05 | 63,1% | ✔ |
| Executor | 12 | 12 | SO | 0,7 ms | 0,0 ms | 7,09 | 59,1% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 0,7 ms | 0,0 ms | 6,96 | 58,0% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 0,7 ms | 0,0 ms | 7,13 | 59,4% | ✔ |

### 10.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 49,3 ms | 0,1 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 49,5 ms | 0,3 ms | 1,00 | 99,7% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 49,4 ms | 0,2 ms | 1,00 | 99,8% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 49,4 ms | 0,2 ms | 1,00 | 99,9% | ✔ |
| Executor | 2 | 2 | SO | 24,8 ms | 0,3 ms | 1,99 | 99,5% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 37,1 ms | 0,2 ms | 1,33 | 66,5% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 25,1 ms | 0,1 ms | 1,97 | 98,3% | ✔ |
| Executor | 4 | 4 | SO | 12,7 ms | 0,1 ms | 3,89 | 97,4% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 18,9 ms | 1,1 ms | 2,60 | 65,1% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 12,7 ms | 0,3 ms | 3,89 | 97,4% | ✔ |
| Executor | 6 | 6 | SO | 8,8 ms | 2,8 ms | 5,62 | 93,6% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 12,8 ms | 0,2 ms | 3,86 | 64,3% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 8,7 ms | 1,1 ms | 5,68 | 94,6% | ✔ |
| Executor | 8 | 8 | SO | 9,6 ms | 0,1 ms | 5,12 | 64,0% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 11,9 ms | 3,8 ms | 4,16 | 52,0% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 9,7 ms | 0,3 ms | 5,10 | 63,7% | ✔ |
| Executor | 12 | 12 | SO | 7,3 ms | 0,6 ms | 6,75 | 56,3% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 6,8 ms | 0,9 ms | 7,22 | 60,2% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 6,6 ms | 0,3 ms | 7,47 | 62,2% | ✔ |

### 50.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 245,2 ms | 1,1 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 244,9 ms | 0,4 ms | 1,00 | 100,1% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 244,7 ms | 1,0 ms | 1,00 | 100,2% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 246,3 ms | 31,4 ms | 1,00 | 99,5% | ✔ |
| Executor | 2 | 2 | SO | 123,3 ms | 2,8 ms | 1,99 | 99,4% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 184,1 ms | 0,2 ms | 1,33 | 66,6% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 122,6 ms | 48,2 ms | 2,00 | 100,0% | ✔ |
| Executor | 4 | 4 | SO | 63,5 ms | 0,2 ms | 3,86 | 96,6% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 93,1 ms | 1,2 ms | 2,63 | 65,9% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 63,6 ms | 0,4 ms | 3,85 | 96,4% | ✔ |
| Executor | 6 | 6 | SO | 43,5 ms | 0,6 ms | 5,64 | 94,0% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 64,0 ms | 0,2 ms | 3,83 | 63,8% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 43,3 ms | 22,3 ms | 5,66 | 94,3% | ✔ |
| Executor | 8 | 8 | SO | 48,0 ms | 1,4 ms | 5,11 | 63,8% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 49,6 ms | 12,2 ms | 4,95 | 61,8% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 48,0 ms | 0,3 ms | 5,10 | 63,8% | ✔ |
| Executor | 12 | 12 | SO | 33,0 ms | 0,3 ms | 7,43 | 61,9% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 33,0 ms | 2,8 ms | 7,43 | 62,0% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 35,5 ms | 2,2 ms | 6,92 | 57,6% | ✔ |

### 100.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 507,2 ms | 8,9 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 513,8 ms | 6,8 ms | 0,99 | 98,7% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 512,7 ms | 2,4 ms | 0,99 | 98,9% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 504,8 ms | 3,4 ms | 1,00 | 100,5% | ✔ |
| Executor | 2 | 2 | SO | 256,8 ms | 4,0 ms | 1,97 | 98,7% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 385,6 ms | 10,2 ms | 1,32 | 65,8% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 256,4 ms | 30,9 ms | 1,98 | 98,9% | ✔ |
| Executor | 4 | 4 | SO | 138,4 ms | 15,3 ms | 3,66 | 91,6% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 191,2 ms | 2,7 ms | 2,65 | 66,3% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 130,8 ms | 2,6 ms | 3,88 | 97,0% | ✔ |
| Executor | 6 | 6 | SO | 103,7 ms | 10,1 ms | 4,89 | 81,5% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 133,5 ms | 9,0 ms | 3,80 | 63,3% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 88,5 ms | 0,9 ms | 5,73 | 95,5% | ✔ |
| Executor | 8 | 8 | SO | 93,3 ms | 2,7 ms | 5,44 | 68,0% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 106,1 ms | 4,0 ms | 4,78 | 59,8% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 97,9 ms | 0,3 ms | 5,18 | 64,8% | ✔ |
| Executor | 12 | 12 | SO | 69,3 ms | 4,1 ms | 7,32 | 61,0% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 76,9 ms | 4,1 ms | 6,59 | 54,9% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 86,6 ms | 7,7 ms | 5,86 | 48,8% | ✔ |

### 250.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 1.229,9 ms | 21,0 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 1.264,9 ms | 13,1 ms | 0,97 | 97,2% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 1.246,5 ms | 5,9 ms | 0,99 | 98,7% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 1.231,1 ms | 11,2 ms | 1,00 | 99,9% | ✔ |
| Executor | 2 | 2 | SO | 618,7 ms | 9,1 ms | 1,99 | 99,4% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 925,9 ms | 13,4 ms | 1,33 | 66,4% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 631,1 ms | 92,3 ms | 1,95 | 97,4% | ✔ |
| Executor | 4 | 4 | SO | 324,4 ms | 2,4 ms | 3,79 | 94,8% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 468,3 ms | 30,8 ms | 2,63 | 65,7% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 324,0 ms | 0,8 ms | 3,80 | 94,9% | ✔ |
| Executor | 6 | 6 | SO | 225,8 ms | 32,2 ms | 5,45 | 90,8% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 327,5 ms | 3,4 ms | 3,76 | 62,6% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 220,4 ms | 1,9 ms | 5,58 | 93,0% | ✔ |
| Executor | 8 | 8 | SO | 223,4 ms | 2,6 ms | 5,51 | 68,8% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 250,5 ms | 4,3 ms | 4,91 | 61,4% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 245,9 ms | 1,4 ms | 5,00 | 62,5% | ✔ |
| Executor | 12 | 12 | SO | 177,0 ms | 29,0 ms | 6,95 | 57,9% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 185,3 ms | 24,9 ms | 6,64 | 55,3% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 179,5 ms | 26,6 ms | 6,85 | 57,1% | ✔ |

### 500.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 2.453,1 ms | 805,5 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 2.450,8 ms | 3,1 ms | 1,00 | 100,1% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 2.551,4 ms | 338,2 ms | 0,96 | 96,1% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 2.449,1 ms | 5,5 ms | 1,00 | 100,2% | ✔ |
| Executor | 2 | 2 | SO | 1.232,4 ms | 4,0 ms | 1,99 | 99,5% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 1.843,4 ms | 3,9 ms | 1,33 | 66,5% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 1.230,1 ms | 15,3 ms | 1,99 | 99,7% | ✔ |
| Executor | 4 | 4 | SO | 650,0 ms | 13,8 ms | 3,77 | 94,3% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 948,9 ms | 2,4 ms | 2,59 | 64,6% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 689,0 ms | 21,5 ms | 3,56 | 89,0% | ✔ |
| Executor | 6 | 6 | SO | 534,6 ms | 18,9 ms | 4,59 | 76,5% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 679,9 ms | 8,6 ms | 3,61 | 60,1% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 551,6 ms | 100,0 ms | 4,45 | 74,1% | ✔ |
| Executor | 8 | 8 | SO | 467,2 ms | 8,9 ms | 5,25 | 65,6% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 518,0 ms | 7,9 ms | 4,74 | 59,2% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 514,6 ms | 11,1 ms | 4,77 | 59,6% | ✔ |
| Executor | 12 | 12 | SO | 376,1 ms | 12,3 ms | 6,52 | 54,4% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 423,2 ms | 33,8 ms | 5,80 | 48,3% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 425,8 ms | 20,6 ms | 5,76 | 48,0% | ✔ |

## Todas as análises

### 100.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 1,6 ms | 0,0 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 1,7 ms | 0,1 ms | 0,94 | 93,7% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 1,7 ms | 0,0 ms | 0,96 | 95,6% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 1,7 ms | 0,0 ms | 0,97 | 96,6% | ✔ |
| Executor | 2 | 2 | SO | 0,9 ms | 0,0 ms | 1,73 | 86,6% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 1,1 ms | 0,0 ms | 1,46 | 73,0% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 0,9 ms | 0,1 ms | 1,81 | 90,7% | ✔ |
| Executor | 4 | 4 | SO | 0,5 ms | 0,1 ms | 3,15 | 78,7% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 0,6 ms | 0,0 ms | 2,61 | 65,2% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 0,5 ms | 0,0 ms | 3,05 | 76,3% | ✔ |
| Executor | 6 | 6 | SO | 0,5 ms | 0,0 ms | 3,36 | 56,0% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 0,5 ms | 0,0 ms | 3,35 | 55,8% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 0,5 ms | 0,7 ms | 3,18 | 53,0% | ✔ |
| Executor | 8 | 8 | SO | 0,4 ms | 0,0 ms | 3,78 | 47,3% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 0,5 ms | 0,1 ms | 3,52 | 43,9% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 0,4 ms | 0,0 ms | 3,81 | 47,6% | ✔ |
| Executor | 12 | 12 | SO | 0,4 ms | 0,0 ms | 3,87 | 32,3% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 0,4 ms | 0,8 ms | 3,85 | 32,1% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 0,4 ms | 0,0 ms | 3,98 | 33,2% | ✔ |

### 1.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 16,3 ms | 0,9 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 16,6 ms | 0,2 ms | 0,98 | 97,8% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 16,6 ms | 0,3 ms | 0,98 | 98,1% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 16,5 ms | 1,7 ms | 0,99 | 98,8% | ✔ |
| Executor | 2 | 2 | SO | 8,2 ms | 0,2 ms | 1,98 | 99,1% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 10,5 ms | 0,1 ms | 1,55 | 77,3% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 8,2 ms | 0,2 ms | 1,98 | 99,0% | ✔ |
| Executor | 4 | 4 | SO | 4,2 ms | 0,3 ms | 3,85 | 96,2% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 5,3 ms | 0,1 ms | 3,05 | 76,3% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 4,2 ms | 1,1 ms | 3,86 | 96,6% | ✔ |
| Executor | 6 | 6 | SO | 3,0 ms | 0,3 ms | 5,49 | 91,4% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 3,7 ms | 0,1 ms | 4,43 | 73,9% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 3,1 ms | 0,5 ms | 5,30 | 88,4% | ✔ |
| Executor | 8 | 8 | SO | 2,8 ms | 0,1 ms | 5,77 | 72,1% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 3,4 ms | 0,5 ms | 4,75 | 59,4% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 2,8 ms | 0,0 ms | 5,80 | 72,5% | ✔ |
| Executor | 12 | 12 | SO | 2,0 ms | 0,0 ms | 8,14 | 67,9% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 2,2 ms | 0,9 ms | 7,28 | 60,7% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 2,2 ms | 0,5 ms | 7,26 | 60,5% | ✔ |

### 10.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 162,8 ms | 0,4 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 163,2 ms | 0,2 ms | 1,00 | 99,7% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 164,3 ms | 0,9 ms | 0,99 | 99,1% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 163,6 ms | 2,2 ms | 1,00 | 99,5% | ✔ |
| Executor | 2 | 2 | SO | 82,6 ms | 0,5 ms | 1,97 | 98,6% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 104,6 ms | 4,1 ms | 1,56 | 77,8% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 82,1 ms | 1,0 ms | 1,98 | 99,1% | ✔ |
| Executor | 4 | 4 | SO | 42,4 ms | 1,1 ms | 3,84 | 96,1% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 52,9 ms | 0,4 ms | 3,08 | 76,9% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 44,3 ms | 8,5 ms | 3,67 | 91,8% | ✔ |
| Executor | 6 | 6 | SO | 34,2 ms | 6,2 ms | 4,76 | 79,4% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 36,4 ms | 0,5 ms | 4,47 | 74,6% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 33,5 ms | 2,8 ms | 4,87 | 81,1% | ✔ |
| Executor | 8 | 8 | SO | 27,6 ms | 0,4 ms | 5,89 | 73,7% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 27,5 ms | 0,1 ms | 5,92 | 74,0% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 27,6 ms | 1,0 ms | 5,89 | 73,6% | ✔ |
| Executor | 12 | 12 | SO | 23,0 ms | 1,9 ms | 7,08 | 59,0% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 20,5 ms | 2,6 ms | 7,94 | 66,2% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 19,1 ms | 2,7 ms | 8,51 | 70,9% | ✔ |

### 50.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 813,9 ms | 1,5 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 814,2 ms | 5,9 ms | 1,00 | 100,0% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 813,0 ms | 4,8 ms | 1,00 | 100,1% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 813,7 ms | 2,7 ms | 1,00 | 100,0% | ✔ |
| Executor | 2 | 2 | SO | 412,0 ms | 3,8 ms | 1,98 | 98,8% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 520,3 ms | 9,8 ms | 1,56 | 78,2% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 407,6 ms | 2,3 ms | 2,00 | 99,8% | ✔ |
| Executor | 4 | 4 | SO | 210,8 ms | 10,7 ms | 3,86 | 96,5% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 261,4 ms | 5,2 ms | 3,11 | 77,8% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 210,1 ms | 2,4 ms | 3,87 | 96,9% | ✔ |
| Executor | 6 | 6 | SO | 146,3 ms | 1,5 ms | 5,56 | 92,7% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 180,1 ms | 4,4 ms | 4,52 | 75,3% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 142,8 ms | 8,6 ms | 5,70 | 95,0% | ✔ |
| Executor | 8 | 8 | SO | 136,7 ms | 7,0 ms | 5,95 | 74,4% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 137,0 ms | 4,9 ms | 5,94 | 74,3% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 137,2 ms | 5,7 ms | 5,93 | 74,2% | ✔ |
| Executor | 12 | 12 | SO | 98,9 ms | 11,9 ms | 8,23 | 68,6% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 94,2 ms | 3,1 ms | 8,64 | 72,0% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 95,7 ms | 5,9 ms | 8,50 | 70,8% | ✔ |

### 100.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 1.632,2 ms | 3,5 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 1.636,5 ms | 119,3 ms | 1,00 | 99,7% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 1.636,4 ms | 6,1 ms | 1,00 | 99,7% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 1.725,2 ms | 103,0 ms | 0,95 | 94,6% | ✔ |
| Executor | 2 | 2 | SO | 1.040,3 ms | 46,2 ms | 1,57 | 78,4% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 1.085,4 ms | 37,3 ms | 1,50 | 75,2% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 844,1 ms | 6,8 ms | 1,93 | 96,7% | ✔ |
| Executor | 4 | 4 | SO | 538,2 ms | 27,2 ms | 3,03 | 75,8% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 630,0 ms | 61,2 ms | 2,59 | 64,8% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 440,8 ms | 42,2 ms | 3,70 | 92,6% | ✔ |
| Executor | 6 | 6 | SO | 362,0 ms | 22,8 ms | 4,51 | 75,1% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 394,9 ms | 33,2 ms | 4,13 | 68,9% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 299,1 ms | 3,1 ms | 5,46 | 90,9% | ✔ |
| Executor | 8 | 8 | SO | 275,5 ms | 2,4 ms | 5,92 | 74,0% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 291,3 ms | 18,3 ms | 5,60 | 70,0% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 419,8 ms | 31,0 ms | 3,89 | 48,6% | ✔ |
| Executor | 12 | 12 | SO | 215,6 ms | 22,9 ms | 7,57 | 63,1% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 209,0 ms | 16,5 ms | 7,81 | 65,1% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 226,1 ms | 20,5 ms | 7,22 | 60,2% | ✔ |

### 250.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 4.082,6 ms | 4,7 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 4.083,2 ms | 9,2 ms | 1,00 | 100,0% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 4.076,8 ms | 18,9 ms | 1,00 | 100,1% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 4.071,1 ms | 7,1 ms | 1,00 | 100,3% | ✔ |
| Executor | 2 | 2 | SO | 2.044,5 ms | 3,2 ms | 2,00 | 99,8% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 2.610,2 ms | 3,4 ms | 1,56 | 78,2% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 2.050,1 ms | 2,5 ms | 1,99 | 99,6% | ✔ |
| Executor | 4 | 4 | SO | 1.066,7 ms | 7,1 ms | 3,83 | 95,7% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 1.311,7 ms | 26,5 ms | 3,11 | 77,8% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 1.075,6 ms | 61,4 ms | 3,80 | 94,9% | ✔ |
| Executor | 6 | 6 | SO | 734,7 ms | 2,9 ms | 5,56 | 92,6% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 915,9 ms | 35,8 ms | 4,46 | 74,3% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 729,9 ms | 46,1 ms | 5,59 | 93,2% | ✔ |
| Executor | 8 | 8 | SO | 660,0 ms | 2,1 ms | 6,19 | 77,3% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 700,8 ms | 13,9 ms | 5,83 | 72,8% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 698,6 ms | 9,3 ms | 5,84 | 73,1% | ✔ |
| Executor | 12 | 12 | SO | 511,7 ms | 22,8 ms | 7,98 | 66,5% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 504,5 ms | 50,2 ms | 8,09 | 67,4% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 499,6 ms | 18,1 ms | 8,17 | 68,1% | ✔ |

### 500.000.000 transações

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Sequencial | 1 | 1 | - | 8.300,3 ms | 45,3 ms | 1,00 | 100,0% | ✔ |
| Executor | 1 | 1 | SO | 8.157,9 ms | 12,8 ms | 1,02 | 101,7% | ✔ |
| Executor + Affinity (compacta) | 1 | 1 | 0 | 8.182,7 ms | 13,5 ms | 1,01 | 101,4% | ✔ |
| Executor + Affinity (espalhada) | 1 | 1 | 0 | 8.196,8 ms | 65,3 ms | 1,01 | 101,3% | ✔ |
| Executor | 2 | 2 | SO | 4.100,8 ms | 3,8 ms | 2,02 | 101,2% | ✔ |
| Executor + Affinity (compacta) | 2 | 2 | 0,1 | 5.235,2 ms | 114,0 ms | 1,59 | 79,3% | ✔ |
| Executor + Affinity (espalhada) | 2 | 2 | 0,2 | 4.103,4 ms | 229,5 ms | 2,02 | 101,1% | ✔ |
| Executor | 4 | 4 | SO | 2.149,2 ms | 12,3 ms | 3,86 | 96,5% | ✔ |
| Executor + Affinity (compacta) | 4 | 4 | 0,1,2,3 | 2.645,3 ms | 16,6 ms | 3,14 | 78,4% | ✔ |
| Executor + Affinity (espalhada) | 4 | 4 | 0,2,4,6 | 2.157,0 ms | 24,5 ms | 3,85 | 96,2% | ✔ |
| Executor | 6 | 6 | SO | 1.482,0 ms | 56,3 ms | 5,60 | 93,3% | ✔ |
| Executor + Affinity (compacta) | 6 | 6 | 0,1,2,3,4,5 | 1.841,7 ms | 75,4 ms | 4,51 | 75,1% | ✔ |
| Executor + Affinity (espalhada) | 6 | 6 | 0,2,4,6,8,10 | 1.474,0 ms | 28,1 ms | 5,63 | 93,9% | ✔ |
| Executor | 8 | 8 | SO | 1.322,1 ms | 8,7 ms | 6,28 | 78,5% | ✔ |
| Executor + Affinity (compacta) | 8 | 8 | 0,1,2,3,4,5,6,7 | 1.419,2 ms | 1,6 ms | 5,85 | 73,1% | ✔ |
| Executor + Affinity (espalhada) | 8 | 8 | 0,2,4,6,8,10,1,3 | 1.408,9 ms | 57,6 ms | 5,89 | 73,6% | ✔ |
| Executor | 12 | 12 | SO | 974,9 ms | 18,2 ms | 8,51 | 71,0% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 995,0 ms | 34,6 ms | 8,34 | 69,5% | ✔ |
| Executor + Affinity (espalhada) | 12 | 12 | 0,2,4,6,8,10,1,3,5,7,9,11 | 1.039,5 ms | 48,7 ms | 7,98 | 66,5% | ✔ |

## Granularidade (tarefas por thread)

| Configuração | Threads | Tarefas | CPUs | Tempo mediano | Desvio | Speedup | Eficiência | Válido |
|---|---:|---:|---|---:|---:|---:|---:|:---:|
| Executor | 12 | 12 | SO | 1.055,6 ms | 30,0 ms | 7,86 | 65,5% | ✔ |
| Executor + Affinity (compacta) | 12 | 12 | 0,1,2,3,4,5,6,7,8,9,10,11 | 1.135,6 ms | 97,1 ms | 7,31 | 60,9% | ✔ |
| Executor | 12 | 24 | SO | 1.076,7 ms | 41,5 ms | 7,71 | 64,2% | ✔ |
| Executor + Affinity (compacta) | 12 | 24 | 0,1,2,3,4,5,6,7,8,9,10,11 | 1.190,4 ms | 77,8 ms | 6,97 | 58,1% | ✔ |
| Executor | 12 | 48 | SO | 1.073,7 ms | 48,0 ms | 7,73 | 64,4% | ✔ |
| Executor + Affinity (compacta) | 12 | 48 | 0,1,2,3,4,5,6,7,8,9,10,11 | 1.118,0 ms | 29,9 ms | 7,42 | 61,9% | ✔ |
| Executor | 12 | 96 | SO | 1.104,8 ms | 23,9 ms | 7,51 | 62,6% | ✔ |
| Executor + Affinity (compacta) | 12 | 96 | 0,1,2,3,4,5,6,7,8,9,10,11 | 1.072,2 ms | 12,7 ms | 7,74 | 64,5% | ✔ |
| Executor | 12 | 192 | SO | 1.051,4 ms | 15,9 ms | 7,89 | 65,8% | ✔ |
| Executor + Affinity (compacta) | 12 | 192 | 0,1,2,3,4,5,6,7,8,9,10,11 | 1.050,3 ms | 20,3 ms | 7,90 | 65,9% | ✔ |
| Executor | 12 | 768 | SO | 1.058,4 ms | 20,1 ms | 7,84 | 65,4% | ✔ |
| Executor + Affinity (compacta) | 12 | 768 | 0,1,2,3,4,5,6,7,8,9,10,11 | 1.059,7 ms | 27,9 ms | 7,83 | 65,3% | ✔ |
