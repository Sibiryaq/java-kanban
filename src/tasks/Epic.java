package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIdList;

    public Epic(String title, String description) {
        super(title, description);
        subtaskIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    @Override
    public String toString() {
        return "ID задачи Epic=\"" + id
                + "\", Название задачи=\"" + title
                + "\", Описание=\"" + description
                + "\", Идентификаторы подзадач в эпике=\"" + subtaskIdList
                + "\", Статус=\"" + status + "\"";
    }


}