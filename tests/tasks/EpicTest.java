package tasks;

import logic.Managers;
import logic.TaskManager;
import logic.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        TaskStatus epicsStatus = taskManager.getEpicById(1).getStatus();
        assertEquals(epicsStatus, TaskStatus.NEW);
    }

    //  b. Все подзадачи со статусом NEW.
    @Test
    public void testingForEpicAllSubtasksWithStatusNew() {
        epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        TaskStatus epicsStatus = taskManager.getEpicById(1).getStatus();
        assertEquals(epicsStatus, TaskStatus.NEW);
    }

    //  c. Все подзадачи со статусом DONE.
    @Test
    public void testingForEpicAllSubtasksWithStatusDone() {
        epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.DONE, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        TaskStatus epicsStatus = taskManager.getEpicById(1).getStatus();
        assertEquals(epicsStatus, TaskStatus.DONE);
    }

    //  d. Подзадачи со статусами NEW и DONE.
    @Test
    public void testingForEpicSubtasksWithStatusNewAndDone() {
        epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.DONE, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        TaskStatus epicsStatus = taskManager.getEpicById(1).getStatus();
        assertEquals(epicsStatus, TaskStatus.IN_PROGRESS);
    }

    //  e. Подзадачи со статусом IN_PROGRESS.
    @Test
    public void testingForEpicSubtasksWithStatusInProgress() {
        epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.IN_PROGRESS, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.IN_PROGRESS, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        TaskStatus epicsStatus = taskManager.getEpicById(1).getStatus();
        assertEquals(epicsStatus, TaskStatus.IN_PROGRESS);
    }
}