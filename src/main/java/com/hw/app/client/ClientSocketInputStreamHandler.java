package com.hw.app.client;

import com.hw.app.core.Common;
import com.hw.app.core.Dispatcher;
import com.hw.app.core.MessageType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClientSocketInputStreamHandler extends Thread {
    private final InputStream _socketInputStream;
    private final String _clientId;

    public ClientSocketInputStreamHandler(String clientId, InputStream socketInputStream) {
        _socketInputStream = socketInputStream;
        _clientId = clientId;
    }

    public void run() {
        try {
            var reader = new BufferedReader(new InputStreamReader(_socketInputStream));
            String message;
            do {
                message = reader.readLine();

                if (message.isEmpty()){
                    continue;
                }
                Dispatcher.sendToClassMessage(_clientId, message);
                Dispatcher.send(_clientId, message, MessageType.SendMessage);

            } while (!Common.ExitMessage.equals(message));

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            System.out.println("Client " + _clientId + " disconnected.");
            Dispatcher.removeClient(_clientId);
        }
    }
}
