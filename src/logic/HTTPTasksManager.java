package logic;


import com.google.gson.*;
import json.DurationTypeAdapter;
import json.LocalDateTimeTypeAdapter;
import servers.*;
import tasks.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class HTTPTasksManager extends FileBackedTasksManager {
    protected static KVTaskClient kvClient = new KVTaskClient("localhost");
    private final String saveKey;   //Ключ для сохранения менеджера на KV сервер

    public HTTPTasksManager() {
        super(new File(""));
        saveKey = "HTTPTaskManager";
    }

    public HTTPTasksManager(String saveKey) {
        super(new File(""));
        this.saveKey = saveKey;
    }

    @Override
    public void save() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        JsonObject result = new JsonObject();
        result.add("tasks", gson.toJsonTree(getAllTasks()));

        JsonArray hist = new JsonArray();
        result.add("history", hist);
        for (Task task : historyManager.getHistory()){
            hist.add(task.getId());
        }

        kvClient.put(saveKey, result.toString());   //Отправка образа менеджера на сервер
    }

    //Создание нового экземпляра менеджера на основе данных с сервера
    public static HTTPTasksManager loadFromJson(String loadKey, String newKey){
        HTTPTasksManager newManager = new HTTPTasksManager(newKey);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        JsonElement managerElement = JsonParser.parseString(kvClient.load(loadKey));
        if(!managerElement.isJsonObject()) {    // проверяем, точно ли мы получили JSON-объект
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return null;
        }
        JsonObject managerJsonObject = managerElement.getAsJsonObject();

        //Формирование структуры задач нового менеджера
        JsonArray tasksJsonArray = managerJsonObject.getAsJsonArray("tasks");
        for(JsonElement taskElement : tasksJsonArray){
            String taskType = taskElement.getAsJsonObject().get("type").getAsString();
            switch(TaskType.valueOf(taskType)){
                case TASK:  //Загрузка обычных задач
                    newManager.taskCreator(gson.fromJson(taskElement, Task.class));
                    break;
                case EPIC:  //Загрузка эпиков с подзадачами
                    Epic epic = gson.fromJson(taskElement, Epic.class);
                    newManager.epicCreator(epic);
                    //Коррекция подзадач после десериализации
                    for(Subtask subtask : epic.getSubtaskIdList()){
                        subtask.setEpic(epic);                              //Восстановление обратной связи с эпиком
                        newManager.subtaskHashMap.put(subtask.getId(), subtask); //Прописывание подзадачи в общем списке менеджера
                    }
                    break;
                default:
                    System.out.println("Ошибочный тип задачи: " + taskType);
            }
        }

        //Формирование истории нового менеджера задач
        JsonArray histJsonArray = managerJsonObject.getAsJsonArray("history");
        for (JsonElement histElement : histJsonArray){
            //newManager.getTask(histElement.getAsInt());
            newManager.getTaskById(histElement.getAsInt());
            newManager.getSubtaskById(histElement.getAsInt());
            newManager.getEpicById(histElement.getAsInt());
        }
        return newManager;
    }
}