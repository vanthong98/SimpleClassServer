module com.hw.simpleclassserver {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.hw.app to javafx.fxml;
    exports com.hw.app;
    exports com.hw.app.client;
    opens com.hw.app.client to javafx.fxml;
}