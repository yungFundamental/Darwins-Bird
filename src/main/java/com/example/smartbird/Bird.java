package com.example.smartbird;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bird extends Circle
{
    private double velocity;
    private final NeuralNetwork brain;
    private long score;

    // constructor to bird
    public Bird(double x, double y, double r, Color color, NeuralNetwork brain) {
        super(x,y,r,color);
        this.velocity = 0;
        this.brain = brain;
        score = 0;

    }

    public void mutate(double chance, double min_weight, double max_weight, double min_bias, double max_bias){
        brain.mutate(chance, min_weight, max_weight, min_bias, max_bias);
    }

    public NeuralNetwork getBrain() {
        return new NeuralNetwork(brain);
    }

    /** move in y coordinate accordance to current velocity.
     *
     */
    public void step() {
//        if (this.getCenterY() - this.getRadius() < 0)
//        {
//            this.velocity = -4;
//            return;
//        }
        setCenterY(getCenterY() + velocity);
    }

    public void jump() {
        velocity = -12;
    }

    /** accelerate in the y-axis.
     *
     * @param a acceleration value.
     */
    public void accelerate(double a)
    {
        velocity +=a;
    }

    public void incScore(){
        score++;
    }

    public long getScore() {
        return score;
    }

    public void setVelocity(double vel) {
        this.velocity = vel;
    }

    /** Check if the bird collided with pipePair p.
     *
     * @param p Pipe pair.
     * @return True - collided, False - didn't collide.
     */
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
        if (p.getLowerPipe().getY() < this.getCenterY() + this.getRadius())
            return true;


        // compare bottom of upper pipe and top of bird
        // (if  the following condition is met the objects collided, else they did not.)
        return p.getUpperPipe().getY() + p.getUpperPipe().getHeight() > this.getCenterY() - this.getRadius();
    }

    public double getVelocity(){
        return velocity;
    }

    public boolean shouldJump(double []input){
        return brain.forwardPropagation(input)[0] > 0.5;
    }
}