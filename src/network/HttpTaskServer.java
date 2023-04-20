package network;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exception.ManagerSaveException;
import logic.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskServer {
    private static final int PORT = 8080;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new HttpTaskHandler(manager));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    public class HttpTaskHandler implements HttpHandler {
        private final TaskManager manager;
        private final Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        public HttpTaskHandler(TaskManager manager) {
            this.manager = manager;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            String requestMethod = httpExchange.getRequestMethod();
            String response = null;

            switch (requestMethod) {
                case "GET":
                    if (path.endsWith("/task/") && uri.getRawQuery() == null) {
                        response = gson.toJson(manager.getTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (path.contains("/task/") && uri.getRawQuery() != null) {
                        Integer id = getIdFromExchange(httpExchange);
                        try {
                            response = gson.toJson(manager.getTaskById(id));
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerSaveException e) {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else if (path.contains("/subtask/epic/") && uri.getRawQuery() != null) {
                        Integer id = getIdFromExchange(httpExchange);
                        try {
                            response = gson.toJson(manager.getEpicById(id).getSubtaskIdList());
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerSaveException e) {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else if (path.endsWith("/epic/") && uri.getRawQuery() == null) {
                        response = gson.toJson(manager.getEpics());
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (path.contains("/epic/") && uri.getRawQuery() != null) {
                        Integer id = getIdFromExchange(httpExchange);
                        try {
                            response = gson.toJson(manager.getEpicById(id));
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerSaveException e) {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else if (path.endsWith("/subtask/") && uri.getRawQuery() == null) {
                        response = gson.toJson(manager.getSubtasks());
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (path.contains("/subtask/") && uri.getRawQuery() != null) {
                        Integer id = getIdFromExchange(httpExchange);
                        try {
                            response = gson.toJson(manager.getSubtaskById(id));
                            httpExchange.sendResponseHeaders(200, 0);
                        } catch (ManagerSaveException e) {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else if (path.endsWith("/tasks/")) {
                        response = gson.toJson(manager.getPrioritizedTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    if (path.endsWith("/history/")) {
                        response = gson.toJson(manager.getTaskHistory());
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;
                case "POST":
                    String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    if (path.contains("/task/")) {
                        Task taskFromJson = gson.fromJson(body, Task.class);
                        if (uri.getRawQuery() == null) {
                            try {
                                manager.taskCreator(taskFromJson);
                                httpExchange.sendResponseHeaders(200, 0);
                            } catch (ManagerSaveException e) {
                                httpExchange.sendResponseHeaders(400, 0);
                            }
                        } else {
                            try {
                                manager.updateTask(taskFromJson);
                                httpExchange.sendResponseHeaders(200, 0);
                            } catch (ManagerSaveException e) {
                                httpExchange.sendResponseHeaders(400, 0);
                            }
                        }
                    } else if (path.contains("/epic/")) {
                        Epic epicFromJson = gson.fromJson(body, Epic.class);
                        if (uri.getRawQuery() == null) {
                            try {
                                manager.epicCreator(epicFromJson);

                                httpExchange.sendResponseHeaders(200, 0);
                            } catch (ManagerSaveException e) {
                                httpExchange.sendResponseHeaders(400, 0);
                            }
                        } else {
                            try {
                                manager.updateEpic(epicFromJson);
                                httpExchange.sendResponseHeaders(200, 0);
                            } catch (ManagerSaveException e) {
                                httpExchange.sendResponseHeaders(400, 0);
                            }
                        }
                    } else if (path.contains("/subtask/")) {
                        Subtask subtaskFromJson = gson.fromJson(body, Subtask.class);
                        if (uri.getRawQuery() == null) {
                            try {
                                manager.subtaskCreator(subtaskFromJson);
                                httpExchange.sendResponseHeaders(200, 0);
                            } catch (ManagerSaveException e) {
                                httpExchange.sendResponseHeaders(400, 0);
                            }
                        } else {
                            try {
                                manager.updateSubtask(subtaskFromJson);
                                httpExchange.sendResponseHeaders(200, 0);
                            } catch (ManagerSaveException e) {
                                httpExchange.sendResponseHeaders(400, 0);
                            }
                        }
                    }
                    break;

                case "DELETE":
                    if (path.endsWith("/task/") && uri.getRawQuery() == null) {
                        try {
                            manager.deleteAllTasks();
                            httpExchange.sendResponseHeaders(200, 0);
                            writeResponse(httpExchange, response);
                        } catch (ManagerSaveException e) {
                            e.printStackTrace();
                        }
                    } else if (path.contains("/task/") && uri.getRawQuery() != null) {
                        Integer id = getIdFromExchange(httpExchange);
                        try {
                            manager.deleteTaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                            writeResponse(httpExchange, response);
                        } catch (ManagerSaveException e) {
                            e.printStackTrace();
                        }
                    } else if (path.endsWith("/epic/") && uri.getRawQuery() == null) {

                        try {
                            manager.deleteAllEpics();
                            httpExchange.sendResponseHeaders(200, 0);
                            writeResponse(httpExchange, response);
                        } catch (ManagerSaveException e) {
                            e.printStackTrace();
                        }
                    } else if (path.contains("/epic/") && uri.getRawQuery() != null) {
                        Integer id = getIdFromExchange(httpExchange);
                        try {
                            manager.deleteEpicById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                            writeResponse(httpExchange, response);
                        } catch (ManagerSaveException e) {
                            e.printStackTrace();
                        }
                    } else if (path.endsWith("/subtask/") && uri.getRawQuery() == null) {
                        try {
                            manager.deleteAllSubtasks();
                            httpExchange.sendResponseHeaders(200, 0);
                            writeResponse(httpExchange, response);
                        } catch (ManagerSaveException e) {
                            e.printStackTrace();
                        }
                    } else if (path.contains("/subtask/") && uri.getRawQuery() != null) {
                        Integer id = getIdFromExchange(httpExchange);
                        try {
                            manager.deleteSubtaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                            writeResponse(httpExchange, response);
                        } catch (ManagerSaveException e) {
                            e.printStackTrace();
                        }

                    }
                default:
                    System.out.println("Использован неизвестный метод");
                    httpExchange.sendResponseHeaders(405, 0);

            }
            try (OutputStream stream = httpExchange.getResponseBody()) {
                if (response != null) {
                    stream.write(response.getBytes());
                }
            }
        }
    }

    private void writeResponse(HttpExchange exchange, String response) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private Integer getIdFromExchange(HttpExchange httpExchange) {
        String query = httpExchange.getRequestURI().getRawQuery();
        String[] splitUrl = query.split("=");
        return Integer.parseInt(splitUrl[1]);
    }
}
