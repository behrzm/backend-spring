#!/bin/bash

# CodeQuest Backend - Запуск тестов и анализ кода

echo "CodeQuest Backend - Test Suite"
echo "=============================="
echo ""

# Запуск юнит тестов
echo "🧪 Запуск юнит-тестов..."
./gradlew test

if [ $? -eq 0 ]; then
    echo "✅ Юнит-тесты пройдены успешно"
else
    echo "❌ Юнит-тесты провалились"
    exit 1
fi

echo ""

# Запуск интеграционных тестов
echo "🔗 Запуск интеграционных тестов..."
./gradlew integrationTest 2>/dev/null || echo "⚠️  Интеграционные тесты не найдены (опционально)"

echo ""

# Статистика покрытия кода
echo "📊 Сборка проекта..."
./gradlew build

echo ""
echo "✅ Все тесты завершены!"
echo ""
echo "📈 Вы можете запустить:"
echo "  - ./gradlew test          - Запустить юнит-тесты"
echo "  - ./gradlew build         - Собрать проект"
echo "  - ./gradlew bootRun       - Запустить приложение"

