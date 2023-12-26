package service;

import models.Task;

import java.util.List;

public interface HistoryManager<T extends Task> {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
