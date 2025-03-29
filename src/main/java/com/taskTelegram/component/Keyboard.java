package com.taskTelegram.component;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

class Keyboard {

    public static SendMessage testInlineKeyboardAb (long chat_id) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText("Выбери команду");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Текущие задачи");
        inlineKeyboardButton1.setCallbackData("list");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Создать задачу");
        inlineKeyboardButton2.setCallbackData("add_task");

        row1.add(inlineKeyboardButton1);
        row1.add(inlineKeyboardButton2);
        inlineKeyboardButtons.add(row1);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        return message;
    }


}
