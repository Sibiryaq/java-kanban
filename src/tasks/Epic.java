package tasks;

import logic.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskIdList;
    private LocalDateTime endTime; //Окончание последней задачи

    //Конструктор
    public Epic(Integer id, String name, String details) {
        super(id, name, details, TaskType.EPIC);

        subtaskIdList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(ArrayList<Subtask> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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