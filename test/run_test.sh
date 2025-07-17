#!/bin/bash

ITERATIONS=3

TEST_SCRIPT="load-test.js"

LOG_FILE="k6.log"

if ! command -v k6 &> /dev/null; then
    echo "Erro: K6 não está instalado. Por favor, instale o K6 antes de executar este script."
    exit 1
fi

if [ ! -f "$TEST_SCRIPT" ]; then
    echo "Erro: Script de teste $TEST_SCRIPT não encontrado."
    exit 1
fi

> "$LOG_FILE"

echo "Iniciando execução de $ITERATIONS iterações do teste de carga com K6..." | tee -a "$LOG_FILE"
echo "Data e hora de início: $(date)" | tee -a "$LOG_FILE"
echo "----------------------------------------" | tee -a "$LOG_FILE"

for ((i=1; i<=$ITERATIONS; i++))
do
    echo "Executando iteração $i de $ITERATIONS..." | tee -a "$LOG_FILE"
    echo "Data e hora da iteração $i: $(date)" | tee -a "$LOG_FILE"
    k6 run --quiet "$TEST_SCRIPT" | tee -a "$LOG_FILE"
    echo "Iteração $i concluída." | tee -a "$LOG_FILE"
    echo "----------------------------------------" | tee -a "$LOG_FILE"
    sleep 5
done

echo "Todas as $ITERATIONS iterações foram concluídas." | tee -a "$LOG_FILE"
echo "Data e hora de término: $(date)" | tee -a "$LOG_FILE"
echo "Resultados salvos em $LOG_FILE."