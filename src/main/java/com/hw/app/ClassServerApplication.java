package com.hw.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClassServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClassServerApplication.class.getResource("class-server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Class Server Application");
        stage.setScene(scene);

        stage.setResizable(false);

        stage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            stage.setIconified(false);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}