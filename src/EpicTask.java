import java.util.ArrayList;
import java.util.Arrays;

public class EpicTask extends Task {
    private final ArrayList<SubTask> subTasks;

    EpicTask(String nameEpicTask, String descriptionEpicTask, ArrayList<SubTask> subTasks) {  //для создания задачи
        super(nameEpicTask, descriptionEpicTask);
        this.setStatus(Manager.getEpicTaskStatus(subTasks));
        this.subTasks = subTasks;
    }

    EpicTask(EpicTask epicTask) {  //для копирования задачи
        this(epicTask.getName(), epicTask.getDescription(), epicTask.subTasks);
    }

    ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        return "ID задачи Epic=\"" + getId()
                + "\", Название Epic задачи=\"" + getName()
                + "\", Описание=\"" + getDescription()
                + "\"" + ", " + Arrays.toString(subTasks.toArray())
                + ", Статус=\"" + getStatus() + "\"";
    }

    static class SubTask extends Task { //Внутренний класс для создания SubTask подзадач Epic задач
        private final String nameEpicTask;

        SubTask(String nameEpicTask, String nameSubTask, String descriptionSubTask, String statusSubTask) {
            super(nameSubTask, descriptionSubTask, statusSubTask);
            this.nameEpicTask = nameEpicTask;
        }

        SubTask(SubTask subtask) {
            this(subtask.nameEpicTask, subtask.getName(), subtask.getDescription(), subtask.getStatus());
        }

        @Override
        public String toString() {
            return "ID подзадачи SubTask=\"" + getId()
                    + "\", Название Epic задачи=\"" + nameEpicTask
                    + "\", Название подзадачи=\"" + getName()
                    + "\", Описание=\"" + getDescription()
                    + "\", Статус=\"" + getStatus() + "\"";
        }
    }
}