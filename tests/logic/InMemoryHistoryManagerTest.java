package logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;
    Task task;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addToHistory() {
        // Пустая история задач.
        assertNotNull(historyManager, "История не пустая!");
        assertEquals(0, historyManager.getHistory().size(), "В пустой истории не должно быть элементов!");

        task = new Task(1, "Задача 1", "Задача для проверки дублирования.");

        historyManager.addToHistory(task);
        assertEquals(1, historyManager.getHistory().size(), "В истории должна быть одна задача!");

        // Повторное добавление той же задачи.
        historyManager.addToHistory(task);  //
        assertEquals(1, historyManager.getHistory().size(), "В истории должна быть одна задача!");
    }

    @Test
    void remove() {
        // Пустая история задач.
        assertDoesNotThrow(() -> historyManager.remove(1), "Удаление из пустой истории не должно вызывать исключений!");

        // Удаление из истории: начало, середина, конец.
        task = new Task(1, "Задача 1", "Задача для удаления из начала истории");
        historyManager.addToHistory(task);

        task = new Task(2, "Задача 2", "Промежуточная задача для тестирования удаления");
        historyManager.addToHistory(task);

        task = new Task(3, "Задача 3", "Задача для удаления из середины истории");
        historyManager.addToHistory(task);

        task = new Task(4, "Задача 3", "Задача для удаления с конца истории");
        historyManager.addToHistory(task);

        historyManager.remove(1);
        assertEquals(3, historyManager.getHistory().size(), "Задача в начале истории не была удалена!");

        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().size(), "Задача из середины истории не была удалена!");

        historyManager.remove(4);
        assertEquals(1, historyManager.getHistory().size(), "Задача в конце истории не была удалена!");
    }

    @Test
    void getHistory() {
        // Пустая история задач.
        assertNotNull(historyManager, "История не пустая!");
        assertEquals(0, historyManager.getHistory().size(), "История должна быть пустой!");

        task = new Task(1, "Задача 1", "Задача для проверки дублирования.");

        historyManager.addToHistory(task);
        assertEquals(1, historyManager.getHistory().size(), "В истории должна быть одна задача!");

        //Повторное добавление той же задачи
        historyManager.addToHistory(task);
        assertEquals(1, historyManager.getHistory().size(), "В истории должна быть одна задача!");

        // Удаление из истории: начало, середина, конец.
        task = new Task(1, "Задача 1", "Задача для удаления из начала истории");
        historyManager.addToHistory(task);

        task = new Task(2, "Задача 2", "Промежуточная задача для тестирования");
        historyManager.addToHistory(task);

        task = new Task(3, "Задача 3", "Задача для удаления из середины истории");
        historyManager.addToHistory(task);

        task = new Task(4, "Задача 3", "Задача для удаления с конца истории");
        historyManager.addToHistory(task);

        historyManager.remove(1);
        assertEquals(3, historyManager.getHistory().size(), "Задача в начале истории не была удалена!");

        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().size(), "Задача из середины истории не была удалена!");

        historyManager.remove(4);
        assertEquals(1, historyManager.getHistory().size(), "Задача в конце истории не была удалена!");
    }
}