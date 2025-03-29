package com.taskTelegram.entity;

public class CreateTaskState {
    private Stage stage;

    public CreateTaskState(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public enum Stage{
        NONE,
        WAITING_FOR_TASK_NAME,
        WAITING_FOR_TASK_DESCRIPTION,
        WAITING_FOR_TASK_DATE
    }
}
