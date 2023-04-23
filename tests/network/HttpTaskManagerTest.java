package network;

import org.junit.jupiter.api.Assertions;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

class HttpTaskManagerTest {
    HttpTaskManager manager;
    KVServer server;
    URI uriKVServer;

    @BeforeEach
    void start() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        uriKVServer = KVServer.getServerURL();
        manager = new HttpTaskManager(uriKVServer);
    }

    @AfterEach
    public void stopKVServer() {
        server.stop();
    }

    @Test
    void getTest() {
        Task task1 = new Task("2001", "11",
                LocalDateTime.of(2001, 1, 1, 1, 1), Duration.ofMinutes(20));
        Epic epic1 = new Epic("эпик включающий2009", "55");
        Subtask subtask1 = new Subtask("2009", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1), Duration.ofMinutes(20));
        Task taskForEquals = task1;
        taskForEquals.setId(1);
        Epic epic1ForEquals = epic1;
        epic1ForEquals.setId(2);
        Subtask subtask1ForEquals = subtask1;
        subtask1ForEquals.setId(3);

        HashMap<Integer, Task> taskHashMap = new HashMap<>();
        HashMap<Integer, Epic> epicHashMap = new HashMap<>();
        HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
        taskHashMap.put(1, taskForEquals);
        epicHashMap.put(2, epic1ForEquals);
        subtaskHashMap.put(3, subtask1ForEquals);
        manager.taskCreator(task1);
        manager.epicCreator(epic1);
        manager.subtaskCreator(subtask1);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(manager.getTaskById(1), taskHashMap.get(1), "таск не совпадает");
        Assertions.assertEquals(manager.getEpicById(2), epicHashMap.get(2), "эпик не совпадает");
        Assertions.assertEquals(manager.getSubtaskById(3), subtaskHashMap.get(3), "сабтаск не совпадает");
        Assertions.assertEquals(manager.getEpicById(2).getSubtaskIdList(), epicHashMap.get(2).getSubtaskIdList(),
                "внутреннай сабтаск не совпадает");
        Assertions.assertEquals(manager.getTasks(), taskHashMap, "хэшмап тасков не совпадает");
        Assertions.assertEquals(manager.getEpics(), epicHashMap, "хэшмап эпиков не совпадает");
        Assertions.assertEquals(manager.getSubtasks(), subtaskHashMap, "хэшмап сабтасков не совпадает");
    }

    @Test
    void deleteTest() {
        Task task1 = new Task("2001", "11",
                LocalDateTime.of(2001, 1, 1, 1, 1), Duration.ofMinutes(20));
        Epic epic1 = new Epic("эпик включающий2009", "55");
        Subtask subtask1 = new Subtask("2009", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1), Duration.ofMinutes(20));

        manager.taskCreator(task1);
        manager.epicCreator(epic1);
        manager.subtaskCreator(subtask1);
        manager.loadManagerFromKVServer();
        manager.deleteTaskById(1);
        manager.deleteSubtaskById(3);
        manager.deleteEpicById(2);
        Assertions.assertNull(manager.getTaskById(1), "таск не удален");
        Assertions.assertNull(manager.getEpicById(2), "эпик не удален");
        Assertions.assertNull(manager.getSubtaskById(3), "сабтаск не удален");

        manager.taskCreator(task1);
        manager.epicCreator(epic1);
        manager.subtaskCreator(subtask1);
        manager.loadManagerFromKVServer();
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        Assertions.assertNull(manager.getTaskById(4), "таски не удалены");
        Assertions.assertNull(manager.getEpicById(5), "эпики не удалены");
        Assertions.assertNull(manager.getSubtaskById(6), "сабтаски не удалены");
    }
}