package com.example.smartbird;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class BirdManager implements Runnable
{
    private Pane pane;
    private ArrayList<Bird> birds;
    private PipeManager obstacles;
    private final double x;
    private final int birdCount;
    private final double radius;
    private final double floorY;
    private boolean running;
    private final double gravity;
    private final double maxPipeX;      //the maximum x coordinate of a pipe.
    private static final Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.DARKGREY, Color.INDIANRED,
                                            Color.AQUA, Color.DARKTURQUOISE, Color.MISTYROSE, Color.LIGHTGOLDENRODYELLOW};
    private static final int MAX_BIRDS = colors.length;
    private static final int PARAMETERS_COUNT = 5;  // number of parameters for each neural network


    public BirdManager(Pane pane, PipeManager pipeManager, double x, double floorY, double radius, int birdCount,
                       double gravity, double maxPipeX) {
        this.pane = pane;
        obstacles = pipeManager;
        this.x = x;
        this.radius = radius;
        this.floorY = floorY;
        this.birdCount = birdCount;
        this.gravity = gravity;
        this.maxPipeX = maxPipeX;

        running = false;
        birds = new ArrayList<>();
        for (int i=0; i<birdCount; i++) {
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
            neuralNetwork.randomize(-10,10,-10,10);

            birds.add(new Bird(x, floorY / 2, radius, Color.ORANGE,neuralNetwork));
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
            // for each alive bird
            for(Bird mrBird: this.birds){

                PipePair p;
                // check for death
                // p = the closest pipe (the only one possible for collision)
                p = obstacles.getClosestRight(this.x-this.radius);
                // if he collides with the pipe or hits the floor
                if (p != null && (mrBird.checkCollision(p) || mrBird.getCenterY()+mrBird.getRadius() >= floorY))
                    mrBird.setDead();

                //for all alive birds
                if (mrBird.isAlive()) {
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


            }
        }
    }
}
