package com.example.smartbird;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.input.KeyCode.SPACE;

public class HelloApplication extends Application {
    public final static int S_WIDTH = 960;
    public final static int S_HEIGHT = 540;
    private BackgroundImage bg;     // background image
    private Text  menuText;
    private Scene menuScene;
    private Scene gameScene;
    private BirdManager birdManager;
    private PipeManager pipeManager;
    private Thread pipeThread;
    private Thread birdThread;



    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();

        Pane gamePane = new Pane();
        Pane menuPane = new Pane();
        bg = new BackgroundImage(new Image("file:src/main/resources/images/Sea&Mountains_scaled.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        menuText = new Text(100, 100, "Press SPACE to start");
        menuText.setFont(new Font(89));
        pipeManager = new PipeManager(gamePane, S_WIDTH+100, -100, 65, 145, 4,
                S_HEIGHT, 400, 0.001);
        birdManager = new BirdManager(gamePane, pipeManager, 290, S_HEIGHT, 12, 4,
                S_WIDTH+100, -10, 10,-10,10);
        birdThread = new Thread(birdManager);
        pipeThread = new Thread(pipeManager);

        birdThread.setName("Bird_Thread");
        pipeThread.setName("Pipe_Thread");


        menuPane.getChildren().add(menuText);
        menuPane.setBackground(new Background(bg));
        gamePane.setBackground(new Background(bg));
        gameScene = new Scene(gamePane, S_WIDTH, S_HEIGHT);
        menuScene = new Scene(menuPane, S_WIDTH, S_HEIGHT);

        menuScene.setOnKeyPressed(e-> {
            if (e.getCode() == SPACE) {
//                birdThread.start();
                pipeThread.start();
                switchScene(stage, gameScene);
            }
        });

        stage.setTitle("Darwin's Bird");
        stage.setScene(menuScene);
        stage.show();
    }

    public void switchScene(Stage stage, Scene scene){
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}



// execute gravity in update:
/*
    for (Node e: pane.getChildren())
        if (e instanceof Bird)
            e.accelerate(gravity)
 */