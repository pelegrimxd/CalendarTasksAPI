package project.calendar;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Главный класс приложения TaskManagerApp.
 * Предоставляет графический интерфейс для управления задачами.
 */
public class TaskManagerApp extends Application {
    /**
     * Текстовая область для взаимодействия с пользователем.
     */
    private TextArea interactionArea;
    /**
     * Выбор даты.
     */
    private DatePicker datePicker;
    /**
     * Правая панель.
     */
    private VBox rightPanel;

    /**
     * Точка входа в приложение.
     *
     * @param primaryStage Главная сцена приложения.
     */
    @Override
    public void start(Stage primaryStage) {

        // Создаем основной контейнер
        HBox root = new HBox(10);
        root.setPadding(new Insets(10));

        // Левая панель с календарем
        VBox leftPanel = createLeftPanel();

        // Правая панель взаимодействия
        rightPanel = createRightPanel();

        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = new Scene(root, 1000, 300);
        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Начальное сообщение
        showMessage("Пожалуйста, выберите дату в календаре слева.");
    }

    /**
     * Создает левую панель с календарем.
     *
     * @return Левая панель.
     */
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPrefWidth(250);

        // Календарь
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setOnAction(e -> handleDateSelection());

        Label calendarLabel = new Label("Календарь:");
        leftPanel.getChildren().addAll(calendarLabel, datePicker);

        return leftPanel;
    }

    /**
     * Создает правую панель с областью взаимодействия и кнопками.
     *
     * @return Правая панель.
     */
    private VBox createRightPanel() {
        VBox rightPanel = new VBox(10);
        rightPanel.setPrefWidth(700);

        // Область взаимодействия
        interactionArea = new TextArea();
        interactionArea.setEditable(false);
        interactionArea.setPrefRowCount(10);
        interactionArea.setWrapText(true);

        // Кнопки действий
        HBox buttonBox = new HBox(10);
        Button createButton = new Button("Добавить заметку");
        Button deleteAllButton = new Button("Удалить все заметки");
        Button deleteByPositionButton = new Button("Удалить по позиции");
        Button showTasksButton = new Button("Показать заметки");
        Button exitButton = new Button("Выход");

        createButton.setOnAction(e -> handleCreate());
        exitButton.setOnAction(e -> handleExit());
        deleteAllButton.setOnAction(e -> {
            try {
                handleDeleteAll();
            } catch (IOException ex) {
                showMessage("Ошибка при удалении заметок: " + ex.getMessage());
            }
        });
        deleteByPositionButton.setOnAction(e -> handleDeleteByPosition());
        showTasksButton.setOnAction(e -> {
            try {
                handleShowTasks();
            } catch (IOException ex) {
                showMessage("Ошибка при отображении заметок: " + ex.getMessage());
            }
        });

        buttonBox.getChildren().addAll(createButton, deleteAllButton, deleteByPositionButton, showTasksButton, exitButton);

        Label interactionLabel = new Label("Область взаимодействия:");
        rightPanel.getChildren().addAll(interactionLabel, interactionArea, buttonBox);

        return rightPanel;
    }

    /**
     * Обрабатывает событие выбора даты в календаре.
     */
    private void handleDateSelection() {
        showMessage("Выбрана дата: " + getDate() + "\n\nВыберите действие:\n" +
                "1. Показать заметки\n" +
                "2. Добавить заметку\n" +
                "3. Удалить все заметки\n" +
                "4. Удалить заметку по позиции");
    }

    /**
     * Возвращает выбранную дату в формате строки.
     *
     * @return Выбранная дата в формате "yyyy-MM-dd".
     */
    private String getDate() {
        LocalDate selectedDate = datePicker.getValue();
        return selectedDate.toString();
    }

    /**
     * Обрабатывает событие нажатия на кнопку "Удалить все заметки".
     *
     * @throws IOException Если произошла ошибка при удалении заметок.
     */
    private void handleDeleteAll() throws IOException {
        Client.sendCleanRequest(getDate());
        showMessage("Все заметки удалены.");
    }

    /**
     * Обрабатывает событие нажатия на кнопку "Выход".
     */
    private void handleExit() {
        // Закрываем приложение
        System.exit(0);
    }

    /**
     * Обрабатывает событие нажатия на кнопку "Добавить заметку".
     */
    private void handleCreate() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавление заметки");
        dialog.setHeaderText("Введите текст заметки:");
        dialog.setContentText("Текст:");
        dialog.showAndWait().ifPresent(text -> {
            try {
                Client.sendCreateRequest(getDate(), text); // Отправляем запрос на добавление
                showMessage("Заметка \"" + text + "\" добавлена.");
            } catch (IOException e) {
                showMessage("Ошибка при добавлении заметки: " + e.getMessage());
            }
        });
    }

    /**
     * Обрабатывает событие нажатия на кнопку "Удалить по позиции".
     */
    private void handleDeleteByPosition() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удаление по позиции");
        dialog.setHeaderText("Введите номер позиции для удаления:");
        dialog.setContentText("Позиция:");
        StringBuilder output = new StringBuilder();
        dialog.showAndWait().ifPresent(position ->
        {
            try {
                int pos = Integer.parseInt(position);
                Day day = Client.sendGetRequest(getDate());
                if (day.getTasks().isEmpty()) {
                    output.append("Список заметок пуст.");
                }
                else if (findPosition(day.getTasks(), pos)) {
                    output.append("Такой заметки нет.");
                }
                else {
                    Client.sendDeleteRequest(getDate(), pos);
                    output.append("Заметка на позиции ").append(pos).append(" удалена.");
                }
            } catch(NumberFormatException | IOException e){
                output.append("Пожалуйста, введите корректное число.");
            }
            showMessage(output.toString());
        });
    }

    private boolean findPosition(List<Task> tasks, int pos){
        for (Task task : tasks){
            if(task.getPosition() == pos){
            return false;
            }
        }
        return true;
    }

    /**
     * Обрабатывает событие нажатия на кнопку "Показать заметки".
     *
     * @throws IOException Если произошла ошибка при получении заметок.
     */
    private void handleShowTasks() throws IOException {
        Day day = Client.sendGetRequest(getDate());
        if (day == null){
            showMessage("Сервер не вернул данные. Пожалуйста, попробуйте еще раз.");
        } else if (day.getTasks().isEmpty()) {
            showMessage("Список заметок пуст.");
        } else {
            StringBuilder output = new StringBuilder();
            output.append("Тип: ").append(day.getType()).append("\n");
            output.append("------------------------\n");
            for (Task task : day.getTasks()) {
                output.append("Дата: ").append(task.getDate()).append("\n");
                output.append("ID: ").append(task.getId()).append("\n");
                output.append("Позиция: ").append(task.getPosition()).append("\n");
                output.append("Текст: ").append(task.getText()).append("\n");
                output.append("------------------------\n");
            }
            showMessage(output.toString());
        }
    }

    /**
     * Отображает сообщение в области взаимодействия.
     *
     * @param message Сообщение для отображения.
     */
    private void showMessage(String message) {
        interactionArea.setText(message);
    }

    /**
     * Главный метод приложения.
     *
     * @param args Аргументы командной строки.
     * @throws IOException Если произошла ошибка при запуске сервера или приложения.
     */
    public static void main(String[] args) throws IOException {
        Server.startServer();
        launch(args);
    }
}