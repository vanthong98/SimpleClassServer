package com.hw.app.core;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Common {
    public static String ExitMessage = "exit";
    public static String NewLine = "\n";
    public static String Teacher = "Teacher";

    public static String getCurrentTime(){
        var currentTime = LocalTime.now();
        var formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }
}