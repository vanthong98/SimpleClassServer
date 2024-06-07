package com.hw.app.core;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class Dispatcher extends Thread {
    public static final HashMap<String, PriorityBlockingQueue<String>> QUEUES = new HashMap<>();
    public static final PriorityBlockingQueue<String> APPLICATION_QUEUE = new PriorityBlockingQueue<>();
    private static final ArrayList<String> _clientNames = new ArrayList<>();
    private static final Random _random = new Random();
    private static ObservableList<String> _connectedClients;
    private static TextArea _classMessageTextArea;

    public Dispatcher() {
        _clientNames.add("Orange");
        _clientNames.add("Banana");
        _clientNames.add("Grape");
        _clientNames.add("Apple");
        _clientNames.add("Durian");
    }

    public static void initConnectedClients(ObservableList<String> connectedClients){
        if (_connectedClients == null) _connectedClients = connectedClients;
    }

    public static void initClassMessage(TextArea classMessageTextArea) {
        _classMessageTextArea = classMessageTextArea;
    }

    public static void dispatchToClassMessage(String sender, String message) {
        Platform.runLater(() -> sendToClassMessage(sender, message));
        Dispatcher.send(sender, message, MessageType.SendMessage);
    }

    public static void sendToClassMessage(String sender, String message) {
        _classMessageTextArea.appendText(sender + " (" +  Common.getCurrentTime() + ")" + ":" + Common.NewLine + message + Common.NewLine);
    }

    private static String getMessage(){
        synchronized (APPLICATION_QUEUE){
            return APPLICATION_QUEUE.poll();
        }
    }

    public static void send(String sender, String message, MessageType type){
        synchronized (APPLICATION_QUEUE){
            var messagePackage = sender + ":" + type.toString() + ":" + message;
            APPLICATION_QUEUE.add(messagePackage);
        }
    }

    public static PriorityBlockingQueue <String> getQueue(String clientId){
        synchronized (QUEUES){
            return QUEUES.get(clientId);
        }
    }

    public static String getNewClientId() {
        int randomIndex = _random.nextInt(_clientNames.size());
        var clientName = _clientNames.get(randomIndex);

        if (QUEUES.isEmpty()){
            return clientName + "_" + "1";
        }

        var maxClientId = Collections.max(QUEUES.keySet().stream()
                .map(clientId -> clientId.split("_")[1])
                .map(Integer::parseInt).toList());

        var newClientId = maxClientId + 1;

        return clientName + "_" + newClientId;
    }

    public static void addClient(String clientId) {
        Platform.runLater(() -> _connectedClients.add(clientId));
        QUEUES.put(clientId, new PriorityBlockingQueue<>());
        Logger.log("Client " + clientId + " added");

    }
    public static void removeClient(String clientId) {
        Platform.runLater(() -> _connectedClients.remove(clientId));
        QUEUES.remove(clientId);
        Logger.log("Client " + clientId + " removed");
    }


    public void run() {

        Logger.log("Dispatcher is running ...");

        String message;
        do {
            message = getMessage();

            if (message == null) {
                continue;
            }

            var parts = message.split(":");
            var senderPart = parts[0];

            synchronized (QUEUES) {
                for (var queueKey : QUEUES.keySet()) {

                    if (Objects.equals(senderPart, queueKey))
                    {
                        continue;
                    }

                    var queue = QUEUES.get(queueKey);
                    queue.add(message);
                }
            }
        } while (!Objects.equals(message, Common.ExitMessage));

        Logger.log("Message dispatcher is stopped");
    }
}
