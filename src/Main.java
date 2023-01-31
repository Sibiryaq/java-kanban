import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();

        Task taskFirst = new Task("Покушать","Съесть бургер", "NEW");
        Task taskSecond = new Task("Поспать","Выспаться", "DONE");

        ArrayList<EpicTask.SubTask> subTasksEpicTaskFirst = new ArrayList<>();
        EpicTask.SubTask subtaskFirstEpicTaskFirst = new EpicTask.SubTask("Закончить учебу",
                "Сдать все спринты", "Своевременно выполнить ТЗ", "NEW");
        EpicTask.SubTask subtaskSecondEpicTaskFirst = new EpicTask.SubTask("Закончить учебу",
                "Сдать дипломный проект", "Выполнить дипломный проект", "DONE");

        subTasksEpicTaskFirst.add(subtaskFirstEpicTaskFirst);
        subTasksEpicTaskFirst.add(subtaskSecondEpicTaskFirst);

        EpicTask epicTaskFirst = new EpicTask("Закончить учебу",
                "Получить сертификат обучения", subTasksEpicTaskFirst);

        EpicTask.SubTask subtaskFirstEpicTaskSecond = new EpicTask.SubTask("Сменить работу",
                "Закончить курс практикума","Научиться программировать на языке Java",
                "NEW");
        ArrayList<EpicTask.SubTask> subTasksEpicTaskSecond = new ArrayList<>();

        subTasksEpicTaskSecond.add(subtaskFirstEpicTaskSecond);

        EpicTask epicTaskSecond = new EpicTask("Сменить работу"
                ,"Начать работать Java разработчиком", subTasksEpicTaskSecond);

        manager.saveToStorage(taskFirst);
        manager.saveToStorage(taskSecond);
        manager.saveToStorage(subtaskFirstEpicTaskFirst);
        manager.saveToStorage(subtaskSecondEpicTaskFirst);
        manager.saveToStorage(epicTaskFirst);
        manager.saveToStorage(subtaskFirstEpicTaskSecond);
        manager.saveToStorage(epicTaskSecond);

        System.out.println("1.1 Получение списка всех задач:");
        System.out.println(Arrays.toString(manager.getCompleteListOfAnyTasks(manager.getTaskStorage()).toArray()));
        System.out.println(Arrays.toString(manager.getCompleteListOfAnyTasks(manager.getEpicTaskStorage()).toArray()));
        System.out.println(Arrays.toString(manager.getCompleteListOfAnyTasks(manager.getSubTaskStorage()).toArray()));

        manager.deleteAllTasksOfAnyType(manager.getEpicTaskStorage());

        System.out.println("1.2 Удаление всех задач:");
        System.out.println(Arrays.toString(manager.getCompleteListOfAnyTasks(manager.getTaskStorage()).toArray()));
        System.out.println(Arrays.toString(manager.getCompleteListOfAnyTasks(manager.getEpicTaskStorage()).toArray()));
        System.out.println(Arrays.toString(manager.getCompleteListOfAnyTasks(manager.getSubTaskStorage()).toArray()));

        System.out.println("1.3 Получение по идентификатору:");
        System.out.println(manager.getTaskOfAnyTypeById(0));
        System.out.println(manager.getTaskOfAnyTypeById(1));
        System.out.println(manager.getTaskOfAnyTypeById(2));
        System.out.println(manager.getTaskOfAnyTypeById(3));
        System.out.println(manager.getTaskOfAnyTypeById(4));
        System.out.println(manager.getTaskOfAnyTypeById(5));
        System.out.println(manager.getTaskOfAnyTypeById(6));
        System.out.println(manager.getTaskOfAnyTypeById(7));
        System.out.println(manager.getTaskOfAnyTypeById(8));

        System.out.println("1.4 Создание. Сам объект должен передаваться в качестве параметра:");
        System.out.println(manager.createCopyOfTaskOfAnyType(taskFirst));
        System.out.println(manager.createCopyOfTaskOfAnyType(epicTaskFirst));
        System.out.println(manager.createCopyOfTaskOfAnyType(subtaskFirstEpicTaskFirst));

        manager.updateTaskOfAnyType(5, epicTaskFirst);
        manager.updateTaskOfAnyType(7, epicTaskSecond);

        System.out.println("1.5 Обновление. Новая версия объекта с верным идентификатором " +
                "передаются в виде параметра:");
        System.out.println(Arrays.toString(manager.getCompleteListOfAnyTasks(manager.getEpicTaskStorage()).toArray()));

        manager.removeTaskOfAnyTypeById(1);
        manager.removeTaskOfAnyTypeById(2);

        System.out.println("1.6 Удаление по идентификатору:");
        System.out.println(Arrays.toString(manager.getCompleteListOfAnyTasks(manager.getTaskStorage()).toArray()));

        System.out.println("2.1 Получение списка всех подзадач определённого эпика:");
        System.out.println(manager.getCompleteListOfSubTaskByEpicTask(epicTaskFirst));
        System.out.println(manager.getCompleteListOfSubTaskByEpicTask(epicTaskSecond));
    }
}