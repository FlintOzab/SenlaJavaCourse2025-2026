@echo off
chcp 65001 >nul
title Банковский монитор

echo Банковский монитор запущен

echo.

:loop
cls
echo Время: %date% %time%

echo.


for /f "usebackq tokens=*" %%a in (`docker exec bank-postgres psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM accounts;" 2^>nul`) do set ACCOUNTS=%%a
if defined ACCOUNTS (
    echo Количество счетов: %ACCOUNTS%
) else (
    echo Количество счетов: Нет данных
)

for /f "usebackq tokens=*" %%a in (`docker exec bank-postgres psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM transfers;" 2^>nul`) do set TOTAL=%%a
for /f "usebackq tokens=*" %%a in (`docker exec bank-postgres psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM transfers WHERE status = 'SUCCESS';" 2^>nul`) do set SUCCESS=%%a
for /f "usebackq tokens=*" %%a in (`docker exec bank-postgres psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM transfers WHERE status = 'FAILED';" 2^>nul`) do set FAILED=%%a

if defined TOTAL (
    echo Всего переводов: %TOTAL% (Успешно: %SUCCESS% ^| Ошибок: %FAILED%)
) else (
    echo Переводов пока нет
)

echo.
echo ПОСЛЕДНИЙ ПЕРЕВОД:
echo.
docker exec bank-postgres psql -U bankuser -d bankdb -c "SELECT id, from_account_id, to_account_id, amount, status, TO_CHAR(created_at, 'HH24:MI:SS') as time FROM transfers ORDER BY created_at DESC LIMIT 1;" 2>nul

if errorlevel 1 (
    echo   Нет переводов в БД
)

echo.
echo Обновление каждые 5 секунд. Ctrl+C для выхода

timeout /t 5 /nobreak >nul
goto loop