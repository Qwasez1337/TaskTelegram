package com.taskTelegram.service;

import com.taskTelegram.entity.Task;
import com.taskTelegram.entity.TaskRepository;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task create(Task task) {
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        existsByTaskId(id);
        taskRepository.deleteById(id);
    }

    public List<Task> findNotCompletedTasks() {
        return taskRepository.findByCompletedFalse();
    }

    public List<Task> findByCompletedTrue() {
        return taskRepository.findByCompletedTrue();
    }

    public void existsByTaskId (Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new IllegalStateException("Task not found");
        }
    }

    public Task findById(Long id) {
        existsByTaskId(id);
        return taskRepository.findById(id).get();
    }

    public void updateTaskStatus(Long id, Boolean completed) {
        Task task = findById(id);
        task.setCompleted(completed);
        taskRepository.save(task);
    }

}
