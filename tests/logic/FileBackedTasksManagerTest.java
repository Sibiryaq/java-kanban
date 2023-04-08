package logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {
    FileBackedTasksManager taskManager1;

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(new File("data/save_tasks.txt"));
    }

    @Test
    void saveTest() {
        // Пустой список задач.
        assertDoesNotThrow(() -> ((FileBackedTasksManager) taskManager).save(), "Сохранение менеджера с пустым списком задач не должно вызывать исключений!");

        //b. Эпик без подзадач.
        epic1 = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic1);
        ((FileBackedTasksManager) taskManager).save();
        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());
        assertEquals(1, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");

        // Пустой список истории.
        assertEquals(0, taskManager1.history().size(), "Количество задач в истории обращения после восстановления не совпало!");

    }

    //Тестирование метода загрузки списка задач из файла
    @Test
    void loadFromFileTest() {   //public static FileBackedTasksManager loadFromFile(Path file)
        // Эпик без подзадач.
        epic1 = new Epic("Эпик 1", "Пустой эпик");
        taskManager.epicCreator(epic1);
        ((FileBackedTasksManager) taskManager).save();

        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());
        assertEquals(1, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");

        // Пустой список истории.
        assertEquals(0, taskManager1.history().size(), "Количество задач в истории обращения после восстановления не совпало!");

        // Со стандартным поведением.
        epic1 = new Epic("Эпик 2", "Эпик с подзадачами");
        taskManager.epicCreator(epic1);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask1);

        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask2);

        task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(2).plusMinutes(15));
        taskManager.taskCreator(task1);

        taskManager.getTaskById(5);   //Добавление истории

        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());
        assertEquals(taskManager1.getTaskById(5).getTitle(), task1.getTitle());
        assertEquals(taskManager1.getTaskById(5).getDescription(), task1.getDescription());
        assertEquals(taskManager1.getTaskById(5).getId(), task1.getId());
        assertEquals(1, taskManager1.getTasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(2, taskManager1.getSubtasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(2, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(1, taskManager1.history().size(), "Количество задач в истории обращения после восстановления не совпало!");
    }

    @Test
    public void loadFromFileAnotherTest() {
        assertEquals(0, taskManager.history().size());
        task1 = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.taskCreator(task1);
        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());
        assertEquals(taskManager1.getTaskById(1).getTitle(), task1.getTitle());
        assertEquals(taskManager1.getTaskById(1).getDescription(), task1.getDescription());
        assertEquals(taskManager1.getTaskById(1).getId(), task1.getId());
    }

    @Test
    public void testingFileEpicWithoutSubtasks() {
        epic1 = new Epic("Эпик", "Описание");
        taskManager.epicCreator(epic1);
        taskManager1 = FileBackedTasksManager.loadFromFile(Paths.get("data/save_tasks.txt").toFile());

        // c. Пустой список истории.
        assertEquals(0, taskManager1.history().size());
        assertEquals(taskManager1.getEpicById(1).getTitle(), epic1.getTitle());
        assertEquals(taskManager1.getEpicById(1).getDescription(), epic1.getDescription());
        assertEquals(taskManager1.getEpicById(1).getSubtaskIdList().size(), 0);
    }

}