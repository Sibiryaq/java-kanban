package logic;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void taskCreator(Task task) {
        task.setId(++id);
        taskHashMap.put(id, task);
    }

    @Override
    public void subtaskCreator(Subtask subtask) {
        subtask.setId(++id);
        subtaskHashMap.put(id, subtask);
        subtask.getEpic().getSubtaskIdList().add(id);
        calcEpicStatus(subtask.getEpic());
    }

    @Override
    public void epicCreator(Epic epic) {
        epic.setId(++id);
        epic.setStatus(TaskStatus.NEW);
        epicHashMap.put(id, epic);
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return taskHashMap;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtaskHashMap;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epicHashMap;
    }

    @Override
    public void deleteTaskList() {
        taskHashMap.clear();
    }

    @Override
    public void deleteSubtaskList() {
        ArrayList<Epic> epicsForStatusUpdate = new ArrayList<>();
        for (Subtask subtask : subtaskHashMap.values()) {
            subtask.getEpic().setSubtaskIdList(new ArrayList<>());
            if (!epicsForStatusUpdate.contains(subtask.getEpic())) {
                epicsForStatusUpdate.add(subtask.getEpic());
            }
        }
        subtaskHashMap.clear();
        for (Epic epic : epicsForStatusUpdate) {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void deleteEpicList() {
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (taskHashMap.containsKey(id)) {
            historyManager.addToHistory(taskHashMap.get(id));
            return taskHashMap.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtaskHashMap.containsKey(id)) {
            historyManager.addToHistory(subtaskHashMap.get(id));
            return subtaskHashMap.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (epicHashMap.containsKey(id)) {
            historyManager.addToHistory(epicHashMap.get(id));
            return epicHashMap.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void deleteTask(int id) {
        if (taskHashMap.containsKey(id)) {
            System.out.println("Задача с id# " + id + " удалена." + System.lineSeparator());
            taskHashMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtaskHashMap.containsKey(id)) {
            System.out.println("Подзадача с id# " + id + " удалена." + System.lineSeparator());
            Epic epic = subtaskHashMap.get(id).getEpic();
            epic.getSubtaskIdList().remove((Integer) id);
            calcEpicStatus(epic);
            subtaskHashMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epicHashMap.containsKey(id)) {
            System.out.println("Эпик с id# " + id + " удален." + System.lineSeparator());
            Epic epic = epicHashMap.get(id);
            epicHashMap.remove(id);
            historyManager.remove(id);
            for (Integer subtaskId : epic.getSubtaskIdList()) {
                subtaskHashMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.setSubtaskIdList(new ArrayList<>());
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
            calcEpicStatus(subtask.getEpic());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicHashMap.containsKey(epic.getId())) {
            epic.setSubtaskIdList(epicHashMap.get(epic.getId()).getSubtaskIdList());
            epicHashMap.put(epic.getId(), epic);
            calcEpicStatus(epic);
        }
    }

    @Override
    public List<Task> history() { //historyTest
        return historyManager.getHistory();
    }

    private void calcEpicStatus(Epic epic) {

        if (epic.getSubtaskIdList().size() == 0) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allTaskIsNew = true;
        boolean allTaskIsDone = true;

        for (Integer epicSubtaskId : epic.getSubtaskIdList()) {
            TaskStatus status = subtaskHashMap.get(epicSubtaskId).getStatus();
            if (!(status == TaskStatus.NEW)) {
                allTaskIsNew = false;
            }
            if (!(status == TaskStatus.DONE)) {
                allTaskIsDone = false;
            }
        }

        if (allTaskIsDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allTaskIsNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

    }

}
