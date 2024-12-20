package project.calendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс Day представляет собой день в календаре, содержащий список задач.
 */
public class Day {
    /**
     * Тип дня (рабочий, выходной, и т.д.).
     */
    private final String type;
    /**
     * Список задач на день.
     */
    private final List<Task> tasks;

    /**
     * Конструктор класса Day.
     *
     * @param type Тип дня (рабочий, выходной и т.д.).
     */
    public Day(String type) {
        this.type = type;
        this.tasks = new ArrayList<>();
    }

    /**
     * Возвращает тип дня.
     *
     * @return Тип дня.
     */
    public String getType() {
        return type;
    }

    /**
     * Возвращает список задач на день.
     *
     * @return Список задач.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Добавляет задачу в список задач на день.
     *
     * @param task Задача для добавления.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Возвращает строковое представление объекта Day в формате JSON.
     *
     * @return Строковое представление объекта Day в формате JSON.
     */
    @Override
    public String toString() {
        return "{\"type\":" + type + ", \"tasks\":" + tasks + "}";
    }
}