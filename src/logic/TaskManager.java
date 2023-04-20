package logic;

import tasks.*;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    void taskCreator(Task task);
    void subtaskCreator(Subtask subtask);
    void epicCreator(Epic epic);

    HashMap<Integer, Task> getTasks();
    HashMap<Integer, Subtask> getSubtasks();
    HashMap<Integer, Epic> getEpics();
    HashMap<Integer, Task> getAllTasks();

    void deleteAllTasks();
    void deleteAllSubtasks();
    void deleteAllEpics();

    Task getTaskById(int id);
    Subtask getSubtaskById(int id);
    Epic getEpicById(int id);

    void deleteTaskById(int id);
    void deleteSubtaskById(int id);
    void deleteEpicById(int id);

    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);

    List<Task> getTaskHistory();
    List<Task> getPrioritizedTasks();

}

