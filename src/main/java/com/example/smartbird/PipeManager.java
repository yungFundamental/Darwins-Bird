package com.example.smartbird;

import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PipeManager implements Runnable
{
    private final Random rand;
    private final List<PipePair> list;
    private final CommandHandler requestHandler;

    private int timer;
    private final int TIMER_RESET;
    private final int startX;     // where the pipes will be created
    private final int endX;       // where the pipes will disappear
    private final double width;   //width of the pipes
    private final double height;  //height of the gap
    private final double bottom;    //The y coordinate of the bottom of the screen.
    private final double acceleration;  //The rate of acceleration of the pipes speed.
    private double speed;       //current speed of the pipes
    private final double initialSpeed;  //Initial speed of the pipes.
    private boolean running;


    /** Basic Constructor.
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
        this.initialSpeed = this.speed = initialSpeed;
        this.bottom = bottom;
        this.TIMER_RESET = period;
        this.acceleration = acceleration;

        list = new LinkedList<>();
        timer = 0;
        running = true;
    }

    private synchronized void createPipe()
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
        setSpeed(initialSpeed);
        for (PipePair p : this.list){
            p.request(requestHandler, false);
        }
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
    public synchronized void pop()
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
        double minDis;      //minimum distance.
        PipePair res;       //the closest PipePair to the right of the x coordinate
        double dis;

        //initialize the variables:
        minDis = startX - x + 1;    // impossibly large distance
        res = null;

        if (this.list.isEmpty())
            return null;

        // for each PipePair p in the list of pipes.
        for (int i = 0; i<this.list.size(); i++)
        {
            PipePair p = list.get(i);
            if (p==null) {
                System.out.println("<======3");     //shouldn't happen
                continue;
            }

            // distance = right side of the pipe - x
            dis = p.getX() + p.getWidth() - x;

            // if the pipe is left to the given x
            if (dis<0)
                continue;
            // if the distance is smaller than minimum distance.
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
