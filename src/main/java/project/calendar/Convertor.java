package project.calendar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Класс Convertor предоставляет утилитарные методы для работы с JSON.
 */
public class Convertor {

    /**
     * Преобразует строку в формате JSON в объект {@link JSONObject}.
     *
     * @param jsonString Строка в формате JSON.
     * @return Объект {@link JSONObject}, представляющий JSON структуру.
     * @throws JSONException Если строка не является корректным JSON или произошла другая ошибка при парсинге.
     */
    public static JSONObject stringToJson(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }
}