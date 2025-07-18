#!/bin/bash

RUNS=3
LOG_FILE="k6.log"
REPORT_PREFIX="k6_report"

> "$LOG_FILE"

for ((i=1; i<=RUNS; i++))
do
    echo "Iniciando teste de carga $i/$RUNS em $(date)" | tee -a "$LOG_FILE"

    K6_WEB_DASHBOARD=true \
    K6_WEB_DASHBOARD_PORT=5665 \
    K6_WEB_DASHBOARD_EXPORT="${REPORT_PREFIX}.${i}.html" \
    K6_WEB_DASHBOARD_PERIOD=1s \
    k6 run load-test.js --quiet 2>&1 | tee -a "$LOG_FILE" &

    wait $!
    echo "Teste $i concluído em $(date)" | tee -a "$LOG_FILE"
done

echo "Todos os testes concluídos. Verifique os arquivos:"
ls -1 "${REPORT_PREFIX}".*.html