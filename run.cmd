@echo off
rem Analise Paralela de Transacoes Financeiras - JCRPGPD
rem Uso: run.cmd <info|demo|benchmark|amostra> [opcoes]
rem Variavel opcional HEAP (padrao 12g). Ex.: set HEAP=8g
setlocal
set "RAIZ=%~dp0"
set "JAR=%RAIZ%target\transacoes-paralelas.jar"
if "%HEAP%"=="" set "HEAP=12g"

if not exist "%JAR%" (
    echo Compilando o projeto...
    call "%RAIZ%mvnw.cmd" -q -DskipTests package || exit /b 1
)

chcp 65001 >nul
rem ParallelGC: coletor de throughput, sem threads concorrentes disputando CPU durante as medicoes.
java -Xmx%HEAP% -XX:+UseParallelGC -Dstdout.encoding=UTF-8 -Dfile.encoding=UTF-8 -jar "%JAR%" %*
