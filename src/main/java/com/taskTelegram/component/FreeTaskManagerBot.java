package com.taskTelegram.component;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class FreeTaskManagerBot  extends TelegramLongPollingBot {

    @Value("${telegrambots.botToken}")
    private String botToken;
    @Value("${telegrambots.botUsername}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        // Здесь обрабатывай входящие сообщения
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Пример простой обработки команды:
            if (messageText.startsWith("/start")) {
                sendMessage(chatId, "Привет! Я твой таск-бот.");
            } else if (messageText.startsWith("/addtask")) {
                // Парсинг параметров и вызов taskService для добавления задачи
                sendMessage(chatId, "Задача добавлена!");
            } else if (messageText.startsWith("/listtasks")) {
                // Вызов taskService для получения списка задач
                sendMessage(chatId, "Список задач: ...");
            }
            // Добавь другие команды по необходимости
        }
    }

    private void sendMessage(long chatId, String text) {
        // Используй Telegram API для отправки сообщений
        // Например, можно создать SendMessage, установить chatId, текст и вызвать execute(sendMessage)
        // Здесь просто пример, детали можно найти в документации библиотеки
        try {
            var message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
