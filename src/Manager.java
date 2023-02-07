import taskclasses.Epic;
import taskclasses.Subtask;
import taskclasses.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Manager {
    private static int idGenerator = 0;
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

    public void taskCreator(Task task) {
        int id = ++idGenerator;
        task.setId(id);
        task.setStatus("NEW");
        taskHashMap.put(id, task);
    }

    public void subtaskCreator(Subtask subtask) {
        int id = ++idGenerator;
        subtask.setId(id);
        subtask.setStatus("NEW");
        subtaskHashMap.put(id, subtask);
        epicHashMap.get(subtask.getIdEpic()).getSubtaskIdList().add(id);
        updateStatusEpic(epicHashMap.get(subtask.getIdEpic()));
    }

    public void epicCreator(Epic epic) {
        int id = ++idGenerator;
        epic.setId(id);
        epic.setStatus("NEW");
        epicHashMap.put(id, epic);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(this.taskHashMap.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtaskHashMap.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(this.epicHashMap.values());
    }

    public void deleteTaskList() {
        taskHashMap.clear();
    }

    public void deleteSubtaskList() {
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            epic.getSubtaskIdList().clear();
            updateStatusEpic(epic);
        }
    }

    public void deleteEpicList() {
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    public Task getTaskById(int id) {
        return taskHashMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskHashMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicHashMap.get(id);
    }

    public void deleteTask(int id) {
        System.out.println("Задача с id# " + id + " удалена." + System.lineSeparator());
        taskHashMap.remove(id);
    }

    public void deleteSubtask(int id) {
        System.out.println("Подзадача с id# " + id + " удалена." + System.lineSeparator());
        if (subtaskHashMap.containsKey(id)) {
            Epic epic = epicHashMap.get(subtaskHashMap.get(id).getIdEpic());
            epic.getSubtaskIdList().remove((Integer) id);
            updateStatusEpic(epic);
            subtaskHashMap.remove(id);
        }
    }

    public void deleteEpic(int id) {
        System.out.println("Эпик с id# " + id + " удален." + System.lineSeparator());
        if (epicHashMap.containsKey(id)) {
            Epic epic = epicHashMap.get(id);
            ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
            for (int subtasks : subtaskIdList) {
                subtaskHashMap.remove(subtasks);
            }
            epicHashMap.remove(id);
        }
    }

    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.getId())) {
            subtaskHashMap.put(subtask.getId(), subtask);
            updateStatusEpic(epicHashMap.get(subtask.getIdEpic()));
        }
    }

    public void updateEpic(Epic epic) {
        if (epicHashMap.containsKey(epic.getId())) {
            epicHashMap.put(epic.getId(), epic);
        }
    }

    private boolean checkStatus(String status, Epic epic) {
        for (int subtaskIdList : epic.getSubtaskIdList()) {
            if (!Objects.equals(subtaskHashMap.get(subtaskIdList).getStatus(), status)) {
                return false;
            }
        }
        return true;
    }

    private void updateStatusEpic(Epic epic) {
        if (epic.getSubtaskIdList().isEmpty() || checkStatus("NEW", epic)) {
            epic.setStatus("NEW");

        } else if (checkStatus("DONE", epic)) {
            epic.setStatus("DONE");

        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

    public ArrayList<Subtask> getAllSubtasks(Epic epic) {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
        for (int id : epic.getSubtaskIdList()) {
            subtaskArrayList.add(subtaskHashMap.get(id));
        }
        return subtaskArrayList;
    }
}