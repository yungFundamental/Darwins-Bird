package com.example.smartbird;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

import static javafx.scene.input.KeyCode.SPACE;

public class GameManager extends Application {
    public final static int SCREEN_WIDTH = 960;
    public final static int SCREEN_HEIGHT = 540;
    private BackgroundImage bg;     // background image
    private Text  menuText;
    private Scene menuScene;
    private Scene gameScene;
    private BirdManager birdManager;
    private PipeManager pipeManager;
    private CommandHandler handler;
    private Thread pipeThread;
    private Thread birdThread;
    AnimationTimer timer;
    private double gravity;



    @Override
    public void start(Stage stage){

        Pane gamePane = new Pane();
        Pane menuPane = new Pane();
        handler = new CommandHandler(gamePane);
        bg = new BackgroundImage(new Image("file:src/main/resources/images/Sea&Mountains_scaled.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        menuText = new Text(100, 100, "Press SPACE to start");
        menuText.setFont(new Font(89));

        pipeManager = new PipeManager(handler, SCREEN_WIDTH +100, -100, 65, 145, 4,
                SCREEN_HEIGHT, 400, 0.001);

        //menu:
        System.out.println("Please enter the number corresponding to the preferred option:");
        boolean selected = false;
        while (!selected)   //while any valid option hasn't been selected.
        {
            System.out.println("1:\"Start from random neural networks\", 2:\"Select a save file to load\", 3:\"exit\".");
            Scanner scanner = new Scanner(System.in);
            // get the option from the user:
            int option = scanner.nextInt();
            switch (option) {
                case 1 -> {
                    //randomize neural networks:
                    birdManager = new BirdManager(handler, pipeManager, 290, SCREEN_HEIGHT, 12, 10,
                            SCREEN_WIDTH + 100,0.1, -10, 10, -10, 10);
                    selected = true;
                }
                case 2 -> {
                    // get path to save file from stdin:
                    System.out.println("Please enter the path to the save file to load (or \"back\" to return to menu):");
                    String path = scanner.next();
                    if (path.equals("back")) {
                        break;
                    }
                    try {
                        birdManager = new BirdManager(handler, pipeManager, path);
                        selected = true;
                        break;
                    } catch (IOException e) {   //if exception was met:
                        System.out.println("Error reading file...");
                        break;
                    }
                }
                case 3 -> {
                    //exit option
                    System.out.println("Exiting...");
                    Platform.exit();
                    return;
                }
                default -> System.out.println("Undefined option, please enter the preferred option again:");
            }
        }




        birdThread = new Thread(birdManager);
        pipeThread = new Thread(pipeManager);
        gravity = 0.9;

        birdThread.setName("Bird_Thread");
        pipeThread.setName("Pipe_Thread");

        menuPane.getChildren().add(menuText);
        menuPane.setBackground(new Background(bg));
        gamePane.setBackground(new Background(bg));
        gameScene = new Scene(gamePane, SCREEN_WIDTH, SCREEN_HEIGHT);
        menuScene = new Scene(menuPane, SCREEN_WIDTH, SCREEN_HEIGHT);

        // timer = thread that handles the physics of the game
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                physics();
            }
        };

        // when user presses space -> start game.
        menuScene.setOnKeyPressed(e-> {
            if (e.getCode() == SPACE) {
                birdThread.start();
//                pipeThread.start();
                timer.start();
                switchScene(stage, gameScene);
            }
        });

        stage.setTitle("Darwin's Birds");
        stage.setScene(menuScene);
        stage.show();
    }

    public void switchScene(Stage stage, Scene scene){
        stage.setScene(scene);
        stage.show();
    }

    /** Iterate in game.
     * Add and remove objects from the screen, birds free fall and the pipes move left.
     *
     */
    public void physics(){
        handler.execute();
        birdManager.step(gravity);
        // instead of pipe thread:
        pipeManager.step();
    }

    public static void main(String[] args) {
        launch();
    }
}
