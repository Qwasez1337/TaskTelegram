package com.taskTelegram.component;

import com.taskTelegram.entity.CreateTaskState;
import com.taskTelegram.entity.Task;
import com.taskTelegram.service.TaskService;
import lombok.SneakyThrows;
import org.glassfish.jersey.process.internal.Stage;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    private final HashMap<Long, CreateTaskState> statesCreateTask = new HashMap<>();
    private final HashMap<Long, Task> currentTask = new HashMap<>();


    public FreeTaskManagerBot(TaskService taskService) {
        this.taskService = taskService;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if ("list".equals(callData)) {
                List <Task> taskList = taskService.findNotCompletedTasks();
                sendMessage(chatId, getTaskListAsString(taskList));
            } else if ("add_task".equals(callData)) {
                sendMessage(chatId, "Введите название задачи");
                currentTask.put(chatId, new Task());
                statesCreateTask.put(chatId, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_NAME));
            }

        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.startsWith("/start")) {
                execute(Keyboard.testInlineKeyboardAb(chatId));
            }
            if (statesCreateTask.containsKey(chatId)) {
                CreateTaskState createTaskState = statesCreateTask.get(chatId);
                Task task = currentTask.get(chatId);
                switch (createTaskState.getStage()){
                    case WAITING_FOR_TASK_NAME:
                        task.setTitle(messageText);
                        statesCreateTask.remove(chatId);
                        statesCreateTask.put(chatId, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_DESCRIPTION));
                        sendMessage(chatId, "Введите описание задачи");
                        break;
                    case WAITING_FOR_TASK_DESCRIPTION:
                        task.setDescription(messageText);
                        statesCreateTask.remove(chatId);
                        statesCreateTask.put(chatId, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_DATE));
                        sendMessage(chatId, "Введите срок выполнения задачи в формате 'дддд-мм-дд'");
                        break;
                    case WAITING_FOR_TASK_DATE:
                        task.setDueDate(LocalDate.parse(messageText));
                        statesCreateTask.remove(chatId);
                        statesCreateTask.put(chatId, new CreateTaskState(CreateTaskState.Stage.NONE));
                        taskService.create(task);
                        sendMessage(chatId, "Задача успешно создана");
                        break;
                }
            }
        }
    }

    private String getTaskListAsString(List<Task> taskList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : taskList) {
            stringBuilder.append(task.getId()).append(" - ").append(task.getTitle()).append("\n");
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
