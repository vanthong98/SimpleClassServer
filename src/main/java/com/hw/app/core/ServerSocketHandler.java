package com.hw.app.core;

import com.hw.app.client.ClientSocketInputStreamHandler;
import com.hw.app.client.ClientSocketOutputStreamHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler extends Thread {

    int _port ;

    public ServerSocketHandler(int port) {
        _port = port;
    }

    public void run(){
        Logger.log("Server socket handler started at port " + _port);
        try {

            Socket socket;

            try (var _serverSocket = new ServerSocket(_port)) {
                //noinspection InfiniteLoopStatement
                do {

                    Logger.log("Server socket is waiting for a new connection");

                    socket = _serverSocket.accept();
                    var clientId = Dispatcher.getNewClientId();
                    Logger.log("Server socket is accepted a new connection with clientId " + clientId);

                    Dispatcher.addClient(clientId);

                    var socketInputHandler = new ClientSocketInputStreamHandler(clientId, socket.getInputStream());

                    socketInputHandler.start();

                    var socketOutputHandler = new ClientSocketOutputStreamHandler(clientId, socket.getOutputStream());

                    socketOutputHandler.start();
                }
                while (true);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
