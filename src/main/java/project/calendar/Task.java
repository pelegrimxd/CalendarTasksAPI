package project.calendar;

import org.json.JSONObject;

/**
 * Класс Task представляет собой задачу с идентификатором, датой, позицией и текстом.
 */
public class Task {
    /**
     * Уникальный идентификатор задачи.
     */
    private final int id;
    /**
     * Позиция задачи в списке задач на день.
     */
    private final int position;
    /**
     * Дата, к которой относится задача, в формате "yyyy-MM-dd".
     */
    private final String date;
    /**
     * Текст задачи.
     */
    private final String text;

    /**
     * Конструктор класса Task.
     *
     * @param id       Уникальный идентификатор задачи.
     * @param date     Дата, к которой относится задача, в формате "yyyy-MM-dd".
     * @param position Позиция задачи в списке задач на день.
     * @param text     Текст задачи.
     */
    public Task(int id, String date, int position, String text) {
        this.id = id;
        this.date = date;
        this.position = position;
        this.text = text;
    }

    /**
     * Возвращает уникальный идентификатор задачи.
     *
     * @return Уникальный идентификатор задачи.
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает текст задачи.
     *
     * @return Текст задачи.
     */
    public String getText() {
        return text;
    }

    /**
     * Возвращает позицию задачи в списке задач на день.
     *
     * @return Позиция задачи.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Возвращает дату, к которой относится задача.
     *
     * @return Дата задачи в формате "yyyy-MM-dd".
     */
    public String getDate() {
        return date;
    }

    /**
     * Возвращает JSON-представление задачи.
     *
     * @return {@link JSONObject}, представляющий задачу.
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("date", date);
        json.put("position", position);
        json.put("text", text);
        return json;
    }

    /**
     * Возвращает строковое представление задачи.
     *
     * @return Строковое представление задачи.
     */
    @Override
    public String toString() {
        return "id:" + id + " date:" + date + " position:" + position + " text:" + text;
    }
}