package Logic;

import Exceptions.*;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager { //6/ класс для второй реализация менеджера, автосохранение в файл
    private final File file;

    /*private final File file;
    private final String fileName;*/

    public FileBackedTasksManager(File file) {
        this.file = file;

        String fileName = "./data/data.csv";
        file = new File(fileName);
        if (!file.isFile()) {
            try {
                Path path = Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new ManagerCreateException("Ошибка создания файла.");
            }
        }
    }

    /*public FileBackedTasksManager(File file) {
        this.file = file;

        fileName = "./data/data.csv";
        file = new File(fileName);
        if (!file.isFile()) {
            try {
                Path path = Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new ManagerCreateException("Ошибка создания файла.");
            }
        }
    } */

    public static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        String data = "";
        try {
            data = Files.readString(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }
        String[] lines = data.split("\n");
        List<String> epics = new ArrayList<>();
        List<String> subtasks = new ArrayList<>();
        List<String> tasks = new ArrayList<>();
        String lineOfHistory = "";
        boolean isTitle = true;
        boolean itsTask = true;
        int maxId = 0;
        int id;

        for (String line : lines) {
            if (isTitle) {
                isTitle = false;
                continue;
            }
            if (line.isEmpty() || line.equals("\r")) {
                itsTask = false;
                continue;
            }
            if (itsTask) {
                TaskType taskType = TaskType.valueOf(line.split(",")[1]);
                switch (taskType) {
                    case EPIC:
                        Epic epic = (Epic) fromString(line, TaskType.EPIC, fileBackedTasksManager);
                        id = epic.getId();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTasksManager.epicHashMap.put(id, epic);
                        //epics.add(line);
                        break;

                    case SUBTASK:
                        Subtask subtask = (Subtask) fromString(line, TaskType.SUBTASK, fileBackedTasksManager);
                        id = subtask.getId();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTasksManager.subtaskHashMap.put(id, subtask);
                        //subtasks.add(line);
                        break;

                    case TASK:
                        Task task = fromString(line, TaskType.TASK, fileBackedTasksManager);

                        id = task.getId();
                        if (id > maxId) {
                            maxId = id;
                        }
                        fileBackedTasksManager.taskHashMap.put(id, task);
                        //tasks.add(line);
                        break;

                }
            } else {
                lineOfHistory = line;
            }
        }
        fileBackedTasksManager.idGenerator = maxId;
        List<Integer> ids = fromString(lineOfHistory);
        for (Integer taskId : ids) {
            fileBackedTasksManager.historyManager.addToHistory(getTaskAllKind(taskId, fileBackedTasksManager));
        }
        return fileBackedTasksManager;
    }

    private static Task getTaskAllKind(int id, InMemoryTaskManager inMemoryTaskManager) {
        Task task = inMemoryTaskManager.getTasks().get(id);
        if (!(task == null)) {
            return task;
        }
        Task epic = inMemoryTaskManager.getEpics().get(id);
        if (!(epic == null)) {
            return epic;
        }
        Task subtask = inMemoryTaskManager.getSubtasks().get(id);
        if (!(subtask == null)) {
            return subtask;
        }
        return null;
    }

    private static String toString(HistoryManager manager) {
        List<String> s = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            s.add(String.valueOf(task.getId()));
        }
        String hist = String.join(",", s);
        return hist;
    }

    private static List<Integer> fromString(String value) {
        String[] idsString = value.split(",");
        List<Integer> tasksId = new ArrayList<>();
        for (String idString : idsString) {
            tasksId.add(Integer.valueOf(idString));
        }
        return tasksId;
    }

    private static Task fromString(String value, TaskType taskType, FileBackedTasksManager fileBackedTasksManager) {
        String[] dataOfTask = value.split(",", 6);
        int id = Integer.parseInt(dataOfTask[0]);
        String name = dataOfTask[2];
        TaskStatus status = TaskStatus.valueOf(dataOfTask[3]);
        String description = dataOfTask[4];
        String epicIdString = dataOfTask[5].trim();

        switch (taskType) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, status, description);
            case SUBTASK:
                return new Subtask(id, name, description, status, fileBackedTasksManager.epicHashMap.get(Integer.valueOf(epicIdString)));
            default:
                return null;
        }
    }

    @Override
    public List<Task> history() {
        return super.history();
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            HashMap<Integer, String> allTasks = new HashMap<>();

            HashMap<Integer, Task> tasks = super.getTasks();
            for (Integer id : tasks.keySet()) {
                allTasks.put(id, tasks.get(id).toStringFromFile());
            }

            HashMap<Integer, Epic> epics = super.getEpics();
            for (Integer id : epics.keySet()) {
                allTasks.put(id, epics.get(id).toStringFromFile());
            }

            HashMap<Integer, Subtask> subtasks = super.getSubtasks();
            for (Integer id : subtasks.keySet()) {
                allTasks.put(id, subtasks.get(id).toStringFromFile());
            }

            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }
            writer.write("\n");
            writer.write(toString(this.historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
    }

    @Override
    public void taskCreator(Task task) {
        super.taskCreator(task);
        save();
    }

    @Override
    public void epicCreator(Epic epic) {
        super.epicCreator(epic);
        save();
    }

    @Override
    public void subtaskCreator(Subtask subtask) {
        super.subtaskCreator(subtask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
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
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteTaskList() {
        super.deleteTaskList();
        save();
    }

    @Override
    public void deleteSubtaskList() {
        super.deleteSubtaskList();
        save();
    }

    @Override
    public void deleteEpicList() {
        super.deleteEpicList();
        save();
    }


}