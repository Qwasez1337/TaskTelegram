package com.taskTelegram.controller;

import com.taskTelegram.entity.Task;
import com.taskTelegram.entity.TaskRepository;
import com.taskTelegram.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task addTask(@RequestBody Task task) {
        return taskService.create(task);
    }

    @DeleteMapping  (path = "{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.delete(id);
    }

    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) Boolean completed, @RequestParam(required = true) Long user_id) {
        if (completed == null) {
            return taskService.findAll();
        } else if (completed) {
            return taskService.findByCompletedTrue();
        } else return taskService.findByUserId(user_id);
    }

    @PutMapping(path = "{id}")
    public String updateTask(@PathVariable Long id, @RequestParam(required = true)Boolean completed) {
        taskService.updateTaskStatus(id, completed);
        return "Статус задачи обновлен";
    }
}
