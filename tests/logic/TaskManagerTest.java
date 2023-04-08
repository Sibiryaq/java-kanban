package logic;


import org.junit.jupiter.api.Test;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    TaskManager taskManager;    //Получение менеджера задач
    Task task1;
    Task task2;
    Epic epic1;
    Epic epic2;
    Subtask subtask1;
    Subtask subtask2;

    @Test
    public void taskCreatorTest() {
        task1 = new Task("Задача 1", "Описание задачи", TaskStatus.NEW,
                LocalDateTime.of(2023, 4, 7, 10, 25), Duration.ofMinutes(10));
        taskManager.taskCreator(task1);

        assertEquals(taskManager.getTaskById(1).getTitle(), task1.getTitle()); // можно так
        assertEquals(taskManager.getTaskById(1).getDescription(), task1.getDescription());
        assertEquals("Задача 1", task1.getTitle()); //а можно так
        assertEquals("Описание задачи", task1.getDescription());
        assertEquals(LocalDateTime.of(2023, 4, 7, 10, 25), task1.getStartTime());
        assertEquals(Duration.ofMinutes(10), task1.getDuration());
        assertEquals(1, taskManager.getTasks().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");
    }

    @Test
    public void epicCreatorTest() {
        epic1 = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW,
                LocalDateTime.of(2023, 4, 7, 10, 25), Duration.ofMinutes(10));
        taskManager.epicCreator(epic1);

        assertEquals(taskManager.getEpicById(1).getTitle(), epic1.getTitle());
        assertEquals(taskManager.getEpicById(1).getDescription(), epic1.getDescription());
        assertEquals("Эпик 1", epic1.getTitle());
        assertEquals("Описание эпика", epic1.getDescription());
        assertEquals(taskManager.getEpicById(1).getStatus(), TaskStatus.NEW);
        assertEquals(LocalDateTime.of(2023, 4, 7, 10, 25), epic1.getStartTime());
        assertEquals(Duration.ofMinutes(10), epic1.getDuration());
        assertEquals(1, taskManager.getEpics().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");
    }

    @Test
    public void subtaskCreatorTest() {
        epic1 = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic1);

        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask1);

        assertEquals(taskManager.getSubtaskById(2).getTitle(), subtask1.getTitle());
        assertEquals(taskManager.getSubtaskById(2).getDescription(), subtask1.getDescription());
        assertEquals(taskManager.getSubtaskById(2).getStatus(), TaskStatus.NEW);
        assertEquals("Подзадача 1", subtask1.getTitle());
        assertEquals("Описание подзадачи", subtask1.getDescription());
        assertEquals(1, taskManager.getSubtasks().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");
    }

    @Test
    void getAllTasksTest() { //Проверка функции получения списка всех Тасок
        // С пустым списком
        assertDoesNotThrow(() -> taskManager.getTasks(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        // Со стандартным поведением
        task1 = new Task("Задача 1", "Описание задачи", TaskStatus.NEW);
        taskManager.taskCreator(task1);
        assertEquals(1, taskManager.getTasks().size(), "Количество задач в менеджере не верно!");
    }

    @Test
    void getAllEpicsTest() { //Проверка функции получения списка всех Эпиков
        // С пустым списком
        assertDoesNotThrow(() -> taskManager.getEpics(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        // Со стандартным поведением
        epic1 = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic1);
        assertEquals(1, taskManager.getEpics().size(), "Количество задач в менеджере не верно!");
    }

    @Test
    void getAllSubtasksTest() { //Проверка функции получения списка всех Сабтасок
        // С пустым списком
        assertDoesNotThrow(() -> taskManager.getSubtasks(),
                "Запрос пустого списка задач не должен вызывать исключений!");

        // Со стандартным поведением
        epic1 = new Epic("Эпик 1", "Эпик для наполнения менеджера");
        taskManager.epicCreator(epic1);

        subtask1 = new Subtask("Подзадача 1", "Подзадача для наполнения менеджера", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask1);

        subtask2 = new Subtask("Подзадача 2", "Подзадача для наполнения менеджера", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask2);

        assertEquals(2, taskManager.getSubtasks().size(), "Количество подзадач в менеджере не верно!");
    }


    @Test
    void getTaskByIdTest() { // Проверка функции получения задачи по идентификатору.
        // С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        assertDoesNotThrow(() -> taskManager.getTaskById(1),
                "Запрос несуществующей задачи не должен вызывать исключений!");

        // Со стандартным поведением.
        task1 = new Task("Задача 1", "Описание задачи", TaskStatus.NEW);
        taskManager.taskCreator(task1);
        assertEquals(task1, taskManager.getTaskById(1),
                "Возвращённая задача не соответствует добавленной!");
    }

    @Test
    void getEpicByIdTest() { // Проверка функции получения задачи по идентификатору.
        // С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        assertDoesNotThrow(() -> taskManager.getEpicById(1),
                "Запрос несуществующей задачи не должен вызывать исключений!");

        // Со стандартным поведением.
        epic1 = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic1);
        assertEquals(epic1, taskManager.getEpicById(1),
                "Возвращённая задача не соответствует добавленной!");
    }

    @Test
    void getSubtaskByIdTest() { // Проверка функции получения задачи по идентификатору.
        // С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
        assertDoesNotThrow(() -> taskManager.getSubtaskById(1),
                "Запрос несуществующей задачи не должен вызывать исключений!");

        // Со стандартным поведением.
        epic1 = new Epic("Эпик 1", "Описание эпика");
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask1);

        assertEquals(subtask1, taskManager.getSubtaskById(1),
                "Возвращённая задача не соответствует добавленной!");
    }


    @Test
    void updateTaskTest() { //Проверка функции обновления задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
        assertEquals(0, taskManager.getTasks().size());

        task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task2 = new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.NEW);

        taskManager.taskCreator(task1);

        assertEquals(1, taskManager.getTasks().size());
        assertEquals("Задача 1", taskManager.getTasks().get(1).getTitle());
        assertEquals("Описание задачи 1", taskManager.getTasks().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getTasks().get(1).getStatus());

        taskManager.updateTask(task2);

        assertEquals("Задача 2", taskManager.getTasks().get(1).getTitle());
        assertEquals("Описание задачи 2", taskManager.getTasks().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getTasks().get(1).getStatus());
    }

    @Test
    void updateEpicTest() {
        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        epic2 = new Epic(1, "Эпик 2", "Описание эпика 2");

        taskManager.epicCreator(epic1);

        assertEquals(1, taskManager.getEpics().size());
        assertEquals("Эпик 1", taskManager.getEpics().get(1).getTitle());
        assertEquals("Описание эпика 1", taskManager.getEpics().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getEpics().get(1).getStatus());

        taskManager.updateEpic(epic2);

        assertEquals("Эпик 2", taskManager.getEpics().get(1).getTitle());
        assertEquals("Описание эпика 2", taskManager.getEpics().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getEpics().get(1).getStatus());
    }

    @Test
    void updateSubtaskTest() {
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());

        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1);
        subtask2 = new Subtask(2, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1);

        taskManager.epicCreator(epic1);
        taskManager.subtaskCreator(subtask1);

        assertEquals("Эпик 1", taskManager.getEpics().get(1).getTitle());
        assertEquals("Описание эпика 1", taskManager.getEpics().get(1).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getEpics().get(1).getStatus());

        assertEquals("Подзадача 1", taskManager.getSubtasks().get(2).getTitle());
        assertEquals("Описание подзадачи 1", taskManager.getSubtasks().get(2).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getSubtasks().get(2).getStatus());

        taskManager.updateSubtask(subtask2);

        assertEquals(subtask2.getTitle(), taskManager.getSubtasks().get(2).getTitle());
        assertEquals(subtask2.getDescription(), taskManager.getSubtasks().get(2).getDescription());
        assertEquals("Подзадача 2", taskManager.getSubtasks().get(2).getTitle());
        assertEquals("Описание подзадачи 2", taskManager.getSubtasks().get(2).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getSubtasks().get(2).getStatus());
    }

    @Test
    void deleteTaskTest() { //Проверка функции удаления ранее добавленных задач — всех и по идентификатору.
        // С пустым списком задач.
        assertDoesNotThrow(() -> taskManager.deleteTask(1),
                "Попытка удаления несуществующей задачи не должна вызывать исключение!");

        // Со стандартным поведением.
        task1 = new Task(1, "Задача 1", "Задача для проверки удаления", TaskStatus.NEW);
        taskManager.taskCreator(task1);
        task2 = new Task(2, "Задача 2", "Задача для проверки удаления", TaskStatus.NEW);
        taskManager.taskCreator(task2);

        assertEquals(2, taskManager.getTasks().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");

        taskManager.deleteTask(1);
        assertEquals(1, taskManager.getTasks().size(),
                "Количество задач в менеджере после удаления задачи не верно!");

        taskManager.deleteTaskList();
        assertEquals(0, taskManager.getTasks().size(),
                "После удаления всех задач список не пустой!");
    }

    @Test
    void deleteEpicTest() { //Проверка функции удаления ранее добавленных эпиков — всех и по идентификатору.
        // С пустым списком задач.
        assertDoesNotThrow(() -> taskManager.deleteEpic(1),
                "Попытка удаления несуществующей задачи не должна вызывать исключение!");

        // Со стандартным поведением.
        epic1 = new Epic("Эпик 1", "Эпик для проверки удаления");
        taskManager.epicCreator(epic1);
        epic2 = new Epic("Эпик 2", "Эпик для проверки удаления");
        taskManager.epicCreator(epic2);

        assertEquals(2, taskManager.getEpics().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");

        taskManager.deleteEpic(1);
        assertEquals(1, taskManager.getEpics().size(),
                "Количество задач в менеджере после удаления задачи не верно!");

        taskManager.deleteEpicList();
        assertEquals(0, taskManager.getEpics().size(),
                "После удаления всех задач список не пустой!");
    }

    @Test
    void deleteSubtaskTest() { //Проверка функции удаления ранее добавленных эпиков — всех и по идентификатору.
        // С пустым списком задач.
        assertDoesNotThrow(() -> taskManager.deleteSubtask(1),
                "Попытка удаления несуществующей задачи не должна вызывать исключение!");

        // Со стандартным поведением.
        epic1 = new Epic("Эпик 1", "Эпик для проверки удаления");
        taskManager.epicCreator(epic1);
        subtask1 = new Subtask("Подзадача 1", "Подзадача для проверки удаления", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask1);
        subtask2 = new Subtask("Подзадача 2", "Подзадача для проверки удаления", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask2);

        assertEquals(2, taskManager.getSubtasks().size(),
                "Количество задач в менеджере после добавления новой задачи не верно!");

        taskManager.deleteSubtask(2);
        assertEquals(1, taskManager.getSubtasks().size(),
                "Количество задач в менеджере после удаления задачи не верно!");

        taskManager.deleteSubtaskList();
        assertEquals(0, taskManager.getSubtasks().size(),
                "После удаления всех задач список не пустой!");
    }

    //Проверка функции получения просмотренных задач, полученных через getTaskById(), изменённых updateTask() или удалённых deleteTask()
    @Test
    void historyTest() {
        // С пустым списком задач.
        assertDoesNotThrow(() -> taskManager.history(),
                "Запрос пустой истории обращений не должен вызывать исключений!");

        assertNotNull(taskManager.history(), "История не пустая!");

        // Со стандартным поведением.
        task1 = new Task("Задача 1", "Задача для тестирования", TaskStatus.NEW);
        taskManager.taskCreator(task1);
        epic1 = new Epic("Эпик 1", "Эпик для тестирования");
        taskManager.epicCreator(epic1);
        subtask1 = new Subtask("Подзадача 1", "Подзадача для тестирования", TaskStatus.NEW, epic1);
        taskManager.subtaskCreator(subtask1);

        assertEquals(0, taskManager.history().size(),
                "Размер истории обращений не верен!");

        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        assertEquals(3, taskManager.history().size(),
                "Размер истории обращений не верен!");


        task2 = new Task(1, "Задача 2", "Задача для тестирования", TaskStatus.NEW);
        taskManager.updateTask(task2); // задача обновилась, но она уже есть в истории, кол-во записей в истории прежнее

        assertEquals(3, taskManager.history().size(),
                "Размер истории обращений не верен!");

        taskManager.deleteTask(1); // задача удалилась, следовательно, ее нужно удалить из истории, кол-во записей в истории: 2
        assertEquals(2, taskManager.history().size(),
                "Размер истории обращений не верен!");
    }


}