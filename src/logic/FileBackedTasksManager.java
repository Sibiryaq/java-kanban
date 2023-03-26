package logic;

import exceptions.*;
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

public class FileBackedTasksManager extends InMemoryTaskManager { //спринт 6. класс для второй реализации менеджера, автосохранение в файл
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;

        if (!file.isFile()) {
            try {
                Path path = Files.createFile(Paths.get(file.toURI()));
            } catch (IOException e) {
                throw new ManagerCreateException("Ошибка создания файла.");
            }
        }
    }

    //Метод для проверки работы менеджера
    public static void main(String[] args) {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("data/data.csv"));
        FileBackedTasksManager manager1;

        //Заведение нескольких разных задач, эпиков и подзадач.
        manager.taskCreator(new Task("Задача №1", "Описание задачи 1", TaskStatus.NEW));
        manager.taskCreator(new Task("Задача №2", "Описание задачи 2", TaskStatus.NEW));

        Epic epic1 = new Epic("Эпик №1", "С тремя подзадачами"); //3
        manager.epicCreator(epic1);

        Subtask subtask11 = new Subtask("Подзадача № 1", "Описание подзадачи 1", TaskStatus.DONE, epic1);
        manager.subtaskCreator(subtask11);
        Subtask subtask12 = new Subtask("Подзадача № 2", "Описание подзадачи 2", TaskStatus.IN_PROGRESS, epic1);
        manager.subtaskCreator(subtask12);
        Subtask subtask13 = new Subtask("Подзадача № 3", "Описание подзадачи 3", TaskStatus.NEW, epic1);
        manager.subtaskCreator(subtask13);

        Epic epic2 = new Epic("Эпик №2", "Без подзадач"); //7
        manager.epicCreator(epic2);

        //Вывод списка задач
        System.out.println("\n Cозданные Эпики : \n" + manager.getEpics());
        System.out.println("\n Созданные Задачи : \n" + manager.getTasks());
        System.out.println("\n Созданные Подзадачи : \n" + manager.getSubtasks());
        System.out.println("Всего создано задач - " + (manager.getTasks().size() + manager.getSubtasks().size() + manager.getEpics().size()));

        //Запрос некоторых задач, чтобы заполнилась история просмотра.
        System.out.println("\n Запрос рандомной задачи : \n" + manager.getTaskById(1));
        System.out.println("\nЗапрос рандомной задачи : \n" + manager.getTaskById(2));
        System.out.println("\nЗапрос рандомного эпика : \n" + manager.getEpicById(3));
        System.out.println("\nЗапрос рандомного эпика : \n" + manager.getEpicById(7));
        System.out.println("\nЗапрос рандомной задачи : \n" + manager.getTaskById(1)); //второй раз, для проверки дублирования
        System.out.println("\nЗапрос рандомной задачи : \n" + manager.getSubtaskById(4)); //второй раз, для проверки дублирования

        //Просмотр истории обращения к задачам
        System.out.println("Показать историю : \n" + manager.history());

        System.out.println("\n----------Создание второго менеджера на основе файла первого экземпляра.");

        // Создание нового FileBackedTasksManager менеджера из этого же файла.
        manager1 = loadFromFile(Paths.get("data/data.csv").toFile());

        // Вывод списка задач
        System.out.println("\n Cозданные Эпики : \n" + manager1.getEpics());
        System.out.println("\n Созданные Задачи : \n" + manager1.getTasks());
        System.out.println("\n Созданные Подзадачи : \n" + manager1.getSubtasks());
        System.out.println("Всего создано задач - " + (manager1.getTasks().size() + manager1.getSubtasks().size() + manager1.getEpics().size()));
        System.out.println("\nСписок обращений к задачам после загрузки из файла:");
        System.out.println("Показать историю : \n" + manager1.history());
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try {
            String data = Files.readString(Path.of(file.getAbsolutePath()));
            String[] lines = data.split("\n");
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
                            if (epic != null) {
                                id = epic.getId();
                                if (id > maxId) {
                                    maxId = id;
                                }
                                fileBackedTasksManager.epicHashMap.put(id, epic);
                            }
                            break;

                        case SUBTASK:
                            Subtask subtask = (Subtask) fromString(line, TaskType.SUBTASK, fileBackedTasksManager);
                            if (subtask != null) {
                                id = subtask.getId();
                                if (id > maxId) {
                                    maxId = id;
                                }
                                fileBackedTasksManager.subtaskHashMap.put(id, subtask);
                            }
                            break;

                        case TASK:
                            Task task = fromString(line, TaskType.TASK, fileBackedTasksManager);

                            if (task != null) {
                                id = task.getId();
                                if (id > maxId) {
                                    maxId = id;
                                }
                                fileBackedTasksManager.taskHashMap.put(id, task);
                            }
                            break;

                    }
                } else {
                    List<Integer> ids = fromString(line);
                    for (Integer taskId : ids) {
                        fileBackedTasksManager.historyManager.addToHistory(getTaskAllKind(taskId, fileBackedTasksManager));
                    }
                }
            }
            fileBackedTasksManager.idGenerator = maxId;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }


        return fileBackedTasksManager;
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

    @Override
    public List<Task> history() {
        return super.history();
    }

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            HashMap<Integer, Task> allTasks = new HashMap<>();

            allTasks.putAll(super.getTasks());
            allTasks.putAll(super.getEpics());
            allTasks.putAll(super.getSubtasks());

            for (Task task : allTasks.values()) {
                writer.write(String.format("%s\n", task.toStringFromFile()));
            }
            writer.write("\n");
            writer.write(toString(this.historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
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
        return String.join(",", s);
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


}