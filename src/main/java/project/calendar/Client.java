package project.calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс Client предоставляет методы для взаимодействия с сервером календаря.
 * Он позволяет отправлять GET и POST запросы для получения, создания, удаления и очистки задач.
 */
public class Client {
    /**
     * Переменная для логирования
     */
    private static final Logger logger = LogManager.getLogger(Client.class);
    /**
     * URL для GET запроса списка задач.
     */
    private static final String GET_LIST_URL = "http://localhost:8000/getList";
    /**
     * URL для POST запроса создания задачи.
     */
    private static final String POST_CREATE_URL = "http://localhost:8000/create";
    /**
     * URL для POST запроса удаления задачи.
     */
    private static final String POST_DELETE_URL = "http://localhost:8000/delete";
    /**
     * URL для POST запроса очистки задач за день.
     */
    private static final String POST_CLEAN_URL = "http://localhost:8000/clean";

    /**
     * Отправляет GET запрос на сервер для получения списка задач на указанную дату.
     *
     * @param date Дата, для которой запрашиваются задачи, в формате "yyyy-MM-dd".
     * @return Объект {@link Day}, содержащий список задач, или null в случае ошибки.
     * @throws IOException Если произошла ошибка ввода-вывода при отправке или обработке запроса.
     */
    public static Day sendGetRequest(String date) throws IOException {
        URL url = new URL(GET_LIST_URL + "?date=" + date);
        logger.info("Request to - " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        logger.info("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            logger.info("Response to a GET request: " + response);
            connection.disconnect();
            return parseTasksFromJson(response.toString());
        } else {
            logger.error("The GET request failed");
            connection.disconnect();
            return null;
        }

    }

    /**
     * Отправляет POST запрос на сервер для очистки всех задач на указанную дату.
     *
     * @param date Дата, для которой необходимо очистить задачи, в формате "yyyy-MM-dd".
     * @throws IOException Если произошла ошибка ввода-вывода при отправке или обработке запроса.
     */
    public static void sendCleanRequest(String date) throws IOException {
        URL url = new URL(POST_CLEAN_URL);
        logger.info("Request to - " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String postData = "{\"date\":\"" + date + "\"}";

        OutputStream os = connection.getOutputStream();
        os.write(postData.getBytes());
        os.flush();
        os.close();

        int responseCode = connection.getResponseCode();
        logger.info("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            logger.info("Response to a POST request: " + response);


        } else {
            logger.error("The POST request failed");
        }

        connection.disconnect();
    }

    /**
     * Отправляет POST запрос на сервер для удаления задачи по указанной дате и позиции.
     *
     * @param date     Дата, для которой необходимо удалить задачу, в формате "yyyy-MM-dd".
     * @param position Позиция задачи в списке задач на указанную дату.
     * @throws IOException Если произошла ошибка ввода-вывода при отправке или обработке запроса.
     */
    public static void sendDeleteRequest(String date, int position) throws IOException {
        URL url = new URL(POST_DELETE_URL);
        logger.info("Request to - " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String postData = "{\"date\":\"" + date + "\"" + "," + "\"position\":\"" + position + "\"}";

        OutputStream os = connection.getOutputStream();
        os.write(postData.getBytes());
        os.flush();
        os.close();

        int responseCode = connection.getResponseCode();
        logger.info("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            logger.info("Response to a POST request: " + response);

        } else {
            logger.error("The POST request failed");
        }

        connection.disconnect();
    }

    /**
     * Отправляет POST запрос на сервер для создания новой задачи.
     *
     * @param date Дата, на которую создается задача, в формате "yyyy-MM-dd".
     * @param text Текст задачи.
     * @throws IOException Если произошла ошибка ввода-вывода при отправке или обработке запроса.
     */
    public static void sendCreateRequest(String date, String text) throws IOException {
        URL url = new URL(POST_CREATE_URL);
        logger.info("Request to - " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String postData = "{\"date\":\"" + date + "\"" + "," + "\"text\":\"" + text + "\"}";

        OutputStream os = connection.getOutputStream();
        os.write(postData.getBytes());
        os.flush();
        os.close();

        int responseCode = connection.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            logger.info("Response to a POST request: " + response);
        } else {
            logger.error("The POST request failed");
        }

        connection.disconnect();
    }


    /**
     * Парсит JSON строку и создает объект {@link Day}, содержащий список задач.
     *
     * @param jsonStr JSON строка, представляющая объект Day.
     * @return Объект {@link Day}, содержащий список задач, или null в случае ошибки парсинга.
     */
    public static Day parseTasksFromJson(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            String type = json.getString("type");
            JSONArray tasksArray = json.getJSONArray("tasks");

            Day day = new Day(String.valueOf(type));

            for (int i = 0; i < tasksArray.length(); i++) {
                JSONObject taskJson = tasksArray.getJSONObject(i);
                Task task = new Task(
                        taskJson.getInt("id"),
                        taskJson.getString("date"),
                        taskJson.getInt("position"),
                        taskJson.getString("text")
                );
                day.addTask(task);
            }

            return day;
        } catch (Exception e) {
            logger.error("unknown error");
            return null;
        }
    }

}