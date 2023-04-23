package logic;


import org.junit.jupiter.api.Test;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;    //Чтобы дженерик сработал, следует его указать
    Task task;
    Task task2;
    Epic epic;
    Epic epic2;
    Subtask subtask;
    Subtask subtask2;

    @Test
    public void taskCreatorTest() {
        task = new Task("Задача 1", "Описание задачи",
                LocalDateTime.of(2023, 4, 7, 10, 25), Duration.ofMinutes(10));
        taskManager.taskCreator(task);

        assertEquals(taskManager.getTaskById(1).getTitle(), task.getTitle()); // можно так
        assertEquals(taskManager.getTaskById(1).getDescription(), task.getDescription());
        assertEquals("Задача 1", task.getTitle()); //а можно так
        assertEquals("Описание задачи", task.getDescription());
        assertEquals(LocalDateTime.of(2023, 4, 7, 10, 25), task.getStartTime());
        assertEquals(Duration.ofMinutes(10), task.getDuration());
        assertEquals(1, taskManager.getTasks().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");
    }

    @Test
    public void epicCreatorTest() {
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);

        assertEquals(taskManager.getEpicById(100).getTitle(), epic.getTitle());
        assertEquals(taskManager.getEpicById(100).getDescription(), epic.getDescription());
        assertEquals("Эпик 1", epic.getTitle());
        assertEquals("Описание эпика", epic.getDescription());
        assertEquals(taskManager.getEpicById(100).getStatus(), TaskStatus.NEW);
        assertEquals(1, taskManager.getEpics().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");
    }

    @Test
    public void subtaskCreatorTest() {
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2023, 4, 7, 10, 25), Duration.ofMinutes(10));
        taskManager.subtaskCreator(subtask);

        assertEquals(taskManager.getSubtaskById(1).getTitle(), subtask.getTitle());
        assertEquals(taskManager.getSubtaskById(1).getDescription(), subtask.getDescription());
        assertEquals(taskManager.getSubtaskById(1).getStatus(), TaskStatus.NEW);
        assertEquals(LocalDateTime.of(2023, 4, 7, 10, 25), subtask.getStartTime());
        assertEquals(Duration.ofMinutes(10), subtask.getDuration());
        assertEquals("Подзадача 1", subtask.getTitle());
        assertEquals("Описание подзадачи", subtask.getDescription());
        assertEquals(1, taskManager.getSubtasks().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");
    }

    @Test
    void getAllTasksTest() { //Проверка функции получения списка всех Тасок
        // С пустым списком
        assertDoesNotThrow(() -> taskManager.getTasks(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        // Со стандартным поведением
        task = new Task("Задача 1", "Описание задачи", TaskStatus.NEW);
        taskManager.taskCreator(task);
        assertEquals(1, taskManager.getTasks().size(), "Количество задач в менеджере не верно!");

        task2 = new Task("Задача 2", "Описание задачи", TaskStatus.NEW);
        taskManager.taskCreator(task2);
        assertEquals(2, taskManager.getTasks().size(), "Количество задач в менеджере не верно!");
    }

    @Test
    void getAllEpicsTest() { //Проверка функции получения списка всех Эпиков
        // С пустым списком
        assertDoesNotThrow(() -> taskManager.getEpics(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        // Со стандартным поведением
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        assertEquals(1, taskManager.getEpics().size(), "Количество задач в менеджере не верно!");

        epic2 = new Epic(200, "Эпик 2", "Описание эпика");
        taskManager.epicCreator(epic2);
        assertEquals(2, taskManager.getEpics().size(), "Количество задач в менеджере не верно!");
    }

    @Test
    void getAllSubtasksTest() { //Проверка функции получения списка всех Сабтасок
        // С пустым списком
        assertDoesNotThrow(() -> taskManager.getSubtasks(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        // Со стандартным поведением
        epic = new Epic(100, "Эпик 1", "Эпик для наполнения менеджера");
        taskManager.epicCreator(epic);

        subtask = new Subtask("Подзадача 1", "Подзадача для наполнения менеджера", TaskStatus.NEW, epic);
        taskManager.subtaskCreator(subtask);
        assertEquals(1, taskManager.getSubtasks().size(), "Количество подзадач в менеджере не верно!");

        subtask2 = new Subtask("Подзадача 2", "Подзадача для наполнения менеджера", TaskStatus.NEW, epic);
        taskManager.subtaskCreator(subtask2);
        assertEquals(2, taskManager.getSubtasks().size(), "Количество подзадач в менеджере не верно!");
    }


    @Test
    void getTaskByIdTest() { // Проверка функции получения задачи по идентификатору.
        // С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        assertDoesNotThrow(() -> taskManager.getTaskById(1),
                "Запрос несуществующей задачи не должен вызывать исключений!");

        // Со стандартным поведением.
        task = new Task("Задача 1", "Описание задачи", TaskStatus.NEW);
        taskManager.taskCreator(task);
        assertEquals(task, taskManager.getTaskById(1),
                "Возвращённая задача не соответствует добавленной!");
    }

    @Test
    void getEpicByIdTest() { // Проверка функции получения задачи по идентификатору.
        // С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        assertDoesNotThrow(() -> taskManager.getEpicById(1),
                "Запрос несуществующей задачи не должен вызывать исключений!");

        // Со стандартным поведением.
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        assertEquals(epic, taskManager.getEpicById(100),
                "Возвращённая задача не соответствует добавленной!");
    }

    @Test
    void getSubtaskByIdTest() { // Проверка функции получения задачи по идентификатору.
        // С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        assertDoesNotThrow(() -> taskManager.getSubtaskById(1),
                "Запрос несуществующей задачи не должен вызывать исключений!");

        // Со стандартным поведением.
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic);
        taskManager.subtaskCreator(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(1),
                "Возвращённая задача не соответствует добавленной!");
    }

    @Test
    void updateTaskTest() { //Проверка функции обновления задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
        assertEquals(0, taskManager.getTasks().size(),
                "Количество задач должно быть равно 0!");

        task = new Task("Задача 1", "Описание задачи 1",
                LocalDateTime.of(2023, 4, 7, 10, 25), Duration.ofMinutes(10));
        task2 = new Task(1, "Задача 2", "Описание задачи 2",
                LocalDateTime.of(2022, 8, 8, 8, 8), Duration.ofMinutes(50));

        taskManager.taskCreator(task);

        assertEquals(1, taskManager.getTasks().size());
        assertEquals("Задача 1", taskManager.getTasks().get(1).getTitle());
        assertEquals("Описание задачи 1", taskManager.getTasks().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getTasks().get(1).getStatus());
        assertEquals(LocalDateTime.of(2023, 4, 7, 10, 25), taskManager.getTasks().get(1).getStartTime());
        assertEquals(Duration.ofMinutes(10), taskManager.getTasks().get(1).getDuration());

        taskManager.updateTask(task2);

        assertEquals("Задача 2", taskManager.getTasks().get(1).getTitle());
        assertEquals("Описание задачи 2", taskManager.getTasks().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getTasks().get(1).getStatus());
        assertEquals(LocalDateTime.of(2022, 8, 8, 8, 8), taskManager.getTasks().get(1).getStartTime());
        assertEquals(Duration.ofMinutes(50), taskManager.getTasks().get(1).getDuration());
    }

    @Test
    void updateEpicTest() {
        epic = new Epic(100, "Эпик 1", "Описание эпика 1");
        epic2 = new Epic(100, "Эпик 2", "Описание эпика 2");

        taskManager.epicCreator(epic);

        assertEquals(1, taskManager.getEpics().size());
        assertEquals("Эпик 1", taskManager.getEpics().get(100).getTitle());
        assertEquals("Описание эпика 1", taskManager.getEpics().get(100).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getEpics().get(100).getStatus());

        taskManager.updateEpic(epic2);

        assertEquals("Эпик 2", taskManager.getEpics().get(100).getTitle());
        assertEquals("Описание эпика 2", taskManager.getEpics().get(100).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getEpics().get(100).getStatus());
    }

    @Test
    void updateSubtaskTest() {
        assertEquals(0, taskManager.getEpics().size(), "Список Эпиков должен быть пуст!");
        assertEquals(0, taskManager.getSubtasks().size(), "Список Подзадач должен быть пуст!");

        epic = new Epic(100, "Эпик 1", "Описание эпика 1");
        subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epic);
        subtask2 = new Subtask(1, "Подзадача 2", "Описание подзадачи 2", epic);

        taskManager.epicCreator(epic);
        taskManager.subtaskCreator(subtask);

        assertEquals("Эпик 1", taskManager.getEpics().get(100).getTitle());
        assertEquals("Описание эпика 1", taskManager.getEpics().get(100).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getEpics().get(100).getStatus());

        assertEquals("Подзадача 1", taskManager.getSubtasks().get(1).getTitle());
        assertEquals("Описание подзадачи 1", taskManager.getSubtasks().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getSubtasks().get(1).getStatus());

        taskManager.updateSubtask(subtask2);

        assertEquals(subtask2.getTitle(), taskManager.getSubtasks().get(1).getTitle());
        assertEquals(subtask2.getDescription(), taskManager.getSubtasks().get(1).getDescription());
        assertEquals("Подзадача 2", taskManager.getSubtasks().get(1).getTitle());
        assertEquals("Описание подзадачи 2", taskManager.getSubtasks().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getSubtasks().get(1).getStatus());
    }

    @Test
    void deleteTaskTest() { //Проверка функции удаления ранее добавленных задач — всех И по идентификатору.
        // С пустым списком задач.
        assertDoesNotThrow(() -> taskManager.deleteTaskById(1),
                "Попытка удаления несуществующей задачи не должна вызывать исключение!");

        // Со стандартным поведением.
        task = new Task("Задача 1", "Задача для проверки удаления", TaskStatus.NEW);
        taskManager.taskCreator(task);
        task2 = new Task("Задача 2", "Задача для проверки удаления", TaskStatus.NEW);
        taskManager.taskCreator(task2);

        assertEquals(2, taskManager.getTasks().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");

        taskManager.deleteTaskById(1);
        assertEquals(1, taskManager.getTasks().size(),
                "Количество задач в менеджере после удаления задачи не верно!");

        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size(),
                "После удаления всех задач список не пустой!");
    }

    @Test
    void deleteEpicTest() { //Проверка функции удаления ранее добавленных эпиков — всех И по идентификатору.
        // С пустым списком задач.
        assertDoesNotThrow(() -> taskManager.deleteEpicById(1),
                "Попытка удаления несуществующей задачи не должна вызывать исключение!");

        // Со стандартным поведением.
        epic = new Epic(100, "Эпик 1", "Эпик для проверки удаления");
        taskManager.epicCreator(epic);
        epic2 = new Epic(200, "Эпик 2", "Эпик для проверки удаления");
        taskManager.epicCreator(epic2);

        assertEquals(2, taskManager.getEpics().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");

        taskManager.deleteEpicById(100);
        assertEquals(1, taskManager.getEpics().size(),
                "Количество задач в менеджере после удаления задачи не верно!");

        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpics().size(),
                "После удаления всех задач список не пустой!");
    }

    @Test
    void deleteSubtaskTest() { //Проверка функции удаления ранее добавленных эпиков — всех И по идентификатору.
        // С пустым списком задач.
        assertDoesNotThrow(() -> taskManager.deleteSubtaskById(1),
                "Попытка удаления несуществующей задачи не должна вызывать исключение!");

        // Со стандартным поведением.
        epic = new Epic(100, "Эпик 1", "Эпик для проверки удаления");
        taskManager.epicCreator(epic);
        subtask = new Subtask("Подзадача 1", "Подзадача для проверки удаления", TaskStatus.NEW, epic);
        taskManager.subtaskCreator(subtask);
        subtask2 = new Subtask("Подзадача 2", "Подзадача для проверки удаления", TaskStatus.NEW, epic);
        taskManager.subtaskCreator(subtask2);

        assertEquals(2, taskManager.getSubtasks().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");

        taskManager.deleteSubtaskById(2);
        assertEquals(1, taskManager.getSubtasks().size(),
                "Количество задач в менеджере после удаления задачи не верно!");

        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getSubtasks().size(),
                "После удаления всех задач список не пустой!");
    }

    //Проверка функции получения просмотренных задач, полученных через getTaskById(), изменённых updateTask() или удалённых deleteTask()
    @Test
    void historyTest() {
        // С пустым списком задач.
        assertDoesNotThrow(() -> taskManager.getTaskHistory(),
                "Запрос пустой истории обращений не должен вызывать исключений!");
        assertNotNull(taskManager.getTaskHistory(), "История не пустая!");

        // Со стандартным поведением.
        task = new Task("Задача 1", "Задача для тестирования");
        taskManager.taskCreator(task);
        epic = new Epic(100, "Эпик 1", "Эпик для тестирования");
        taskManager.epicCreator(epic);
        subtask = new Subtask("Подзадача 1", "Подзадача для тестирования", TaskStatus.NEW, epic);
        taskManager.subtaskCreator(subtask);

        assertEquals(0, taskManager.getTaskHistory().size(),
                "Размер истории обращений не верен!");

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(2);
        taskManager.getEpicById(100);
        assertEquals(3, taskManager.getTaskHistory().size(),
                "Размер истории обращений не верен!");

        task2 = new Task(1, "Задача 2", "Задача для тестирования");

        taskManager.updateTask(task2); // задача обновилась, но она уже есть в истории, кол-во записей в истории прежнее
        assertEquals(3, taskManager.getTaskHistory().size(),
                "Размер истории обращений не верен!");

        taskManager.deleteTaskById(1); // задача удалилась, следовательно, ее нужно удалить из истории, кол-во записей в истории: 2
        assertEquals(2, taskManager.getTaskHistory().size(),
                "Размер истории обращений не верен!");
    }

    //Проверка функции получения отсортированного списка всех задач
    @Test
    void getPrioritizedTasksTest(){
        assertNotNull(taskManager.getPrioritizedTasks(), "Список задач не пустой!");

        epic = new Epic(200,"Эпик 1", "Пустой эпик");
        taskManager.epicCreator(epic);

        subtask = new Subtask("Подзадача 1",
                "Описание подзадачи 1", TaskStatus.NEW,
                epic,
                LocalDateTime.now().plusMinutes(100),
                Duration.ofMinutes(30));
        taskManager.subtaskCreator(subtask); // первая задача в отсортированном списке

        subtask2 = new Subtask("Подзадача 2",
                "Описание подзадачи 2", TaskStatus.NEW,
                epic,
                LocalDateTime.now().plusMinutes(300),
                Duration.ofHours(1).plusMinutes(30));
        taskManager.subtaskCreator(subtask2); // вторая задача в отсортированном списке

        task = new Task(100,
                "Задача 1",
                "Задача для наполнения менеджера",
                LocalDateTime.now().plusMinutes(200),
                Duration.ofHours(1).plusMinutes(15));
        taskManager.taskCreator(task); // третья задача в отсортированном списке

        assertEquals(3, taskManager.getPrioritizedTasks().size(),
                "Количество задач в отсортированном списке не верно!");
    }


}