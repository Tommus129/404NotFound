#!/bin/bash
cd "$(dirname "$0")"

JAVA_BIN="./custom-jre/bin/java"
JAR_FILE="play.jar"

if [ ! -f "$JAVA_BIN" ]; then
  echo "ERRORE: Java non trovato in $JAVA_BIN"
  exit 1
fi

if [ ! -f "$JAR_FILE" ]; then
  echo "ERRORE: File $JAR_FILE non trovato"
  exit 1
fi

echo "Avvio dell'applicazione JavaFX..."
"$JAVA_BIN" -jar "$JAR_FILE"
