package ru.kovalenkojuls.telegrambot;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.kovalenkojuls.telegrambot.services.TelegramBotService;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
@ComponentScan(basePackages = "ru.kovalenkojuls.telegrambot")
public class TelegramBotApplication {

    private final TelegramBotService myTelegramBot;

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(myTelegramBot);
        } catch (TelegramApiException e) {
            log.error("Ошибка регистрации бота: {}", e.getMessage());
        }
    }
}
