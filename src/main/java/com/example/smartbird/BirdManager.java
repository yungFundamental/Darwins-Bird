package com.example.smartbird;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;

public class BirdManager implements Runnable
{
    private Pane pane;
    private LinkedList<Bird> aliveGeneration;    //the currently alive birds of the current generation
    private LinkedList<Bird> deadGeneration;    //the dead birds of the current generation
    private PipeManager obstacles;
    private final double x;
    private final int generationSize;       //the amount of bird in one generation
    private final double radius;
    private final double floorY;
    private boolean running;
    private final double gravity;
    private final double maxPipeX;      //the maximum x coordinate of a pipe.
    private static final Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.DARKGREY, Color.INDIANRED,
                                            Color.AQUA, Color.DARKTURQUOISE, Color.MISTYROSE, Color.LIGHTGOLDENRODYELLOW};
    private static final int MAX_BIRDS = colors.length;
    private static final int PARAMETERS_COUNT = 5;  // number of parameters for each neural network

    private double min_weight;
    private double max_weight;
    private double min_bias;
    private double max_bias;


    private Bird select() {
        // the bird with the maximum score will have 100% chance to be selected.
        Bird maxBird = deadGeneration.get(0);
        for (Bird bird: deadGeneration){
            if (maxBird.getScore() < bird.getScore())
                maxBird = bird;
        }
        return maxBird;
    }


    public BirdManager(Pane pane, PipeManager pipeManager, double x, double floorY, double radius, int generationSize,
                       double gravity, double maxPipeX, double min_weight, double max_weight, double min_bias, double max_bias) {
        this.pane = pane;
        obstacles = pipeManager;
        this.x = x;
        this.radius = radius;
        this.floorY = floorY;
        this.generationSize = generationSize;
        this.gravity = gravity;
        this.maxPipeX = maxPipeX;

        this.min_weight = min_weight;
        this.max_weight = max_weight;
        this.min_bias = min_bias;
        this.max_bias = max_bias;

        running = false;
        aliveGeneration = new LinkedList<>();
        for (int i = 0; i< generationSize; i++) {
            // input layer - 4 neurons (the parameters will be explained later)
            NeuralNetwork neuralNetwork = new NeuralNetwork(PARAMETERS_COUNT);
            // first hidden layer - 16 neurons, activation function is RelU
            neuralNetwork.addLayer(16, new ReLU());
            // second hidden layer - 16 neurons, activation function is RelU
            neuralNetwork.addLayer(16, new ReLU());
            // output layer - 1 neuron (jump or not)
            // I used the sigmoid function because the answer should be between 0 and 1.
            neuralNetwork.addLayer(1, new Sigmoid());
            //randomize neural network:
            neuralNetwork.randomize(min_weight,max_weight,min_bias,max_bias);

            Bird b = new Bird(x, floorY / 2, radius, colors[i],neuralNetwork);
            aliveGeneration.add(b);
            pane.getChildren().add(b);
        }
    }

    public void stop(){
        running = false;
    }

    @Override
    public void run() {
        running = true;
        double[] input = new double[PARAMETERS_COUNT];
        while (running){
            while (!aliveGeneration.isEmpty()) {
                // for each alive bird
                for (Bird mrBird : this.aliveGeneration) {

                    PipePair p;
                    // check for death
                    // p = the closest pipe (the only one possible for collision)
                    p = obstacles.getClosestRight(this.x - this.radius);
                    // if he collides with the pipe or hits the floor
                    if (p != null && (mrBird.checkCollision(p) || mrBird.getCenterY() + mrBird.getRadius() >= floorY)) {
                        aliveGeneration.remove(mrBird);
                        deadGeneration.add(mrBird);
                        pane.getChildren().remove(mrBird);
                    }


                    //check neural network:
                    // get parameters
                    //param1: The birds Y coordinate
                    input[0] = mrBird.getCenterY();
                    //param2: The closest pipes x coordinate
                    input[1] = (p != null) ? p.getX() : maxPipeX;
                    //param3: The Y of the closest gap.
                    input[2] = (p != null) ? p.getGapY() : 0;
                    //param4: The y velocity of the bird
                    input[3] = mrBird.getVelocity();
                    //param5: the x velocity of the pipes
                    input[4] = obstacles.getSpeed();

                    // check neuralNetwork
                    if (mrBird.shouldJump(input))
                        mrBird.jump();

                    mrBird.step();
                    mrBird.accelerate(gravity);
                    mrBird.incScore();
                }
            }   //generation has perished

            Bird fittest = select();
            for (int i = 0; i<generationSize; i++) {
                Bird child = new Bird(x, floorY / 2, radius, colors[i], fittest.getBrain());
                child.mutate(0.1, min_weight,max_weight,min_bias,max_bias);
                aliveGeneration.add(child);
                pane.getChildren().add(child);
            }
            deadGeneration.clear();



        }
    }
}
