package project.calendar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Класс TaskDatabase предоставляет методы для взаимодействия с базой данных задач.
 */
public class TaskDatabase {
    /**
     * Поле для логирования
     */
    private static final Logger logger = LogManager.getLogger(TaskDatabase.class);
    /**
     * URL-адрес базы данных SQLite.
     */
    private static final String DB_URL = "jdbc:sqlite:tasks.db";
    /**
     * Имя таблицы задач в базе данных.
     */
    private static final String TABLE_NAME = "tasks";

    /**
     * Соединение с базой данных.
     */
    private Connection connection;

    /**
     * Устанавливает соединение с базой данных.
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            logger.info("The connection to the database is established.");
        } catch (SQLException e) {
            logger.error("Error connecting to the database: " + e.getMessage());
        }
    }

    /**
     * Закрывает соединение с базой данных.
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("The connection to the database is closed.");
            }
        } catch (SQLException e) {
            logger.error("Error when closing the connection: " + e.getMessage());
        }
    }

    /**
     * Создает таблицу задач в базе данных, если она не существует.
     */
    public void createTable() {
        connect();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "text TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "position INTEGER NOT NULL);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            logger.info("The table has been created or already exists.");
        } catch (SQLException e) {
            logger.error("Error when creating the table: " + e.getMessage());
        }
        disconnect();
    }

    /**
     * Добавляет новую задачу в базу данных.
     *
     * @param date Дата, к которой относится задача, в формате "yyyy-MM-dd".
     * @param text Текст задачи.
     */
    public void addTask(String date, String text) {
        connect();
        String insertSQL = "INSERT INTO " + TABLE_NAME + "(text, date, position) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, text);
            preparedStatement.setString(2, date);
            preparedStatement.setInt(3, findFreePosition(date));
            preparedStatement.executeUpdate();
            logger.info("The task has been added.");
        } catch (SQLException e) {
            logger.error("Error when adding an issue: " + e.getMessage());
        }
        disconnect();
    }

    /**
     * Получает список задач для указанной даты.
     *
     * @param date Дата, для которой необходимо получить задачи, в формате "yyyy-MM-dd".
     * @return Список задач для указанной даты.
     */
    public List<Task> listTasksByDate(String date) {
        connect();
        String selectSQL = "SELECT * FROM " + TABLE_NAME + " WHERE date = ?;";
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String text = resultSet.getString("text");
                String dt = resultSet.getString("date");
                int position = resultSet.getInt("position");

                Task task = new Task(id, dt, position, text);
                tasks.add(task);
            }
            logger.info("DB return list of tasks");
        } catch (SQLException e) {
            logger.error("Error when outputting tasks: " + e.getMessage());
        }
        disconnect();
        return tasks;
    }

    /**
     * Удаляет все задачи для указанной даты.
     *
     * @param date Дата, для которой необходимо удалить задачи, в формате "yyyy-MM-dd".
     */
    public void deleteAllTasksByDate(String date) {
        connect();
        String deleteSQL = "DELETE FROM " + TABLE_NAME + " WHERE date = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, date);
            int rowsAffected = preparedStatement.executeUpdate();
            logger.info("Deleted entries: " + rowsAffected);
        } catch (SQLException e) {
            logger.error("Error deleting issues: " + e.getMessage());
        }
        disconnect();
    }

    /**
     * Удаляет задачу по указанной позиции и дате.
     *
     * @param position Позиция задачи для удаления.
     * @param date     Дата, к которой относится задача, в формате "yyyy-MM-dd".
     */
    public void deleteTaskByPositionAndDate(int position, String date) {
        connect();
        String deleteSQL = "DELETE FROM " + TABLE_NAME + " WHERE position = ? AND date = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, position);
            preparedStatement.setString(2, date);
            int rowsAffected = preparedStatement.executeUpdate();
            logger.info("Deleted entries: " + rowsAffected);
        } catch (SQLException e) {
            logger.error("Error deleting issues: " + e.getMessage());
        }
        disconnect();
    }

    /**
     * Находит свободную позицию для добавления задачи на указанную дату.
     *
     * @param date Дата, для которой необходимо найти свободную позицию, в формате "yyyy-MM-dd".
     * @return Свободная позиция для добавления задачи.
     */
    public int findFreePosition(String date) {
        connect();
        String selectSQL = "SELECT position FROM " + TABLE_NAME + " WHERE date = ? ORDER BY position DESC LIMIT 1;";
        int freePosition = 1; // Default to 1 if no tasks exist for the date

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, date);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    freePosition = resultSet.getInt("position") + 1;
                }
            }
        } catch (SQLException e) {
            logger.error("Error when searching for a vacant position: " + e.getMessage());
        }
        disconnect();
        return freePosition;
    }

}