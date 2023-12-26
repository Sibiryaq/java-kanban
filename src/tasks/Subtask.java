package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(Integer id, String title, String description, Epic epic) {
        super(id, title, description, TaskType.SUBTASK);
        this.epic = epic;
    }

    public Subtask(String title, String description, Epic epic) {
        super(title, description, TaskType.SUBTASK);
        this.epic = epic;
    }

    public Subtask(String title, String description, TaskStatus status, Epic epic) {
        super(title, description, TaskType.SUBTASK);
        this.status = status;
        this.epic = epic;
    }

    public Subtask(Integer id, String title, String description, Epic epic, LocalDateTime startTime, Duration duration) {
        super(id, title, description, TaskType.SUBTASK, startTime, duration);
        this.epic = epic;
    }

    public Subtask(String title, String description, TaskStatus status, Epic epic, LocalDateTime startTime, Duration duration) {
        super(title, description, TaskType.SUBTASK, startTime, duration);
        this.status = status;
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
        return getId() + "," +
                getTaskType() + "," +
                getTitle() + "," +
                getStatus() + "," +
                getDescription() + "," +
                getEpic().getId() + "," +
                getStartTime() + "," +
                (getDuration() == Duration.ZERO ? "" : getDuration());
    }


}