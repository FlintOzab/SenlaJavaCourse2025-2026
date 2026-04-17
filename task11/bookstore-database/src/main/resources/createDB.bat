@echo off
echo Установка базы данных

set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=bookstore_db
set DB_USER=bookstore_user
set DB_PASSWORD=bookstore_pass
set ADMIN_USER=postgres

echo 1. Создание базы данных '%DB_NAME%'...
psql -h %DB_HOST% -p %DB_PORT% -U %ADMIN_USER% -c "CREATE DATABASE %DB_NAME%;" 2>nul
if errorlevel 1 echo База данных уже существует или произошла ошибка

echo 2. Создание пользователя '%DB_USER%'...
psql -h %DB_HOST% -p %DB_PORT% -U %ADMIN_USER% -c "CREATE USER %DB_USER% WITH PASSWORD '%DB_PASSWORD%';" 2>nul
if errorlevel 1 echo Пользователь уже существует или произошла ошибка

echo 3. Назначение прав пользователю...
psql -h %DB_HOST% -p %DB_PORT% -U %ADMIN_USER% -c "GRANT ALL PRIVILEGES ON DATABASE %DB_NAME% TO %DB_USER%;" 2>nul

echo 4. Создание таблиц...
psql -h %DB_HOST% -p %DB_PORT% -U %ADMIN_USER% -d %DB_NAME% -f create_tables.sql
if errorlevel 1 echo Ошибка при создании таблиц

echo 5. Заполнение тестовыми данными...
psql -h %DB_HOST% -p %DB_PORT% -U %ADMIN_USER% -d %DB_NAME% -f insert_test_data.sql
if errorlevel 1 echo Ошибка при заполнении данными

echo 6. Настройка прав доступа...
psql -h %DB_HOST% -p %DB_PORT% -U %ADMIN_USER% -d %DB_NAME% -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %DB_USER%; GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %DB_USER%; GRANT USAGE ON SCHEMA public TO %DB_USER%;"

echo Установка завершена
echo Данные для подключения:
echo   Хост: %DB_HOST%
echo   Порт: %DB_PORT%
echo   База данных: %DB_NAME%
echo   Пользователь: %DB_USER%
echo   Пароль: %DB_PASSWORD%
pause