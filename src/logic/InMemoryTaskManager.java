package logic;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    protected int idGenerator = 0;
    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> sortedTaskSet = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() != null && task2.getStartTime() != null) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return task1.getId().compareTo(task2.getId());
        } else if (task1.getStartTime() == null) {
            return 1;
        } else return -1;
    });

    @Override
    public void taskCreator(Task task) {
        if (!hasCorrectTime(task)) {
            System.out.println("Новая задача пересекается по времени с уже существующей!");
            return;
        }
        if (task.getId() == null) {
            task.setId(++idGenerator);
        }
        taskHashMap.put(task.getId(), task);
        refreshSortedSet();
    }

    @Override
    public void subtaskCreator(Subtask subtask) {
        if (!hasCorrectTime(subtask)) {
            System.out.println("Новая задача пересекается по времени с уже существующей!");
            return;
        }
        if (subtask.getId() == null) {
            subtask.setId(++idGenerator);
        }
        subtaskHashMap.put(subtask.getId(), subtask);
        refreshSortedSet();
        subtask.getEpic().getSubtaskIdList().add(subtask);
        refreshDates(subtask.getEpic());
        calcEpicStatus(subtask.getEpic());
    }

    @Override
    public void epicCreator(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(++idGenerator);
        }
        epic.setStatus(TaskStatus.NEW);
        epicHashMap.put(epic.getId(), epic);
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
    public void deleteAllTasks() {
        taskHashMap.clear();
    }

    @Override
    public void deleteAllSubtasks() {
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
    public void deleteAllEpics() {
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
    public void deleteTaskById(int id) {
        if (taskHashMap.containsKey(id)) {
            taskHashMap.remove(id);
            historyManager.remove(id);
            refreshSortedSet();
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtaskHashMap.containsKey(id)) {
            Epic epic = subtaskHashMap.get(id).getEpic();
            epic.getSubtaskIdList().remove(subtaskHashMap.get(id));
            calcEpicStatus(epic);
            subtaskHashMap.remove(id);
            historyManager.remove(id);
            refreshSortedSet();
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epicHashMap.containsKey(id)) {
            Epic epic = epicHashMap.get(id);
            epicHashMap.remove(id);
            historyManager.remove(id);
            for (Subtask subtask : epic.getSubtaskIdList()) {
                subtaskHashMap.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            epic.setSubtaskIdList(new ArrayList<>());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.getId())) {
            if (!hasCorrectTime(task)) {
                System.out.println("Новая задача пересекается по времени с уже существующей!");
                return;
            }
            taskHashMap.put(task.getId(), task);
            refreshSortedSet();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.getId())) {
            if (!hasCorrectTime(subtask)) {
                System.out.println("Новая задача пересекается по времени с уже существующей!");
                return;
            }
            subtaskHashMap.put(subtask.getId(), subtask);
            calcEpicStatus(subtask.getEpic());
            refreshSortedSet();
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

        for (Subtask subtask : epic.getSubtaskIdList()) {
            TaskStatus status = subtaskHashMap.get(subtask.getId()).getStatus();
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

    //Обновление дат начала/окончания и продолжительности эпика
    private void refreshDates(Epic epic) {
        Duration sumDuration = null;
        LocalDateTime firstDate = null;
        LocalDateTime lastDate = null;

        if (epic.getSubtaskIdList() != null) {
            for (Subtask subtask : epic.getSubtaskIdList()) {
                if (subtask.getDuration() != null && subtask.getStartTime() != null) {
                    if (firstDate == null || firstDate.isAfter(subtask.getStartTime()))
                        firstDate = subtask.getStartTime();
                    if (lastDate == null || lastDate.isBefore(subtask.getEndTime()))
                        lastDate = subtask.getEndTime();
                    if (sumDuration == null)
                        sumDuration = subtask.getDuration();
                    else
                        sumDuration = sumDuration.plus(subtask.getDuration());
                }
            }
        }
        epic.setDuration(sumDuration);
        epic.setStartTime(firstDate);
        epic.setEndTime(lastDate);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTaskSet;
    }

    // Обновление сортировки списка задач и подзадач после изменений
    private void refreshSortedSet() {
        sortedTaskSet.clear();
        sortedTaskSet.addAll(subtaskHashMap.values());
        sortedTaskSet.addAll(taskHashMap.values());
    }

    //Проверка задач и подзадач на пересечение с другими по времени
    private boolean hasCorrectTime(Task newTask) {
        Task task;
        if (newTask.getTaskType() != TaskType.EPIC) {
            task = findTaskByTime(newTask.getStartTime(), newTask.getEndTime());
            if (task == null) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    //Функция для поиска задач и подзадач по временному периоду
    private Task findTaskByTime(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            Stream<Task> tasks = Stream.concat(taskHashMap.values().stream(), subtaskHashMap.values().stream())
                    .filter(task -> task.getTaskType() != TaskType.EPIC && task.getStartTime() != null && task.getDuration() != null)
                    .filter(task -> task.getStartTime().isAfter(startDate) && task.getEndTime().isBefore(endDate));
            return tasks.findFirst().orElse(null);
        }
        return null;
    }


}
