package service;

import enums.*;
import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final String path;

    public FileBackedTaskManager(String path) {
        super();
        this.path = path;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subTask = super.getSubtask(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws FileNotFoundException {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file.toString());
        try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {
            String line;
            while (!(line = br.readLine()).isBlank()) {

                if (!line.contains("id,type")) {

                    Task task = fromString(line);
                    if (task == null) {
                        continue;
                    }
                    switch (task.getType()) {
                        case TASK:
                            taskManager.createTask(task);
                            break;
                        case EPIC:
                            taskManager.createEpic((Epic) task);
                            break;
                        case SUBTASK:
                            taskManager.createSubTask((SubTask) task);
                            break;
                    }
                }
            }
            if (br.ready()) {
                line = br.readLine();
                List<Integer> loadedHistory = historyFromString(line);
                for (Integer id : loadedHistory) {
                    if (taskManager.tasks.containsKey(id)) {
                        taskManager.getTask(id);
                    } else if (taskManager.epics.containsKey(id)) {
                        taskManager.getEpic(id);
                    } else if (taskManager.subtasks.containsKey(id)) {
                        taskManager.getSubtask(id);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return taskManager;
    }

    protected void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.path, false))) {
            bw.write("id,type,name,status,description,startTime,duration,endTime,epic \n");
            bw.write(separateTasksFromLists(super.getTasks()));
            bw.write(separateTasksFromLists(super.getEpics()));
            bw.write(separateTasksFromLists(super.getSubtasks()));
            bw.write("\n");
            bw.write(historyToString(super.getHistoryManager()));

        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Ошибка сохранения файла.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static String historyToString(HistoryManager<Task> historyManager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            sb.append(task.getUniqueID());
            sb.append(",");
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> list = new ArrayList<>();
        String[] ids = value.split(",");
        for (String i : ids) {
            list.add(Integer.parseInt(i));
        }

        return list;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        Tasks type = Tasks.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (fields[5].equals("null")) {
            startTime = null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
            startTime = LocalDateTime.parse(fields[5], formatter);
        }
        int duration = Integer.parseInt(fields[6]);

        switch (type) {
            case TASK:
                return new Task(name, description, id, status, duration, startTime);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[8]);
                return new SubTask(name, description, id, status, duration, startTime, epicId);
            case EPIC:
                if (fields[7].equals("null")) {
                    endTime = null;
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
                    endTime = LocalDateTime.parse(fields[7], formatter);
                }
                return new Epic(name, description, id, status, duration, startTime, endTime);
        }

        return null;
    }

    protected String separateTasksFromLists(List<?> list) {
        StringBuilder sb = new StringBuilder();
        for (Object task : list) {
            sb.append(task.toString());
        }
        return sb.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        // 1 сценарий:
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager("resources/save.csv");
        fileBackedTaskManager.createTask(new Task("simple task", "no big deal", 0, Status.NEW,
                15, LocalDateTime.of(2023, Month.MARCH, 16, 11, 4)));

        //fileBackedTaskManager.createTask(new Task("sample", "just for fun", 0, Status.NEW));// проверка реакции на null дату

        fileBackedTaskManager.createEpic(new Epic("epic epic", "got three subtasks", 0, Status.NEW));
        fileBackedTaskManager.createSubTask(new SubTask("first subtask", "am I the startTime?", 0,
                Status.IN_PROGRESS, 15, LocalDateTime.of(2023, Month.MARCH, 16, 14, 2),
                2));
        fileBackedTaskManager.createSubTask(new SubTask("second subtask",
                "needed for date testing", 0, Status.NEW, 15,
                LocalDateTime.of(2023, Month.MARCH, 16, 14, 20), 2));

        fileBackedTaskManager.createSubTask(new SubTask("third subtask",
                "needed for time conflicts testing", 0, Status.NEW, 15,
                LocalDateTime.of(2023, Month.MARCH, 16, 14, 7), 2));

        fileBackedTaskManager.getTask(1);
        System.out.println(fileBackedTaskManager.getHistory());

        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println(fileBackedTaskManager.getSubtasks());

        System.out.println(fileBackedTaskManager.getPrioritizedTasks());

        FileBackedTaskManager fileBackedTaskManager2 = FileBackedTaskManager.loadFromFile(new
                File("resources/save.csv"));

        System.out.println(fileBackedTaskManager2.getHistory());
        System.out.println(fileBackedTaskManager2.getPrioritizedTasks());

    }
}
