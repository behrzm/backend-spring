#!/bin/bash

# CodeQuest Backend - Локальный запуск (без Docker)

echo "CodeQuest Backend - Local Setup"
echo "================================"

# Проверка Java
if ! command -v java &> /dev/null; then
    echo "❌ Java не найдена. Пожалуйста установите Java 17+"
    exit 1
fi

echo "✓ Java найдена: $(java -version 2>&1 | head -n 1)"

# Создание .env файла если не существует
if [ ! -f .env ]; then
    echo "📝 Создание .env файла..."
    cp .env.example .env
    echo "⚠️  Отредактируйте .env файл с вашими параметрами"
fi

# Запуск в dev режиме
echo ""
echo "🚀 Запуск приложения..."
echo "📍 API будет доступен по адресу: http://localhost:8080/api/v1"
echo "📚 Swagger UI: http://localhost:8080/api/v1/swagger-ui.html"
echo ""

./gradlew bootRun

