package com.taskTelegram.component;

import com.taskTelegram.entity.Task;
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

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Отметить выполнение");
        inlineKeyboardButton3.setCallbackData("status");

        row1.add(inlineKeyboardButton1);
        row1.add(inlineKeyboardButton2);
        row2.add(inlineKeyboardButton3);
        inlineKeyboardButtons.add(row1);
        inlineKeyboardButtons.add(row2);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        return message;
    }

    public static SendMessage getListTaskInKeyboard(long chat_id, List<Task> tasks) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText("Выбери выполненную задачу");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        for (Task task : tasks) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(task.getTitle());
            inlineKeyboardButton.setCallbackData("done_" + task.getId().toString());
            row.add(inlineKeyboardButton);
            inlineKeyboardButtons.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);

        return message;
    }


}
