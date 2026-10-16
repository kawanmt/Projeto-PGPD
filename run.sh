#!/usr/bin/env sh
# Analise Paralela de Transacoes Financeiras - JCRPGPD
# Uso: ./run.sh <info|demo|benchmark|amostra> [opcoes]   (HEAP=8g ./run.sh ... para mudar o heap)
RAIZ="$(cd "$(dirname "$0")" && pwd)"
JAR="$RAIZ/target/transacoes-paralelas.jar"
HEAP="${HEAP:-12g}"

if [ ! -f "$JAR" ]; then
  echo "Compilando o projeto..."
  "$RAIZ/mvnw" -q -DskipTests package || exit 1
fi

exec java -Xmx"$HEAP" -XX:+UseParallelGC -Dstdout.encoding=UTF-8 -Dfile.encoding=UTF-8 -jar "$JAR" "$@"
