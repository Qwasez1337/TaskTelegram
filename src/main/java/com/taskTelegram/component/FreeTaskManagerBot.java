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
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
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
    private long user_id;
    private long chat_id;

    public FreeTaskManagerBot(TaskService taskService) {
        this.taskService = taskService;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            if ("list".equals(callData)) {
                List <Task> taskList = taskService.findByUserId(user_id);
                sendMessage(chat_id, getTaskListAsString(taskList));
            } else if ("add_task".equals(callData)) {
                sendMessage(chat_id, "Введите название задачи");
                Task taskNew = new Task();
                taskNew.setUserId(user_id);
                currentTask.put(chat_id, taskNew);
                statesCreateTask.put(chat_id, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_NAME));
            } else if ("status".equals(callData)) {
                List <Task> taskList = taskService.findByUserId(user_id);
                execute(Keyboard.getListTaskInKeyboard(chat_id, taskList));
            } else if (callData.startsWith("done_")) {
                Long taskId = Long.valueOf(callData.split("_")[1]);
                Task task = taskService.findById(taskId);
                taskService.delete(taskId);
                sendMessage(chat_id, "Задача " + task.getTitle() + " выполенена");
                execute(Keyboard.testInlineKeyboardAb(chat_id));
            }

        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            user_id = update.getMessage().getFrom().getId();
            chat_id = update.getMessage().getChatId();
            if (messageText.startsWith("/start")) {
                execute(Keyboard.testInlineKeyboardAb(chat_id));
            }
            if (statesCreateTask.containsKey(chat_id)) {
                CreateTaskState createTaskState = statesCreateTask.get(chat_id);
                Task task = currentTask.get(chat_id);
                switch (createTaskState.getStage()){
                    case WAITING_FOR_TASK_NAME:
                        task.setTitle(messageText);
                        statesCreateTask.remove(chat_id);
                        statesCreateTask.put(chat_id, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_DESCRIPTION));
                        sendMessage(chat_id, "Введите описание задачи");
                        break;
                    case WAITING_FOR_TASK_DESCRIPTION:
                        task.setDescription(messageText);
                        statesCreateTask.remove(chat_id);
                        statesCreateTask.put(chat_id, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_DATE));
                        sendMessage(chat_id, "Введите срок выполнения задачи в формате 'дддд-мм-дд'");
                        break;
                    case WAITING_FOR_TASK_DATE:
                        task.setDueDate(LocalDate.parse(messageText));
                        statesCreateTask.remove(chat_id);
                        statesCreateTask.put(chat_id, new CreateTaskState(CreateTaskState.Stage.NONE));
                        taskService.create(task);
                        sendMessage(chat_id, "Задача успешно создана");
                        execute(Keyboard.testInlineKeyboardAb(chat_id));
                        break;
                }
            }
        }
    }

    private String getTaskListAsString(List<Task> taskList) {
        if(taskList.isEmpty()){
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
