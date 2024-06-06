package com.hw.app;

import com.hw.app.core.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ClassServerController {
    @FXML
    public TextArea systemConsoleTextArea;
    public Canvas canvas;
    public TextField portTextField;
    public Button sendMessageButton;
    public Button clearBoardButton;
    public Button startServerButton;
    public Button stopServerButton;
    public TextArea messageTextArea;
    public TextArea classMessageTextArea;
    public Button sendFileButton;
    public ListView<String> clientListView;
    public ColorPicker penColorPicker;

    public void onStartServerButtonClick(ActionEvent actionEvent) {
        Logger.init(systemConsoleTextArea);
        Logger.log("Starting server...");

        ObservableList<String> connectedClients = FXCollections.observableArrayList();
        clientListView.setItems(connectedClients);

        Dispatcher.initConnectedClients(connectedClients);
        Dispatcher.initClassMessage(classMessageTextArea);

        var messageDispatcher = new Dispatcher();
        messageDispatcher.start();

        var port = Integer.parseInt(portTextField.getText());
        var serverSocketHandler = new ServerSocketHandler(port);
        serverSocketHandler.start();

        portTextField.setDisable(true);
        startServerButton.setDisable(true);

        canvas.setDisable(false);
        clearBoardButton.setDisable(false);
        sendFileButton.setDisable(false);
        sendMessageButton.setDisable(false);
        stopServerButton.setDisable(false);
        messageTextArea.setDisable(false);
        penColorPicker.setDisable(false);
        messageTextArea.setEditable(true);

        byte[] decodedBytes = Base64.getDecoder().decode(Common.PenBase64);
        var penImage = new Image(new ByteArrayInputStream(decodedBytes));
        var penCursor = new ImageCursor(penImage, penImage.getWidth() / 2, penImage.getHeight());
        canvas.setCursor(penCursor);

        var context = canvas.getGraphicsContext2D();
        context.setStroke(Color.PURPLE);
        penColorPicker.setValue(Color.PURPLE);
    }

    public void onStopServerButtonClick(ActionEvent actionEvent) {
        portTextField.setDisable(false);
        startServerButton.setDisable(false);


        canvas.setDisable(true);
        clearBoardButton.setDisable(true);
        sendMessageButton.setDisable(true);
        sendFileButton.setDisable(true);
        stopServerButton.setDisable(true);
        messageTextArea.setDisable(true);
        messageTextArea.setEditable(false);

        Logger.log("Server stopped");
    }

    public void onCanvasMousePressed(MouseEvent mouseEvent) {
        var context = canvas.getGraphicsContext2D();
        context.beginPath();
        context.moveTo(mouseEvent.getX(), mouseEvent.getY());
        context.stroke();

        Dispatcher.send(Common.Teacher, mouseEvent.getX() + ";" + mouseEvent.getY(), MessageType.SendBoardActionStartDrawing);
        Logger.log("Start drawing line...");
    }

    public void onCanvasMouseDragged(MouseEvent mouseEvent) {
        var context = canvas.getGraphicsContext2D();
        context.lineTo(mouseEvent.getX(), mouseEvent.getY());
        context.stroke();
        Dispatcher.send(Common.Teacher,mouseEvent.getX() + ";" + mouseEvent.getY(), MessageType.SendBoardActionDrawing);
    }

    public void onClearBoardButtonClick(ActionEvent actionEvent) {
        var context = canvas.getGraphicsContext2D();
        context.setFill(Color.WHITESMOKE); // Set the fill color to white (or any color you desire)
        context.fillRect(1, 1, context.getCanvas().getWidth() -2, context.getCanvas().getHeight()-2);
        Dispatcher.send(Common.Teacher,"", MessageType.SendBoardActionClear);
        Logger.log("Cleared board");
    }

    public void onSendMessageButtonClick(ActionEvent actionEvent) {
        var text = messageTextArea.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        messageTextArea.clear();
        Dispatcher.dispatchToClassMessage("Teacher", text);
    }

    public void onSendFileButtonClick(ActionEvent actionEvent) throws IOException {

        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");

        var selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            var path = selectedFile.toPath();
            byte[] fileContent = Files.readAllBytes(path);
            var base64 = Base64.getEncoder().encodeToString(fileContent);
            Dispatcher.send(Common.Teacher,selectedFile.getName() + ";" +  Files.probeContentType(path) + ";" + base64, MessageType.SendFile);
            Dispatcher.dispatchToClassMessage("Teacher", "Sent file [" + selectedFile.getName() +"]" );
        } else {
            Logger.log("No file selected");
        }
    }

    public void onColorPickerChanged(ActionEvent actionEvent) {
        var context = canvas.getGraphicsContext2D();
        var penColor = penColorPicker.getValue();
        context.setStroke(penColor);
        Dispatcher.send(Common.Teacher, penColor.toString(), MessageType.ChangePenColor);
    }
}