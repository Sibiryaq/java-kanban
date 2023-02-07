import taskclasses.Epic;
import taskclasses.Subtask;
import taskclasses.Task;

public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();

        Task taskFirst = new Task("Покушать", "Съесть бургер", "NEW");
        Task taskSecond = new Task("Поспать", "Выспаться", "DONE");

        Epic epicTaskFirst = new Epic("Закончить учебу", "Получить сертификат обучения");
        Epic epicTaskSecond = new Epic("Сменить работу", "Начать работать Java разработчиком");

        Subtask subtaskFirst = new Subtask("Закончить учебу", "Сдать все спринты", 4);
        Subtask subtaskSecond = new Subtask("Закончить учебу", "Сдать дипломный проект", 4);

        manager.taskCreator(taskFirst);
        manager.taskCreator(taskSecond);
        manager.epicCreator(epicTaskFirst);
        manager.epicCreator(epicTaskSecond);
        manager.subtaskCreator(subtaskFirst);
        manager.subtaskCreator(subtaskSecond);

        System.out.println("\n    Получение списка всех задач:");
        System.out.println(manager.getTasks());
        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getEpics());
        System.out.println(manager.getEpicById(4));
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getSubtaskById(6));

        System.out.println("\n    Получение по идентификатору:");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println("\n    Обновление. Новая версия объекта с верным идентификатором " +
                "передаются в виде параметра:");
        taskFirst = manager.getTaskById(1);
        taskFirst.setStatus("DONE");
        manager.updateTask(taskFirst);
        System.out.println(manager.getTasks());

        subtaskFirst = manager.getSubtaskById(5);
        subtaskFirst.setStatus("DONE");
        manager.updateSubtask(subtaskFirst);
        System.out.println(manager.getSubtasks());

        subtaskSecond = manager.getSubtaskById(6);
        subtaskSecond.setStatus("DONE");
        manager.updateSubtask(subtaskSecond);
        System.out.println(manager.getSubtasks());

        System.out.println("\n    Получение списка всех подзадач определённого эпика:");
        System.out.println(manager.getAllSubtasks(epicTaskFirst));
        System.out.println(manager.getAllSubtasks(epicTaskSecond));

        System.out.println("\n    Удаление по идентификатору:");
        manager.deleteEpic(4);
        System.out.println(manager.getEpicById(4));
        manager.deleteTask(2);
        System.out.println(manager.getTaskById(2));
        manager.deleteSubtask(2);
        System.out.println(manager.getSubtaskById(6));

        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println("\n    Удаление всех задач:");
        manager.deleteTaskList();
        manager.deleteEpicList();
        manager.deleteSubtaskList();
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getEpics());
    }
}