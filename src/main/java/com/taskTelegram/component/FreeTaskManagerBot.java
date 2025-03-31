package com.taskTelegram.component;

import com.taskTelegram.entity.CreateTaskState;
import com.taskTelegram.entity.Task;
import com.taskTelegram.service.TaskService;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.glassfish.jersey.process.internal.Stage;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FreeTaskManagerBot extends TelegramLongPollingBot {
    //public class FreeTaskManagerBot extends LongPollingBot {

    @Value("${telegrambots.botToken}")
    private String botToken;
    @Value("${telegrambots.botUsername}")
    private String botUsername;
    private final TaskService taskService;
    private final TaskDialogManager taskDialogManager;

    public FreeTaskManagerBot(TaskService taskService, TaskDialogManager taskDialogManager) {
        this.taskService = taskService;
        this.taskDialogManager = taskDialogManager;
    }

    @PostConstruct
    private void addCommandInterface() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Обновить меню"));
        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.startsWith("/start")) {
                execute(Keyboard.startInlineKeyboard(chatId));
            }
            if (taskDialogManager.hasActiveSession(chatId)) {
                String textMessage = taskDialogManager.updateTaskCreation(chatId, messageText);
                sendMessage(chatId, textMessage);
                if(textMessage.equals("Задача успешно создана")){
                    execute(Keyboard.startInlineKeyboard(chatId));
                }
            }
        }

        handleButtonClick(update);
    }

    private String getTaskListAsString(List<Task> taskList) {
        if (taskList.isEmpty()) {
            return "Текущие задачи отсутсвуют";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int counter = 1;
        for (Task task : taskList) {
            stringBuilder.append(counter).append(". ").append(task.getTitle()).append("\n");
            stringBuilder.append(task.getDescription()).append("\n");
            stringBuilder.append(task.getDueDate()).append("\n");
            counter++;
        }
        return stringBuilder.toString();
    }

    private void sendMessage(long chatId, String text) {
        try {
            var message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            execute(message);
        } catch (TelegramApiException e) {
            throw new IllegalStateException();
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

    public void handleButtonClick(Update update) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long userId = update.getCallbackQuery().getFrom().getId();
            String callData = update.getCallbackQuery().getData();
            if ("list".equals(callData)) {
                List<Task> taskList = taskService.findByUserId(userId);
                sendMessage(chatId, getTaskListAsString(taskList));
            } else if ("add_task".equals(callData)) {
                sendMessage(chatId, taskDialogManager.startTaskCreation(chatId, userId));
            } else if ("status".equals(callData)) {
                List<Task> taskList = taskService.findByUserId(userId);
                execute(Keyboard.getListTaskInKeyboard(chatId, taskList));
            } else if (callData.startsWith("done_")) {
                Long taskId = Long.valueOf(callData.split("_")[1]);
                Task task = taskService.findById(taskId);
                taskService.delete(taskId);
                sendMessage(chatId, "Задача " + task.getTitle() + " выполенена");
                execute(Keyboard.startInlineKeyboard(chatId));
            }

        }
    }
}
