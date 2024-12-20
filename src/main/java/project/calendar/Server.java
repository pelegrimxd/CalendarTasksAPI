package project.calendar;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static project.calendar.Convertor.stringToJson;

/**
 * Запускает HTTP-сервер для обработки запросов календаря.
 * Сервер обрабатывает запросы для получения списка задач, добавления, удаления и очистки задач.
 */

public class Server {

    /**
     * Поле для логирования
     */
    private static final Logger logger = LogManager.getLogger(Server.class);

    /**
     * Запускает HTTP-сервер на порту 8000.
     * Создает контексты для обработки различных запросов.
     * @throws IOException Если возникает ошибка ввода-вывода.
     */
    public static void startServer() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        TaskDatabase db = new TaskDatabase();
        db.createTable();

        server.createContext("/getList", new GetListHandler());
        server.createContext("/create", new PostCreateHandler());
        server.createContext("/delete", new PostDeleteHandler());
        server.createContext("/clean", new PostCleanHandler());

        server.setExecutor(null);
        server.start();
        logger.info("The server is running on port 8000");
    }


    /**
     * Внутренний класс, обрабатывающий GET-запросы для получения списка задач по дате.
     */
    static class GetListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                URI requestURI = exchange.getRequestURI();
                String query = requestURI.getQuery();


                String date = null;
                String holiday = null;
                String result = null;
                JSONArray jsonArray = new JSONArray();
                if (query != null) {

                    String[] pair = query.split("=");
                    if (pair.length == 2) {
                        date = pair[0];
                        String value = pair[1];

                        holiday = sendGetHolidayRequest(value);

                        TaskDatabase db = new TaskDatabase();

                        for (Task task : db.listTasksByDate(value)) {
                            jsonArray.put(task.toJson());
                        }

                    } else {
                        logger.error("Incorrect parameter format: " + query);
                    }
                } else {
                    logger.error("There are no parameters");
                }

                String response = jsonArray.toString();
                result = "{\"type\":" + getStatusMessage(holiday) + ", \"tasks\":" + response + "}";

                exchange.sendResponseHeaders(200, result.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(result.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * Внутренний класс, обрабатывающий POST-запросы для создания новой задачи.
     */
    static class PostCreateHandler implements HttpHandler {
        /**
         * Обрабатывает POST-запрос, получая данные задачи из тела запроса и добавляя задачу в базу данных.
         * @param exchange Объект HttpExchange, представляющий текущий HTTP-обмен.
         * @throws IOException Если возникает ошибка ввода-вывода.
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                InputStream is = exchange.getRequestBody();
                String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                JSONObject jsonObject = stringToJson(requestBody);
                String date = jsonObject.getString("date");
                String text = jsonObject.getString("text");

                TaskDatabase db = new TaskDatabase();
                db.addTask(date, text);

                String response = "Добавлена новая заметка на день - " + date;
                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * Внутренний класс, обрабатывающий POST-запросы для удаления задачи.
     */
    static class PostDeleteHandler implements HttpHandler {
        /**
         * Обрабатывает POST-запрос, получая данные задачи из тела запроса и удаляя задачу из базы данных.
         * @param exchange Объект HttpExchange, представляющий текущий HTTP-обмен.
         * @throws IOException Если возникает ошибка ввода-вывода.
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                JSONObject jsonObject = stringToJson(requestBody);
                String date = jsonObject.getString("date");
                int position = jsonObject.getInt("position");

                TaskDatabase db = new TaskDatabase();
                db.deleteTaskByPositionAndDate(position, date);

                String response = "Удалена запись на дату - " + date + " с позицией - " + position;

                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * Внутренний класс, обрабатывающий POST-запросы для очистки всех задач на заданную дату.
     */
    static class PostCleanHandler implements HttpHandler {
        /**
         * Обрабатывает POST-запрос, получая дату из тела запроса и удаляя все задачи на эту дату из базы данных.
         * @param exchange Объект HttpExchange, представляющий текущий HTTP-обмен.
         * @throws IOException Если возникает ошибка ввода-вывода.
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                InputStream is = exchange.getRequestBody();
                String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                JSONObject jsonObject = stringToJson(requestBody);
                String date = jsonObject.getString("date");

                TaskDatabase db = new TaskDatabase();
                db.deleteAllTasksByDate(date);

                String response = "Все заметки на дату - " + date + " удалены.";
                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    /**
     * Отправляет GET-запрос на сервис isdayoff.ru для определения типа дня (рабочий/выходной).
     * @param date Дата в формате YYYY-MM-DD.
     * @return Строковое представление кода ответа сервиса isdayoff.ru.
     * @throws IOException Если возникает ошибка ввода-вывода.
     */
    private static String sendGetHolidayRequest(String date) throws IOException {
        String result = null;
        String[] parts = date.split("-");
        String year = parts[0];
        String month = parts[1];
        String day = parts[2];
        String urlAddress = "https://isdayoff.ru/api/getdata?year=" + year + "&month=" + month + "&day=" + day;
        URL url = new URL(urlAddress);
        logger.info("Request to - " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        logger.info("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            result = response.toString();

        } else {
            result = "GET запрос не удался";
        }
        connection.disconnect();
        return result;
    }

    /**
     * Возвращает текстовое сообщение, соответствующее коду ответа сервиса isdayoff.ru.
     * @param code Код ответа сервиса isdayoff.ru.
     * @return Текстовое сообщение, описывающее тип дня.
     */
    private static String getStatusMessage(String code) {
        return switch (code) {
            case "0" -> "Рабочий день";
            case "1" -> "Нерабочий день";
            case "100" -> "Ошибка в дате";
            case "101" -> "Данные не найдены";
            case "199" -> "Ошибка сервиса";
            default -> "Неизвестный статус";
        };
    }

}