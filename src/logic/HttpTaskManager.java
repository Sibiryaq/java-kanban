package logic;

import com.google.gson.*;
import json.DurationTypeAdapter;
import servers.KVTaskClient;
import json.LocalDateTimeAdapter;
import tasks.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

//Класс Менеджера задач, работающего по сети
public class HttpTaskManager extends FileBackedTasksManager {
    //Общий клиент экземпляров класса для отправки данных на KV сервер
    private static KVTaskClient kvClient = new KVTaskClient("localhost");

    private final String saveKey;   //Ключ для сохранения менеджера на KV сервер

    //Конструктор класса
    public HttpTaskManager() {
        super(new File(""));
        saveKey = "HTTPTaskManager";
    }

    //Конструктор класса
    public HttpTaskManager(String saveKey) {
        super(new File(""));
        this.saveKey = saveKey;
    }

    //Перегрузка методов для создания задач
    @Override
    public void taskCreator(Task task) {
        super.taskCreator(task);
        save();
    }

    @Override
    public void epicCreator(Epic epic) {
        super.epicCreator(epic);
        save();
    }

    @Override
    public void subtaskCreator(Subtask subtask) {
        super.subtaskCreator(subtask);
        save();
    }

    //Перегрузка методов для обновления задач
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    //Перегрузка методов для удаления по id
    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    //Перегрузка метода для получения задач по id
    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    //Сохранение данных на сервер
    @Override
    public void save() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        JsonObject result = new JsonObject();
        result.add("tasks", gson.toJsonTree(getTasksList()));

        JsonArray hist = new JsonArray();
        result.add("history", hist);
        for (Task task : historyManager.getHistory()) {
            hist.add(task.getId());
        }

        kvClient.put(saveKey, result.toString());   //Отправка образа менеджера на сервер
    }

    //Создание нового экземпляра менеджера на основе данных с сервера
    public static HttpTaskManager loadFromJson(String loadKey, String newKey) {
        HttpTaskManager newManager = new HttpTaskManager(newKey);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        JsonElement mngElement = JsonParser.parseString(kvClient.load(loadKey));
        if (!mngElement.isJsonObject()) {    // проверяем, точно ли мы получили JSON-объект
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return null;
        }
        JsonObject mngJsonObj = mngElement.getAsJsonObject();

        //Формирование структуры задач нового менеджера
        JsonArray tasksJsonArray = mngJsonObj.getAsJsonArray("tasks");
        for (JsonElement taskElement : tasksJsonArray) {
            String taskType = taskElement.getAsJsonObject().get("type").getAsString();
            switch (TaskType.valueOf(taskType)) {
                case TASK:  //Загрузка обычных задач
                    newManager.taskCreator(gson.fromJson(taskElement, Task.class));
                    break;
                case EPIC:  //Загрузка эпиков с подзадачами
                    Epic epic = gson.fromJson(taskElement, Epic.class);
                    newManager.epicCreator(epic);
                    //Коррекция подзадач после десериализации
                    for (Subtask subtask : epic.getSubtaskIdList()) {
                        subtask.setEpic(epic);                              //Восстановление обратной связи с эпиком
                        newManager.subtaskHashMap.put(subtask.getId(), subtask); //Прописывание подзадачи в общем списке менеджера
                    }
                    break;
                default:
                    System.out.println("Ошибочный тип задачи: " + taskType);
            }
        }

        newManager.refreshSortedSet();

        //Формирование истории нового менеджера задач
        JsonArray histJsonArray = mngJsonObj.getAsJsonArray("history");
        for (JsonElement histElement : histJsonArray) {
            newManager.getTask(histElement.getAsInt());
        }
        return newManager;
    }
}
