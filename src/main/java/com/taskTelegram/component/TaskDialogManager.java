package com.taskTelegram.component;

import com.taskTelegram.entity.CreateTaskState;
import com.taskTelegram.entity.Task;
import com.taskTelegram.service.TaskService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class TaskDialogManager {
    private final Map<Long, CreateTaskState> stateMap;
    private final Map<Long, Task> taskMap;
    private final TaskService taskService;

    public TaskDialogManager(TaskService taskService) {
        this.taskService = taskService;
        this.stateMap = new HashMap<>();
        this.taskMap = new HashMap<>();
    }

    public String startTaskCreation(long chatId, long userId) {
        Task taskNew = new Task();
        taskNew.setUserId(userId);
        taskMap.put(userId, taskNew);
        stateMap.put(chatId, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_NAME));
        return "Введите название задачи";
    }

    String updateTaskCreation(long chatId, String userInput) {
        CreateTaskState createTaskState = stateMap.get(chatId);
        Task task = taskMap.get(chatId);
        switch (createTaskState.getStage()) {
            case WAITING_FOR_TASK_NAME:
                task.setTitle(userInput);
                stateMap.remove(chatId);
                stateMap.put(chatId, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_DESCRIPTION));
                return "Введите описание задачи";
            case WAITING_FOR_TASK_DESCRIPTION:
                task.setDescription(userInput);
                stateMap.remove(chatId);
                stateMap.put(chatId, new CreateTaskState(CreateTaskState.Stage.WAITING_FOR_TASK_DATE));
                return "Введите срок выполнения задачи в формате 'dd.MM.yyyy' или словами сегодня/завтра/послезавтра";
            case WAITING_FOR_TASK_DATE:
                LocalDate date = DateParser.parseDate(userInput);
                task.setDueDate(date);
                stateMap.remove(chatId);
                stateMap.put(chatId, new CreateTaskState(CreateTaskState.Stage.NONE));
                taskService.create(task);
                cancelTaskCreation(chatId);
                return "Задача успешно создана";
        }
        return "Что-то пошло не так, попробуйте еще раз";
    }

    public boolean hasActiveSession(Long chatId) {
        return stateMap.containsKey(chatId);
    }

    public void cancelTaskCreation(long chatId) {
        stateMap.remove(chatId);
    }
}


