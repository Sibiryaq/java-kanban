package servers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import json.DurationTypeAdapter;
import json.LocalDateTimeTypeAdapter;
import logic.HTTPTasksManager;
import tasks.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

//класс HttpTaskServer, будет слушать порт 8080 и принимать запросы.
public class HTTPTaskServer {
    protected HTTPTasksManager manager;
    private final int PORT = 8080;
    protected HttpServer server;

    public static void main(String[] args) throws IOException {
        HTTPTaskServer serv = new HTTPTaskServer();
        serv.start();
    }

    //Запуск сервера
    public void start() {
        System.out.println("Запуск HttpTaskServer сервера на порту " + PORT);
        server.start();
    }

    //Остановка сервера
    public void stop() {
        System.out.println("Остановка сервера HttpTaskServer на порту " + PORT);
        server.stop(1);
    }

    //Конструктор класса
    public HTTPTaskServer() throws IOException {
        manager = new HTTPTasksManager();    //Создание менеджера задач
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(server);
    }

    //Конструктор класса с заданием именем сохранения менеджера на сервере
    public HTTPTaskServer(String saveKey) throws IOException {
        manager = new HTTPTasksManager(saveKey);    //Создание менеджера задач
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(server);
    }

    //Конструктор класса на основе загрузки образа менеджера с сервера
    public HTTPTaskServer(String newKey, String loadKey) throws IOException {
        manager = HTTPTasksManager.loadFromJson(loadKey, newKey);    //Создание менеджера задач
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        createContext(server);
        start();
    }

    //Процедура формирования контекста сервера
    private void createContext(HttpServer server) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        //Получение списка задач с сортировкой по приоритету для выполнения
        server.createContext("/tasks", (h) -> {
            JsonObject resp = new JsonObject();
            try {
                System.out.println("\n/tasks");
                if ("GET".equals(h.getRequestMethod())) {
                    JsonArray priority = new JsonArray();
                    resp.add("taskPriority", priority);
                    for (Task task : manager.getPrioritizedTasks()) {
                        priority.add(task.getId());
                    }
                    sendText(h, resp.toString());
                } else {
                    System.out.println("/tasks ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Получение истории обращения к задачам
        server.createContext("/tasks/history", (h) -> {
            JsonObject resp = new JsonObject();
            try {
                System.out.println("\n/tasks/history");
                if ("GET".equals(h.getRequestMethod())) {
                    JsonArray hist = new JsonArray();
                    resp.add("history", hist);
                    for (Task task : manager.history()) {
                        hist.add(task.getId());
                    }
                    sendText(h, resp.toString());
                } else {
                    System.out.println("/tasks/history ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Работа с задачами: получение(клиентом), создание, удаление
        server.createContext("/tasks/task", (h) -> {
            JsonObject resp = new JsonObject();
            Integer id = getIntParam(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/task");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить задачу(задачи) по запросу
                        if (id == null) {   //Если идентификатор не указан - выдать все
                            resp.add("tasks", gson.toJsonTree(manager.getTasks()));
                            sendText(h, resp.toString());
                        } else {
                            sendText(h, gson.toJson(manager.getTaskById(id)));
                        }
                        break;
                    case "POST":    //Создать задачу на основе полученного Json
                        manager.taskCreator(gson.fromJson(JsonParser.parseString(readText(h)), Task.class));
                        h.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":  //Удаление задачи (либо - задач, если идентификатор пустой)
                        manager.deleteTaskById(id);
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Работа с подзадачами: получение(клиентом), создание, удаление
        server.createContext("/tasks/subtask", (h) -> {
            Integer id = getIntParam(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/subtask");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить подзадачу по запросу
                        if (id != null) {
                            sendText(h, gson.toJson(manager.getSubtaskById(id)));
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "POST":    //Создать задачу на основе полученного Json
                        Subtask subTask = gson.fromJson(JsonParser.parseString(readText(h)), Subtask.class);
                        manager.subtaskCreator(subTask);
                        //manager.addTask(gson.fromJson(JsonParser.parseString(readText(h)), SubTask.class));
                        h.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":  //Удаление подзадачи
                        if (id != null) {
                            manager.deleteSubtaskById(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Получение эпика подзадачи
        server.createContext("/tasks/subtask/epic", (h) -> {
            Integer id = getIntParam(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/subtask/epic");
                if ("GET".equals(h.getRequestMethod())) {
                    if (id != null) {
                        sendText(h, gson.toJson(manager.getSubtaskById(id).getEpic()));
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                } else {
                    System.out.println("/tasks/subtask/epic ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Работа с эпиками: получение(клиентом), создание, удаление
        server.createContext("/tasks/epic", (h) -> {
            Integer id = getIntParam(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/epic");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить эпик по запросу
                        if (id != null) {
                            sendText(h, gson.toJson(manager.getEpicById(id)));
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "POST":    //Создать эпика на основе полученного Json
                        String body = readText(h);
                        if (body != null) {
                            Epic epic = gson.fromJson(JsonParser.parseString(body), Epic.class);
                            manager.epicCreator(epic);
                            //Коррекция подзадач (если они пришли в составе эпика) после десериализации
                            for (Subtask subtask : epic.getSubtaskIdList()) {
                                subtask.setEpic(epic);  //Восстановление обратной связи с эпиком
                                manager.getSubtasks().put(subtask.getId(), subtask);    //Прописывание подзадачи в общем списке менеджера
                            }
                        } else {
                            System.out.println("Пустые данные для создания эпика");
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":  //Удаление эпика (с подзадачами)
                        if (id != null) {
                            manager.deleteEpicById(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally { // finally - выполнится в любом случае, закрываем поток т.к. try-without-resources (close() не будет вызван автоматически)
                h.close();
            }
        });
    }

    // Получение заданного числового параметра из строки запроса
    private Integer getIntParam(URI uri, String name) { // Мб без name как советует идея
        if (uri != null && name != null) {
            String query = uri.getQuery(); //извлечение строки запроса из uri
            if (query != null) {
                String[] pairs = query.split("&"); // разбиваем на пары ключ-значение
                for (String pair : pairs) {
                    String[] param = pair.split("="); // разбиваем пару на ключ и значение
                    if (param.length >= 1 && name.equals(param[0]))
                        return Integer.parseInt(param[1]);
                }
            }
        }
        return null;
    }

    //Чтение ответа на запрос
    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8); // Возращение тела запроса
    }

    //Отправка ответа на запрос
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8); // преобразование в массив байтов
        h.getResponseHeaders().add("Content-Type", "application/json"); // ответ в формате JSON
        h.sendResponseHeaders(200, resp.length); // длина ответа = длина массива байтов
        h.getResponseBody().write(resp); // Записывание тела ответа в выходной поток объекта HttpExchange
    }
}