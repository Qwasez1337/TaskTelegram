package com.taskTelegram.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompletedFalse();
    List<Task> findByCompletedTrue();
    List<Task> findByUserId(Long userId);
}
