package logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import servers.KVServer;
import tasks.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTasksManagerTest{
    static KVServer server;
    HttpTaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subTask1;
    Subtask subTask2;
    @BeforeAll
    public static void beforeAll() throws IOException {
        server = new KVServer();
        server.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        server.stop();
    }

    //Тестирование метода сохранения задач в базу
    @Test
    void save() {
        taskManager =  new HttpTaskManager();
        assertNotNull(taskManager.getPrioritizedTasks(), "Список задач не пустой!");

        epic = new Epic(200,"Эпик 1", "Пустой эпик");
        taskManager.epicCreator(epic);

        epic = new Epic(300,"Эпик 2", "Эпик с подзадачами");
        taskManager.epicCreator(epic);

        subTask1 = new Subtask("Собрать коробки", "Коробки на чердаке", TaskStatus.NEW, epic,
                LocalDateTime.now().plusMinutes(100), Duration.ofMinutes(30));
        taskManager.subtaskCreator(subTask1);

        subTask2 = new Subtask("Упаковать кошку", "Переноска за дверью", TaskStatus.NEW, epic,
                LocalDateTime.now().plusMinutes(300), Duration.ofHours(1).plusMinutes(30));
        taskManager.subtaskCreator(subTask2);

        task = new Task(100, "Задача 1", "Задача для наполнения менеджера",
                LocalDateTime.now().plusMinutes(200), Duration.ofHours(1).plusMinutes(15));
        taskManager.taskCreator(task);

        assertEquals(3, taskManager.getPrioritizedTasks().size(),
                "Количество задач в отсортированном списке не верно!"); // Всего задач - 5, но в список попадет только - 3

        //Обращения к задачам для формирования истории обращений
        taskManager.getSubtaskById(1);
        taskManager.getTaskById(100);
        taskManager.getEpicById(200);
        taskManager.getEpicById(300);

        assertEquals(4, taskManager.history().size(),
                "Количество задач в истории не верно!");

    }

    //Тестирование метода восстановления задач с сервера
    @Test
    void loadFromJson() {
        HttpTaskManager taskManager =  new HttpTaskManager("PrimaryManager");
        assertNotNull(taskManager.getPrioritizedTasks(), "Список задач не пустой!");

        //Формирование первичного менеджера задач
        epic = new Epic(200,"Эпик 1", "Пустой эпик");
        taskManager.epicCreator(epic);

        epic = new Epic(300,"Эпик 2", "Эпик с подзадачами");
        taskManager.epicCreator(epic);

        subTask1 = new Subtask("Собрать коробки", "Коробки на чердаке", TaskStatus.NEW, epic,
                LocalDateTime.now().plusMinutes(100), Duration.ofMinutes(30));
        taskManager.subtaskCreator(subTask1);

        subTask2 = new Subtask("Упаковать кошку", "Переноска за дверью", TaskStatus.NEW, epic,
                LocalDateTime.now().plusMinutes(300), Duration.ofHours(1).plusMinutes(30));
        taskManager.subtaskCreator(subTask2);

        task = new Task(100, "Задача 1", "Задача для наполнения менеджера",
                LocalDateTime.now().plusMinutes(200), Duration.ofHours(1).plusMinutes(15));
        taskManager.taskCreator(task);

        assertEquals(3, taskManager.getPrioritizedTasks().size(),
                "Количество задач в отсортированном списке не верно!");

        //Обращения к задачам для формирования истории обращений
        taskManager.getSubtaskById(1);
        taskManager.getTaskById(100);
        taskManager.getEpicById(200);
        taskManager.getEpicById(300);

        //Создание нового менеджера задач на основе образа с сервера
        HttpTaskManager taskManager1 = HttpTaskManager.loadFromJson("PrimaryManager", "CopyOfPrimaryManager");

        assertEquals(3, taskManager1.getPrioritizedTasks().size(),
                "Количество задач в списке приоритетов после восстаносления с сервера не верно!");

        assertEquals(4, taskManager1.history().size(),
                "Количество задач в истории обращений после восстаносления с сервера не верно!");

        assertEquals(4, taskManager1.history().size(),
                "Количество задач в истории обращений после восстаносления с сервера не верно!");
    }
}