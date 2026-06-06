@echo off
REM CodeQuest Backend - Локальный запуск (без Docker)

echo CodeQuest Backend - Local Setup
echo ================================

REM Проверка Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java не найдена. Пожалуйста установите Java 17+
    exit /b 1
)

for /f "tokens=*" %%i in ('java -version 2^>^&1') do set JAVA_VERSION=%%i
echo ✓ Java найдена: %JAVA_VERSION%

REM Создание .env файла если не существует
if not exist ".env" (
    echo 📝 Создание .env файла...
    copy .env.example .env
    echo ⚠️  Отредактируйте .env файл с вашими параметрами
)

REM Запуск в dev режиме
echo.
echo 🚀 Запуск приложения...
echo 📍 API будет доступен по адресу: http://localhost:8080/api/v1
echo 📚 Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
echo.

call gradlew.bat bootRun

