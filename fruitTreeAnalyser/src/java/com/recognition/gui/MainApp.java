package com.recognition.gui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/mainPage.fxml")));
        primaryStage.getIcons().add(new Image("image/grapes.png"));
        primaryStage.setTitle("水果识别");
        primaryStage.setScene(new Scene(root, 850, 650));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


}
