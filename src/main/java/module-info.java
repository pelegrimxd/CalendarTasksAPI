module project.calendar {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;
    requires org.json;
    requires java.sql;
    requires java.net.http;
    requires org.apache.logging.log4j;


    opens project.calendar to javafx.fxml;
    exports project.calendar;
}