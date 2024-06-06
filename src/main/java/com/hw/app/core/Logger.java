package com.hw.app.core;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Logger {
    private static TextArea _systemConsole = null;

    public static void init(TextArea systemConsole) {
        if (_systemConsole == null){
            _systemConsole = systemConsole;
        }
    }

    public static void log(String message) {
        System.out.println(message);
        if (_systemConsole != null){
            Platform.runLater(() -> _systemConsole.appendText(message+ "\n"));
        }
    }
}
