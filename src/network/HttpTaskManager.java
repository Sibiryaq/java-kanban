package network;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import logic.FileBackedTasksManager;
import tasks.*;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    protected String API_TOKEN;
    Gson gson;

    public HttpTaskManager(URI KVUri) throws IOException, InterruptedException {
        client = new KVTaskClient(KVUri);
        API_TOKEN = client.getApiToken();
        gson = new GsonBuilder().
                registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
                registerTypeAdapter(Duration.class, new DurationAdapter()).
                setPrettyPrinting().
                serializeNulls().
                create();
        loadManagerFromKVServer();
    }

    @Override
    public void save() { // достаточно перегрузить метод save, чем все методы где он был
        if (getTasks().isEmpty()) {
            System.out.println("таски пусты");
        } else {
            String taskToGson = gson.toJson(getTasks());
            client.put("task", taskToGson);
        }

        if (getEpics().isEmpty()) {
            System.out.println("эпики пусты");
        } else {
            String epicToGson = gson.toJson(getEpics());
            client.put("epic", epicToGson);
        }

        if (getSubtasks().isEmpty()) {
            System.out.println("подзадачи пусты");
        } else {
            String subtaskToGson = gson.toJson(getSubtasks());
            client.put("subtask", subtaskToGson);
        }
        if (getTaskHistory() == null || getTaskHistory().isEmpty()) {
            System.out.println("история пуста");
        } else {
            String historyToGson = gson.toJson(getTaskHistory());
            client.put("history", historyToGson);
        }
    }

    public void loadManagerFromKVServer() {
        loadTasks();
        loadHistory();
        loadSortedTask();
    }

    public void loadTasks() {
        HashMap<Integer, Task> normalTasksLoaded = gson.fromJson(client.load("task"), new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        if (normalTasksLoaded != null) {
            for (Task task : normalTasksLoaded.values()) {
                taskHashMap.put(task.getId(), task);
                if (idGenerator <= task.getId()) {
                    idGenerator = task.getId() + 1;
                }
            }
        }

        HashMap<Integer, Epic> epicTasksLoaded = gson.fromJson(client.load("epic"), new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());
        if (epicTasksLoaded != null) {
            for (Epic epic : epicTasksLoaded.values()) {
                epicHashMap.put(epic.getId(), epic);
                if (idGenerator <= epic.getId()) {
                    idGenerator = epic.getId() + 1;
                }
            }
        }

        HashMap<Integer, Subtask> subTasksLoaded = gson.fromJson(client.load("subtask"), new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType());
        if (subTasksLoaded != null) {
            for (Subtask subtask : subTasksLoaded.values()) {
                subtaskHashMap.put(subtask.getId(), subtask);
                if (idGenerator <= subtask.getId()) {
                    idGenerator = subtask.getId() + 1;
                }
            }
        }
    }

    void loadHistory() {
        ArrayList<Task> historyLoaded = gson.fromJson(client.load("history"), new TypeToken<List<Task>>() {
        }.getType());
        if (historyLoaded != null) {
            for (Task task : historyLoaded) {
                historyManager.addToHistory(task);
            }
        }
    }

    void loadSortedTask() { // Если мапа пуста, то valuev вернет пустой набор, никак не повлияя на sortedTask
        sortedTaskSet.addAll(taskHashMap.values());
        sortedTaskSet.addAll(subtaskHashMap.values());
    }
}
