package com.hw.app.client;

import com.hw.app.core.Common;
import com.hw.app.core.Dispatcher;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ClientSocketOutputStreamHandler extends Thread {
    private final OutputStream _socketOutputStream;
    private final String _id ;

    public ClientSocketOutputStreamHandler(String id, OutputStream socketOutputStream) {
        _socketOutputStream = socketOutputStream;
        _id = id;
    }

    private String getMessage(){
        var queue = Dispatcher.getQueue(_id);
        if (queue == null) {
            return null;
        }
        synchronized (queue) {
            return queue.poll();
        }
    }

    public void run() {
        PrintWriter writer = new PrintWriter(_socketOutputStream, true);
        String message;
        do {
            message = getMessage();
            if (message == null) {
                continue;
            }
            message = message + "\n";
            writer.println(message);
        } while (!Common.ExitMessage.equals(message));
    }
}
