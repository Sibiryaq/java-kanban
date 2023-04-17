package tasks;

import logic.Managers;
import logic.TaskManager;
import logic.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //  a. Пустой список подзадач.
    @Test
    public void testingForEpicEmptyListOfSubtasks() {
        epic = new Epic(1, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        assertEquals(TaskStatus.NEW, taskManager.getEpicById(1).getStatus());
        assertNull(epic.getDuration());
    }

    //  b. Все подзадачи со статусом NEW.
    @Test
    public void testingForEpicAllSubtasksWithStatusNew() {
        epic = new Epic(100, "Эпик 1", "Описание эпика 1");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2022, 8, 6, 8, 8), Duration.ofMinutes(10));
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2022, 8, 8, 8, 8), Duration.ofMinutes(25));
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    //  c. Все подзадачи со статусом DONE.
    @Test
    public void testingForEpicAllSubtasksWithStatusDone() {
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.DONE, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    //  d. Подзадачи со статусами NEW и DONE.
    @Test
    public void testingForEpicSubtasksWithStatusNewAndDone() {
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    //  e. Подзадачи со статусом IN_PROGRESS.
    @Test
    public void testingForEpicSubtasksWithStatusInProgress() {
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.IN_PROGRESS, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.IN_PROGRESS, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void EpicDurationTest() {
        epic = new Epic(100, "Эпик 1", "Описание эпика 1");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2022, 8, 8, 7, 8), Duration.ofMinutes(10));
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2022, 8, 8, 8, 8), Duration.ofMinutes(25));
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(Duration.ofMinutes(35), epic.getDuration(),
                "Продолжительность Эпика не равна сумме продолжительности Подзадач!");
    }

    @Test
    public void EpicDurationWithOneSubtaskNullDurationTest() {
        epic = new Epic(100, "Эпик 1", "Описание эпика 1");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2022, 8, 6, 8, 8), null);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2022, 8, 8, 8, 8), Duration.ofMinutes(25));
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(Duration.ofMinutes(25), epic.getDuration(),
                "Продолжительность Эпика не равна сумме продолжительности Подзадач!"); //0+25=25
    }
}