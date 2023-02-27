package logic;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    public void addToHistory(Task task);

    public void remove(int id);

    public List<Task> getHistory();

}