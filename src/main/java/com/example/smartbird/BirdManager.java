package com.example.smartbird;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  This class handles neuroevolution of birds on a specific pane.
 */
public class BirdManager implements Runnable
{
    private final CommandHandler handler;       // handles addition and removal of birds to the pane.
    private ArrayList<Bird> aliveGeneration;    //the currently alive birds of the current generation
    private ArrayList<Bird> deadGeneration;    //the dead birds of the current generation
    private PipeManager obstacles;
    private long bestScore;
    private int generationNumber;
    private GenerationNumber genText;

    private double x;
    private double mutationChance;  //mutation rate/chance in each mutation (see mutation function in NeuralNetwork class)
    private double radius;
    private double floorY;
    private boolean running;
    private int generationSize;     //the amount of birds in a generation
    private double maxPipeX;      //the maximum x coordinate of a pipe.
    private static final Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.DARKGREY, Color.INDIANRED,
            Color.AQUA, Color.DARKTURQUOISE, Color.MISTYROSE, Color.LIGHTGOLDENRODYELLOW,
            Color.FIREBRICK};
    private static final int PARAMETERS_COUNT = 5;  // number of parameters for each neural network

    private double min_weight;
    private double max_weight;
    private double min_bias;
    private double max_bias;


    /** Basic constructor.
     *
     * @param commandHandler CommandHandler instance that is linked with the pane the birds will be drawn on.
     * @param pipeManager Manages the obstacles of the bird.
     * @param x The x coordinate of the birds.
     * @param floorY The Y coordinate of the floor.
     * @param radius Radius of the birds.
     * @param generationSize Amount of birds in a generation.
     * @param mutationChance The chance of each weight and bias to be random while mutating.
     * @param maxPipeX The maximum X coordinate of a pipe.
     * @param min_weight Minimum value for each weight.
     * @param max_weight Maximum value for each weight.
     * @param min_bias Minimum value for each bias.
     * @param max_bias Maximum value for each bias.
     * @param textX X coordinate of the generation number text.
     * @param textY Y coordinate of the generation number text.
     * @param textFont Font size of the generation number text.
     */
    public BirdManager(CommandHandler commandHandler, PipeManager pipeManager, double x, double floorY, double radius,
                       int generationSize, double maxPipeX,
                       double mutationChance, double min_weight, double max_weight, double min_bias, double max_bias,
                       double textX, double textY, int textFont) {
        this.handler = commandHandler;
        this.obstacles = pipeManager;
        this.x = x;
        this.radius = radius;
        this.floorY = floorY;
        this.generationSize = generationSize;
        this.maxPipeX = maxPipeX;
        this.bestScore = 0;
        this.generationNumber = 0;
        this.mutationChance = mutationChance;
        this.min_weight = min_weight;
        this.max_weight = max_weight;
        this.min_bias = min_bias;
        this.max_bias = max_bias;

        running = false;
        aliveGeneration = new ArrayList<>();
        deadGeneration = new ArrayList<>();
        //create birds:
        for (int i = 0; i< generationSize; i++) {
            // input layer - 4 neurons (the parameters will be explained later)
            NeuralNetwork neuralNetwork = new NeuralNetwork(5); //PARAMETERS_COUnt
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
            handler.demand(b, true);
        }
        // add Text describing which generation we are on.
        this.genText = new GenerationNumber(textX, textY, new Font(textFont), this.generationNumber, "Generation Number: ");
        handler.demand(this.genText, true);
    }

    /** Import constructor - constructs from saveFile.
     *
     * @param commandHandler CommandHandler instance that is linked with the pane the birds will be drawn on.
     * @param pipeManager Manages the obstacles of the bird.
     * @param filePath The path to the file where the previous BirdManager saved its progress.
     * @throws IOException Thrown in case of file error, file saved incorrectly, or more.
     */
    public BirdManager(CommandHandler commandHandler, PipeManager pipeManager, String filePath, double textX,
                       double textY, int textFont) throws IOException {
        // create a reader that allows us to read the lines of the saveFile
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        this.handler = commandHandler;
        this.obstacles = pipeManager;
        running = false;
        aliveGeneration = new ArrayList<>();
        deadGeneration = new ArrayList<>();

        this.generationSize = 0;

        // read line
        String line = reader.readLine();
        //while there are lines to read:
        while (line != null){
            //split by words
            String[] words = line.split("[ ]", 0);
            switch (words[0]){
                case "generationSize=":
                    //generation size is calculated by amount of neural networks
                    break;

                case "x=":
                    this.x = Double.parseDouble(words[1]);
                    break;

                case "generationNumber=":
                    this.generationNumber = Integer.parseInt(words[1]);
                    break;

                case "bestScore=":
                    this.bestScore = Long.parseLong(words[1]);
                    break;

                case "radius=":
                    this.radius = Double.parseDouble(words[1]);
                    break;

                case "maxPipeX=":
                    this.maxPipeX = Double.parseDouble(words[1]);
                    break;

                case "floorY=":
                    this.floorY = Double.parseDouble(words[1]);
                    break;

                case "mutationChance=":
                    this.mutationChance = Double.parseDouble(words[1]);
                    break;

                case "max_bias=":
                    this.max_bias = Double.parseDouble(words[1]);
                    break;

                case "min_bias=":
                    this.min_bias = Double.parseDouble(words[1]);
                    break;

                case "max_weight=":
                    this.max_weight = Double.parseDouble(words[1]);
                    break;

                case "min_weight=":
                    this.min_weight = Double.parseDouble(words[1]);
                    break;

                case "NeuralNetwork{":
                    // get the neural network string
                    StringBuilder networkString = new StringBuilder();
                    line = reader.readLine();
                    while (!line.equals("}")) {
                        networkString.append(line).append('\n');
                        line = reader.readLine();
                    }
                    //recreate the neural network
                    NeuralNetwork net = NeuralNetwork.fromString(networkString.toString());

                    // create a new bird with the brain we loaded.
                    Bird bird = new Bird(this.x, this.floorY/2, this.radius, colors[generationSize++], net);

                    //add to alive generation and draw
                    aliveGeneration.add(bird);
                    handler.demand(bird, true);
                    break;

                default:
                    System.out.println("word found: \"" + words[0] + "\"");
                    throw new IOException();
            }

            // read next line
            line = reader.readLine();
        }
        // the generationSize = the amount of birds (neural networks) found.
        this.generationSize = this.aliveGeneration.size();
        // add Text describing which generation we are on.
        this.genText = new GenerationNumber(textX, textY, new Font(textFont), this.generationNumber, "Generation Number: ");
        handler.demand(this.genText, true);
        // close reader
        reader.close();

    }


    /** Select a bird from the dead generation to be mutated. The score of each bird is their fitness and the bird
     * with maximum fitness will be selected 100% of the time.
     * @return Selected bird.
     */
    private Bird select() {
        // the bird with the maximum score will have 100% chance to be selected.
        Bird maxBird = deadGeneration.get(0);
        for (Bird bird: deadGeneration){
            if (maxBird.getScore() < bird.getScore())
                maxBird = bird;
        }
        return maxBird;
    }

    /** Stop the thread and genetic algorithm.
     *
     */
    public void stop(){
        running = false;
    }

    /** execute scheduled tasks on every alive bird (move in accordance to speed, accelerate in accordance to gravity)
     *
     * @param gravity Acceleration value (larger the value, the more the speed increases downwards).
     */
    public void step(double gravity){
        List<Bird> birdList = new ArrayList<>(this.aliveGeneration);
        for(Bird bird: birdList){
            bird.accelerate(gravity);
            bird.step();
        }
    }

    public long getBestScore() {
        return bestScore;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    /** execute the genetic algorithm while the "running" attribute is set to true.
     *
     */
    @Override
    public void run() {
        running = true;
        double[] input = new double[PARAMETERS_COUNT];
        while (running){

            if (!aliveGeneration.isEmpty()) {    // while the current generation is still alive
                // for each alive bird
                for (int i = 0; i<this.aliveGeneration.size(); i++) {
                    Bird mrBird = aliveGeneration.get(i);
                    PipePair p;
                    // check for death
                    // p = the closest pipe (the only one possible for collision)
                    p = obstacles.getClosestRight(this.x - this.radius);
                    // if he collides with the pipe or hits the floor
                    if (p != null && (mrBird.checkCollision(p) || mrBird.getCenterY() + mrBird.getRadius() >= floorY)) {
                        aliveGeneration.remove(mrBird);
                        deadGeneration.add(mrBird);
                        handler.demand(mrBird, false);          // remove bird from pane.
                    }
                    else {

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
                        if (mrBird.shouldJump(input)) {
                            mrBird.jump();
                        }

                        // increase the current bird's score (survived another iteration)
                        mrBird.incScore();

                        long score = mrBird.getScore();
                        if (score > this.bestScore) // if the current score is above the best score
                            this.bestScore = score;     // set current score as best score
                    }
                }
            }

            else { //generation has perished
                // select a bird in accordance to the generations fitness
                Bird fittest = select();
                // discard the list of birds from the previous generation
                deadGeneration.clear();
                // start the game from the beginning
                obstacles.clearList();

                // create "generationSize" amount of birds
                for (int i = 0; i < generationSize; i++) {
                    // each new bird gets an identical brain to the selected bird...
                    Bird child = new Bird(x, floorY / 2, radius, colors[i], fittest.getBrain());
                    // but mutated with a chance of 10%
                    child.mutate(mutationChance, min_weight, max_weight, min_bias, max_bias);
                    // add the bird to the next generation and draw on screen.
                    aliveGeneration.add(child);
                    handler.demand(child, true);
                }
                this.generationNumber++;
                this.genText.increment();
                // save the new generation
                this.save("saves\\saveFile" + generationNumber);
            }

        }
    }

    /** Save the state of the bird manager and the birds it trained in order to continue training in later executions.
     *
     * @param fileName The name of the file or path of the file to save the brains into.
     * @return true - saved the files. false - did not save the files.
     */
    public boolean save(String fileName) {
        boolean status = true;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            int i = 0;
            StringBuilder str = new StringBuilder();
//            //add generation size
//            StringBuilder str = new StringBuilder("generationSize= ");
//            str.append(this.generationSize);

            //add x coordinate
            str.append("x= ").append(this.x);

            //add generation number
            str.append("\ngenerationNumber= ").append(this.generationNumber);

            //add best score
            str.append("\nbestScore= ").append(this.bestScore);

            //add radius
            str.append("\nradius= ").append(this.radius);

            //add maxPipeX
            str.append("\nmaxPipeX= ").append(this.maxPipeX);

            //add floor y coordinate
            str.append("\nfloorY= ").append(this.floorY);

            //add mutation chance
            str.append("\nmutationChance= ").append(this.mutationChance);

            //add maximum bias
            str.append("\nmax_bias= ").append(this.max_bias);

            //add minimum bias
            str.append("\nmin_bias= ").append(this.min_bias);

            //add maximum weight
            str.append("\nmax_weight= ").append(this.max_weight);

            //add minimum weight
            str.append("\nmin_weight= ").append(this.min_weight);

            for (Bird bird:this.aliveGeneration) {
                str.append('\n').append(bird.getBrain().toString());
            }
            for (Bird bird:this.deadGeneration) {
                str.append('\n').append(bird.getBrain().toString());
            }
            writer.write(str.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving!");
            e.printStackTrace();
            status = false;
        }


        return status;
    }
}
