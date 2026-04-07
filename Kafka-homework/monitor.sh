#!/bin/bash
echo "Банковский монитор"


while true; do
    clear
    echo "Время: $(date '+%H:%M:%S')"

    ACCOUNTS=$(docker exec bank-postgres psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM accounts;" 2>/dev/null | tr -d ' ')
    echo "Количество счетов: $ACCOUNTS"
    
    # Статистика переводов
    STATS=$(docker exec bank-postgres psql -U bankuser -d bankdb -t -c "
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success,
            SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed
        FROM transfers;" 2>/dev/null)
    
    TOTAL=$(echo $STATS | awk '{print $1}')
    SUCCESS=$(echo $STATS | awk '{print $2}')
    FAILED=$(echo $STATS | awk '{print $3}')
    echo "Всего переводов: $TOTAL ($SUCCESS | $FAILED)"
    
    echo "ПОСЛЕДНИЙ ПЕРЕВОД:"
    docker exec bank-postgres psql -U bankuser -d bankdb -c "
        SELECT 
            id,
            from_account_id as from_account,
            to_account_id as to_account,
            amount,
            status,
            TO_CHAR(created_at, 'HH24:MI:SS') as time
        FROM transfers 
        ORDER BY created_at DESC 
        LIMIT 1;" 2>/dev/null
    
    echo ""
    echo "Обновление каждые 5 секунд."
    sleep 5
done