package models;

import enums.Status;
import enums.Tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static enums.Tasks.TASK;

public class Task {
    protected String name;
    protected String description;
    protected int uniqueID;
    protected Status status;

    protected Tasks type;

    protected int duration;
    LocalDateTime startTime;

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Task(String name, String description, int uniqueID, Status status, int duration,
                LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.uniqueID = uniqueID;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.type = TASK;
    }

    public Task(String name, String description, int uniqueID, Status status) {
        this.name = name;
        this.description = description;
        this.uniqueID = uniqueID;
        this.status = status;
        this.type = TASK;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public Status getStatus() {
        return status;
    }

    public void setUniqueID(int uniqueID) {

        this.uniqueID = uniqueID;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Tasks getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return uniqueID == task.uniqueID && duration == task.duration
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status && type == task.type
                && startTime.equals(task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, uniqueID, status, type, duration, startTime);
    }

    @Override
    public String toString() {
        String formattedTaskDate;
        String formattedEndTime;
        if (startTime == null) {
            formattedTaskDate = null;
            formattedEndTime = null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
            formattedTaskDate = startTime.format(formatter);
            formattedEndTime = getEndTime().format(formatter);
        }
        return uniqueID + "," + type + "," +
                name + "," + status + "," + description + "," + formattedTaskDate + "," + duration + "," +
                formattedEndTime + "\n";
    }
}
