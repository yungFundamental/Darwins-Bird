package com.example.smartbird;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bird extends Circle
{
    private double vel;
    private boolean alive;
    private final NeuralNetwork brain;

    // constructor to bird
    public Bird(double x, double y, double r, Color color, NeuralNetwork brain) {
        super(x,y,r,color);
        this.vel = 0;
        alive = true;
        this.brain = brain;

    }

    public void step() {
        setCenterY(getCenterY() + vel);
    }
    public void jump() {
        vel = -12;
    }
    public void accelerate(double a)
    {
        vel+=a;
    }

    public void setVelocity(double vel) {
        this.vel = vel;
    }

    public boolean checkCollision(PipePair p)
    {
        // X AXIS:
        // compare left side of pipes and right side of bird
        if (p.getX() > this.getRadius() + this.getCenterX())
            return false;



        // compare right side of pipes and left side of bird
        if (p.getX() + p.getWidth() < this.getCenterX() - this.getRadius()) {
            return false;
        }

        // Y AXIS:
        // compare top of lower pipe and bottom of bird
        //System.out.println("lower y: " + p.getLowerPipe().getY() + " bottom bird: " + (this.getCenterY() + this.getRadius()));
        if (p.getLowerPipe().getTranslateY() < this.getCenterY() + this.getRadius())
            return true;


        // compare bottom of upper pipe and top of bird
        // (if  the following condition is met the objects collided, else they did not.)
        return p.getUpperPipe().getTranslateY() + p.getUpperPipe().getHeight() > this.getCenterY() - this.getRadius();
    }
}