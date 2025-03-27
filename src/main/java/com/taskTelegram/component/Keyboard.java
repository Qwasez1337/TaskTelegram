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
        message.setText("Нажми на кнопку получишь результат");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Приветствие");
        inlineKeyboardButton1.setCallbackData("Привет");

        row1.add(inlineKeyboardButton1);
        inlineKeyboardButtons.add(row1);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        return message;
    }


}
