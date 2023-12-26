package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected String title;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskType taskType;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
        taskType = TaskType.TASK;
    }

    public Task(Integer id, String title, String description, TaskType taskType) {
        this.id = id;
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        this.taskType = taskType;
    }

    public Task(String title, String description, TaskType taskType) {
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        this.taskType = taskType;
    }

    public Task(Integer id, String title, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer id, String title, String description, TaskType taskType, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, TaskType taskType, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null)
            return startTime.plusSeconds(duration.toSeconds());
        else
            return null;
    }

    @Override
    public String toString() {
        return getId() + "," +
                getTaskType() + "," +
                getTitle() + "," +
                getStatus() + "," +
                getDescription() + ",," +
                getStartTime() + "," +
                (getDuration() == Duration.ZERO ? "" : getDuration());
    }
}