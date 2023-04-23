package network;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.junit.jupiter.api.Assertions;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

class HttpTaskServerTest {
    HttpTaskManager manager;
    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        URI uriKVServer = KVServer.getServerURL();
        manager = new HttpTaskManager(uriKVServer);
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void stopKVServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    //Проверка получения
    @Test
    void getAllTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("title", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        Task task2 = new Task("title", "description",
                LocalDateTime.of(2002, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.taskCreator(task1);
        manager.taskCreator(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<Integer, Task> mapFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        Assertions.assertEquals(manager.getTasks().toString(), mapFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getTasksByIdTest() throws IOException, InterruptedException {
        Task task1 = new Task("title", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.taskCreator(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromServer = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(task1.toString(), taskFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getAllEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        Epic epic2 = new Epic("эпик включающий2010", "56");
        manager.epicCreator(epic1);
        manager.epicCreator(epic2);
        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        Subtask subtask4 = new Subtask("2010", "66", TaskStatus.DONE, epic2,
                LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);
        manager.subtaskCreator(subtask4);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<Integer, Epic> mapFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());
        Assertions.assertEquals(manager.getEpics().toString(), mapFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getTasksEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);

        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);
        Assertions.assertEquals(epic1.toString(), epicFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getAllSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        Epic epic2 = new Epic("эпик включающий2010", "56");
        manager.epicCreator(epic1);
        manager.epicCreator(epic2);
        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        Subtask subtask4 = new Subtask("2010", "66", TaskStatus.DONE, epic2,
                LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);
        manager.subtaskCreator(subtask4);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<Integer, Subtask> mapFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType());
        Assertions.assertEquals(manager.getSubtasks().toString(), mapFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);

        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);
        System.out.println(manager.getSubtasks());

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask taskFromServer = gson.fromJson(response.body(), Subtask.class);
        System.out.println(taskFromServer);
        Assertions.assertEquals(subtask3.toString(), taskFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getSubtaskOfEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);

        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<Integer, Subtask> listFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType());
        Assertions.assertEquals(listFromServer.toString(), epic1.getSubtaskIdList().toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("title", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        Task task2 = new Task("title", "description",
                LocalDateTime.of(2002, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.taskCreator(task1);
        manager.taskCreator(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> listFromServer = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        TreeSet<Task> treeFromServer = new TreeSet<>(listFromServer);


        Assertions.assertEquals(manager.getPrioritizedTasks().toString(), treeFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getTaskHistoryTest() throws IOException, InterruptedException {
        Task task1 = new Task("title", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        Task task2 = new Task("title", "description",
                LocalDateTime.of(2002, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.taskCreator(task1);
        manager.taskCreator(task2);
        manager.getTaskById(2);
        manager.getTaskById(1);
        System.out.println(manager.getTaskHistory());
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Task> arrayListFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        Assertions.assertEquals(manager.getTaskHistory().toString(), arrayListFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }


    //Проверка добавления
    @Test
    void taskCreatorTest() throws IOException, InterruptedException {
        Task task1 = new Task("2001добавленный", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task1));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(task1.getTitle(), manager.getTaskById(1).getTitle());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("title", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.taskCreator(task1);

        Task task2 = new Task("newTile", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));

        System.out.println(gson.toJson(task2));
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task2));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String name1 = manager.getTaskById(1).getTitle();
        String name2 = task2.getTitle();

        Assertions.assertEquals(name1, name2);
        Assertions.assertEquals(200, response.statusCode());

    }

    @Test
    void epicCreatorTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        epic1.setStartTime(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        epic1.setEndTime(LocalDateTime.of(2001, 1, 1, 1, 20, 1));
        epic1.setDuration(Duration.ofMinutes(20));

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(manager.getEpicById(1).getTitle(), epic1.getTitle());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void updateEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);
        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);

        Epic epic2 = epic1;
        epic2.setTitle("обновленный");
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic2));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epic2.getTitle(), manager.getEpicById(epic1.getId()).getTitle());
    }

    @Test
    void subtaskCreatorTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);

        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(manager.getSubtaskById(2).getTitle(), subtask3.getTitle());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void updateSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);
        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);

        Subtask subTaskUpd = subtask3;
        subTaskUpd.setTitle("обновленный");
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskUpd));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subTaskUpd.getTitle(), manager.getSubtaskById(subtask3.getId()).getTitle());
    }

    //Проверка удаления
    @Test
    void deleteAllTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("title", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.taskCreator(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(manager.getTasks().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        Task task1 = new Task("title", "description",
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.taskCreator(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertNull(manager.getTaskById(task1.getId()));
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteAllEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);

        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(manager.getEpics().isEmpty());
        Assertions.assertTrue(manager.getSubtasks().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteEpicByIdTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);

        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(manager.getEpics().isEmpty());
        Assertions.assertTrue(manager.getSubtasks().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteAllSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);

        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(manager.getSubtasks().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("title", "description");
        manager.epicCreator(epic1);

        Subtask subtask3 = new Subtask("title", "66", TaskStatus.DONE, epic1,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20));
        manager.subtaskCreator(subtask3);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(manager.getSubtasks());
        Assertions.assertTrue(manager.getSubtasks().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }
}