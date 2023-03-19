package Tasks;

import TaskStatus.TaskStatus;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String title, String description, TaskStatus taskStatus, int idEpic) {
        super(title, description, TaskStatus.TaskStatus.valueOf(params[3].toUpperCase()));
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "ID подзадачи Subtask=\"" + id
                + "\", Название подзадачи=\"" + title
                + "\", Описание=\"" + description
                + "\", Находится в эпике с идентификатором=\"" + idEpic
                + "\", Статус=\"" + status + "\""
                + "\n";
    }
}