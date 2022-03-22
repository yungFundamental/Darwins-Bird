package com.example.smartbird;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeManager implements Runnable
{
    private Random rand;
    private List<PipePair> list;
    private CommandHandler requestHandler;

    private int timer;
    private final int TIMER_RESET;
    private final int startX;     // where the pipes will be created
    private final int endX;       // where the pipes will disappear
    private double width;   //width of the pipes
    private final double height;  //height of the gap
    private final double bottom;
    private final double acceleration;
    private double speed;
    private boolean running;


    /**
     *
     * @param commandHandler a command handler to handle addition and removal of pipes.
     * @param maxX The maximum x coordinate of a pipe pair created. This is where each pipe pair will be initialized.
     * @param minX The minimum x coordinate of a pipe pair created. This is where each pipe pair will be deleted.
     * @param width Width of the pipes.
     * @param gapHeight Height of each gap.
     * @param initialSpeed  Initial speed of the pipes.
     * @param bottom The y coordinate of the bottom of the screen.
     * @param period How many frames between each creation of PipePairs.
     * @param acceleration The rate of acceleration of the pipes speed. Note: should be around 0.001.
     */
    public PipeManager(CommandHandler commandHandler, int maxX, int minX, double width, double gapHeight, double initialSpeed, int bottom,
                       int period, double acceleration)
    {
        rand = new Random();
        this.requestHandler = commandHandler;
        this.startX = maxX;
        this.endX = minX;
        this.width = width;
        this.height = gapHeight;
        this.speed = initialSpeed;
        this.bottom = bottom;
        this.TIMER_RESET = period;
        this.acceleration = acceleration;

        list = new LinkedList<>();
        timer = 0;
        running = true;
    }

    private void createPipe()
    {
        PipePair add = new PipePair(startX, rand.nextInt((int)(bottom-height-40)), width, height, Color.GREEN);
        list.add(add);
        // add the pipe pair to the pane.
        add.request(requestHandler, true);
    }

    private void stop(){
        running = false;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void clearList()
    {
        System.out.println("preHola");
        for (PipePair p : this.list){
            //remove from pane.
//            p.request(requestHandler, false);
            System.out.println("hola");
        }
        System.out.println("PostHola");
        list.clear();

    }

    public void step() {
        pop();
        if (timer<=0) {
            createPipe();
            timer = TIMER_RESET;
        }
        else
            timer-=speed;

        for (PipePair i: list)
            i.moveLeft(speed);

        accelerate(acceleration);

    }

    /** check if the leftmost pipe is past the endX. If so, remove it.
     *
     */
    public void pop()
    {
        if (list.size() <= 0)
            return;
        if (list.get(0).getX() <= endX){    //if leftmost pipe pair is too left
            list.get(0).request(requestHandler, false);     //request removal from pane
            list.remove(0);                                   //remove from list
        }

    }

    /** Accelerate the speed of the moving pipes.
     *
     * @param a accelerationRate
     */
    public void accelerate(double a) {
        this.speed += a;
    }

    /** Find the closest PipePair to the right of a given x coordinate
     *  WARNING: returns reference to actual PipePair, do not change.
     * @param x the given x coordinate
     * @return The found PipePair
     */
    public PipePair getClosestRight(double x)
    {
        double minDis;
        PipePair res;
        double dis;

        // impossibly large distance
        minDis = startX - x + 1;
        res = null;
        if (list.isEmpty())
            return null;
        for (PipePair p: this.list)
        {
            dis = p.getX() + p.getWidth() + -x;
            if (dis<0)
                continue;
            if (dis<minDis)
            {
                minDis = dis;
                res = p;
            }

        }
        return res;
    }

    public double getSpeed(){
        return speed;
    }

    @Override
    public void run() {
        running = true;
        while (running)
            step();
    }


}
