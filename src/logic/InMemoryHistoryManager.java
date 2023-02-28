package logic;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> receivedTasks = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void addToHistory(Task task) {
            // Лучше перед удалением проверить, есть ли такой узел в мапе
            if (receivedTasks.containsKey(task.getId())) {
                remove(task.getId());
            }
            linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(receivedTasks.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        receivedTasks.put(element.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            //Лучше не манипулировать с данными объекта, чтобы equals срабатывал корректно (удалил node.data = null)
            if (head.equals(node) && tail.equals(node)) { //Объекты в java сравниваются только методом equals() т.к. "==" сравнивает ссылки
                head = null;
                tail = null;
            } else if (head.equals(node)) {
                head = next;
                head.prev = null;
            } else if (tail.equals(node)) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
            receivedTasks.remove(node);   //Нет удаления ноды из мапы (метод receivedTasks.remove()
        }
    }

    private static class Node<Task> { // Лучше перенести класс в InMemoryHistoryManager с модификатором private static

        public Task data;
        public Node next; // Дженерики тут необязательны, так как у тебя data указана явно как Task
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}

