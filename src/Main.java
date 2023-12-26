import enums.*;
import models.*;
import server.KVServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        KVServer kvServer = new KVServer();
        kvServer.start();

        final TaskManager taskManager = Managers.getDefault();

        taskManager.createTask(new Task("simple task", "no big deal", 0, Status.NEW,
                15, LocalDateTime.of(2023, Month.MARCH, 16, 15, 4)));

        taskManager.createEpic(new Epic("epic epic", "got three subtasks", 0, Status.NEW));
        taskManager.createSubTask(new SubTask("first subtask", "am I the startTime?", 0,
                Status.NEW, 15, LocalDateTime.of(2023, Month.MARCH, 16, 14, 2),
                2));
        taskManager.createSubTask(new SubTask("second subtask",
                "needed for date testing", 0, Status.NEW, 15,
                LocalDateTime.of(2023, Month.MARCH, 16, 14, 20), 2));

        taskManager.createSubTask(new SubTask("third subtask",
                "needed for time conflicts testing", 0, Status.DONE, 15,
                LocalDateTime.of(2023, Month.MARCH, 16, 14, 7), 2));

        System.out.println("Создаем задачи...");

        System.out.println("У нас создались следующие задачи: ");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getEpics());

        System.out.println();

        System.out.println("Сортировка по приоритету: ");
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println("История");
        taskManager.getHistory();

        System.out.println("Удалим подзадачу под номером 4");
        taskManager.deleteSubTask(4);

        System.out.println("История");
        taskManager.getHistory();

        System.out.println("Сортировка по приоритету: ");
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println("История");
        taskManager.getHistory();

        System.out.println("Удалим все подзадачи.. ");
        taskManager.deleteAllSubTasks();

        System.out.println("Сортировка по приоритету: ");
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println("История");
        taskManager.getHistory();

        kvServer.stop();

        /*HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

        httpTaskServer.start();*/

    }
}