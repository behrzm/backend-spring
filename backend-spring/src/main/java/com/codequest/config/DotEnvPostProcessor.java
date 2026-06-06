package com.codequest.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Загружает переменные из .env файла в окружение Spring Boot
 */
@Component
public class DotEnvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // Пытаемся загрузить .env файл
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMalformed()
                    .load();
            
            // Конвертируем переменные в Map
            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
            });
            
            // Добавляем в environment Spring'а
            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenv", dotenvMap)
            );
        } catch (DotenvException e) {
            // .env файл не найден - это нормально для production
            System.out.println(".env файл не найден, используются системные переменные окружения");
        }
    }
}

