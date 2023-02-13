package logic;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void printHistory();

    List<Task> getHistory();

    void addToHistory(Task task);
}