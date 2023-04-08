package tasks;

import logic.TaskStatus;
import logic.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, TaskStatus status, Epic epic) {
        super(title, description, status);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(int id, String title, String description, TaskStatus status, Epic epic) {
        super(id, title, description, status);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String title, String description, TaskStatus status, Epic epic, LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "ID подзадачи Subtask=\"" + id
                + "\", Название подзадачи=\"" + title
                + "\", Описание=\"" + description
                + "\", Статус=\"" + status + "\""
                + "\n";
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", id, taskType, title, status, description, epic.getId());
    }
}