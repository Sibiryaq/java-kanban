package logic;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private static int idGenerator = 0;
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void taskCreator(Task task) {
        int id = ++idGenerator;
        task.setId(id);
        task.setStatus(TaskStatus.NEW);
        taskHashMap.put(id, task);
    }

    @Override
    public void subtaskCreator(Subtask subtask) {
        int id = ++idGenerator;
        subtask.setId(id);
        subtask.setStatus(TaskStatus.NEW);
        subtaskHashMap.put(id, subtask);
        epicHashMap.get(subtask.getIdEpic()).getSubtaskIdList().add(id);
        updateStatusEpic(epicHashMap.get(subtask.getIdEpic()));
    }

    @Override
    public void epicCreator(Epic epic) {
        int id = ++idGenerator;
        epic.setId(id);
        epic.setStatus(TaskStatus.NEW);
        epicHashMap.put(id, epic);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskHashMap.values());//Можно без this, по коду будет понятно, что запрашивается переменная класса
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskHashMap.values());//Можно без this, по коду будет понятно, что запрашивается переменная класса
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicHashMap.values());//Можно без this, по коду будет понятно, что запрашивается переменная класса
    }

    @Override
    public void deleteTaskList() {
        taskHashMap.clear();
    }

    @Override
    public void deleteSubtaskList() {
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            epic.getSubtaskIdList().clear();
            updateStatusEpic(epic);
        }
    }

    @Override
    public void deleteEpicList() {
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task idTask = taskHashMap.get(id);
        historyManager.addToHistory(idTask);
        return idTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask idSub = subtaskHashMap.get(id);
        historyManager.addToHistory(idSub );
        return idSub;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic idEpic = epicHashMap.get(id);
        historyManager.addToHistory(idEpic);
        return idEpic;
    }

    @Override
    public void deleteTask(int id) {
        System.out.println("Задача с id# " + id + " удалена." + System.lineSeparator());
        taskHashMap.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        System.out.println("Подзадача с id# " + id + " удалена." + System.lineSeparator());
        if (subtaskHashMap.containsKey(id)) {
            Epic epic = epicHashMap.get(subtaskHashMap.get(id).getIdEpic());
            epic.getSubtaskIdList().remove((Integer) id);
            updateStatusEpic(epic);
            subtaskHashMap.remove(id);
        }
    }

    @Override
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

    @Override
    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.getId())) {
            subtaskHashMap.put(subtask.getId(), subtask);
            updateStatusEpic(epicHashMap.get(subtask.getIdEpic()));
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicHashMap.containsKey(epic.getId())) {
            epicHashMap.put(epic.getId(), epic);
        }
    }

    @Override
    public boolean checkStatus(TaskStatus status, Epic epic) {
        for (int subtaskIdList : epic.getSubtaskIdList()) {
            if (!Objects.equals(subtaskHashMap.get(subtaskIdList).getStatus(), status)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        if (epic.getSubtaskIdList().isEmpty() || checkStatus(TaskStatus.NEW, epic)) {
            epic.setStatus(TaskStatus.NEW);
            return; // Можно сразу вернуться из метода, чтобы не выполнять дальнейший код
        } else if (checkStatus(TaskStatus.DONE, epic)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

    }

    @Override
    public ArrayList<Subtask> getAllSubtasks(Epic epic) {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
        for (int id : epic.getSubtaskIdList()) {
            subtaskArrayList.add(subtaskHashMap.get(id));
        }
        return subtaskArrayList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


}
