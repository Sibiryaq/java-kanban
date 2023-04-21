package logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    FileBackedTasksManager taskManager1;

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(new File("data/save_tasks.txt"));
    }

    @Test
    void saveTest() {
        // Пустой список задач.
        assertDoesNotThrow(() -> taskManager.save(), "Сохранение менеджера с пустым списком задач не должно вызывать исключений!");

        //b. Эпик без подзадач.
        Epic epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        taskManager.save();
        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());
        assertEquals(1, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(0, taskManager1.getSubtasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(0, taskManager1.getTasks().size(), "Количество задач менеджера после восстановления не совпало!");

        // Пустой список истории.
        assertEquals(0, taskManager1.getTaskHistory().size(), "Количество задач в истории обращения после восстановления не совпало!");
    }

    //Тестирование метода загрузки списка задач из файла
    @Test
    void loadFromFileTest() {   //public static FileBackedTasksManager loadFromFile(Path file)
        // Эпик без подзадач.
        Epic epic1 = new Epic(100, "Эпик 1", "Пустой эпик");
        taskManager.epicCreator(epic1);
        taskManager.save();

        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());
        assertEquals(1, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");

        // Пустой список истории.
        assertEquals(0, taskManager1.getTaskHistory().size(), "Количество задач в истории обращения после восстановления не совпало!");

        // Со стандартным поведением.
        Epic epic2 = new Epic(200, "Эпик 2", "Эпик с подзадачами");
        taskManager.epicCreator(epic2);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic2);
        taskManager.subtaskCreator(subtask1); //1

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic2);
        taskManager.subtaskCreator(subtask2); //2

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        taskManager.taskCreator(task1); //3

        taskManager.getTaskById(3);   //Добавление истории

        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());
        assertEquals(taskManager1.getTaskById(3).getTitle(), task1.getTitle());
        assertEquals(taskManager1.getTaskById(3).getDescription(), task1.getDescription());
        assertEquals(taskManager1.getTaskById(3).getId(), task1.getId());
        assertEquals(1, taskManager1.getTasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(2, taskManager1.getSubtasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(2, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(1, taskManager1.getTaskHistory().size(), "Количество задач в истории обращения после восстановления не совпало!");
    }

    @Test
    public void loadFromFileAnotherTest() {
        assertEquals(0, taskManager.getTaskHistory().size());
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.taskCreator(task);
        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());
        assertEquals(taskManager1.getTaskById(1).getTitle(), task.getTitle());
        assertEquals(taskManager1.getTaskById(1).getDescription(), task.getDescription());
        assertEquals(taskManager1.getTaskById(1).getId(), task.getId());
    }

    @Test
    public void testingFileEpicWithoutSubtasks() {
        Epic epic = new Epic(100, "Эпик", "Описание");
        taskManager.epicCreator(epic);
        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());

        // c. Пустой список истории.
        assertEquals(0, taskManager1.getTaskHistory().size());
        assertEquals(taskManager1.getEpicById(100).getTitle(), epic.getTitle());
        assertEquals(taskManager1.getEpicById(100).getDescription(), epic.getDescription());
        assertEquals(taskManager1.getEpicById(100).getSubtaskIdList().size(), 0);
    }

}