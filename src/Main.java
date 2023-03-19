import Managers.Managers;
import Managers.TaskManager;

import TaskStatus.TaskStatus;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

     /*
      Спринт 5. создайте две задачи, эпик с тремя подзадачами и эпик без подзадач:
     */
        manager.taskCreator(new Task("Задача №1", "Описание задачи 1", TaskStatus.TaskStatus.valueOf(params[3].toUpperCase()))); //1
        manager.taskCreator(new Task("Задача №2", "Описание задачи 2", TaskStatus.TaskStatus.valueOf(params[3].toUpperCase()))); //2

        Epic epic1 = new Epic("Эпик №1", "С тремя подзадачами", TaskStatus.valueOf(params[3].toUpperCase())); //3
        manager.epicCreator(epic1);

        Subtask subtask11 = new Subtask("Подзадача № 1", "Описание подзадачи 1", TaskStatus.TaskStatus.valueOf(params[3].toUpperCase()), 3); //4
        manager.subtaskCreator(subtask11);
        Subtask subtask12 = new Subtask("Подзадача № 2", "Описание подзадачи 2", TaskStatus.TaskStatus.valueOf(params[3].toUpperCase()), 3); //5
        manager.subtaskCreator(subtask12);
        Subtask subtask13 = new Subtask("Подзадача № 3", "Описание подзадачи 3", TaskStatus.TaskStatus.valueOf(params[3].toUpperCase()), 3); //6
        manager.subtaskCreator(subtask13);


        Epic epic2 = new Epic("Эпик №2", "Без подзадач", TaskStatus.TaskStatus.valueOf(params[3].toUpperCase())); //7
        manager.epicCreator(epic2);

     /*
      Спринт 5. Для проверки создания выведем все виды задач:
     */
        System.out.println("\n Cозданные Эпики : \n" + manager.getEpics());
        System.out.println("\n Созданные Задачи : \n" + manager.getTasks());
        System.out.println("\n Созданные Подзадачи : \n" + manager.getSubtasks());

     /*
       Спринт 5. запросите созданные задачи несколько раз в разном порядке,
       после каждого запроса выведите историю и убедитесь, что в ней нет повторов:
     */
        System.out.println("\n Запрос рандомной задачи : \n" + manager.getTaskById(1));
        System.out.println("Показать историю : \n" + manager.history());
        System.out.println("\nЗапрос рандомной задачи : \n" + manager.getTaskById(2));
        System.out.println("Показать историю : \n" + manager.history());
        System.out.println("\nЗапрос рандомного эпика : \n" + manager.getEpicById(3));
        System.out.println("Показать историю : \n" + manager.history());
        System.out.println("\nЗапрос рандомного эпика : \n" + manager.getEpicById(7));
        System.out.println("Показать историю : \n" + manager.history());
        System.out.println("\nЗапрос рандомной задачи : \n" + manager.getTaskById(1)); //второй раз, для проверки дублирования
        System.out.println("Показать историю : \n" + manager.history());
        System.out.println("\nЗапрос рандомной задачи : \n" + manager.getSubtaskById(4)); //второй раз, для проверки дублирования
        System.out.println("Показать историю : \n" + manager.history());


     /*
      Спринт 5. удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться:
     */
        System.out.println("Удалим задачу, которая есть в истории: ");
        manager.deleteTask(1);
        System.out.println("Проверим не осталась ли она в истории: ");
        System.out.println("Показать историю : \n" + manager.history());

     /*
        Спринт 5.  удалите эпик с тремя подзадачами
     */
        System.out.println("Удалим эпик с тремя подзадачами: ");
        manager.deleteEpic(3);
        manager.deleteSubtask(4);
        manager.deleteSubtask(5);
        manager.deleteSubtask(6);

     /*
        Спринт 5. убедитесь, что из истории удалился как сам эпик, так и все его подзадачи
     */
        System.out.println("\n Показать историю : \n" + manager.history());

    }
}