package com.example.smartbird;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PipePair {
    Pipe upper;
    Pipe lower;

    public PipePair(double x, double gapY, double width, double gapHeight, Color color)
    {
        upper = new Pipe(x, true, width, gapY, color);
        lower = new Pipe(x, false, width, GameManager.S_HEIGHT - (gapY+gapHeight), color);
    }


    public double getGapY(){
        return upper.getHeight();
    }

    public void moveLeft(double amount)
    {
        upper.moveLeft(amount);
        lower.moveLeft(amount);
    }

    public double getWidth()
    {
        return this.upper.getWidth();
    }

    public void addToPane(Pane p)
    {
        p.getChildren().add(this.upper);
        p.getChildren().add(this.lower);
    }

    public void removeFromPane(Pane p)
    {
        p.getChildren().remove(this.upper);
        p.getChildren().remove(this.lower);
    }

    /** Request addition or removal from pane.
     *
     * @param handler The command handler.
     * @param addition boolean value: true = to add; false = to remove.
     */
    public void request(CommandHandler handler, boolean addition)
    {
        handler.demand(this.upper, addition);
        handler.demand(this.lower, addition);
    }


    public Pipe getUpperPipe(){
        return new Pipe(this.upper);
    }

    public Pipe getLowerPipe(){
        return new Pipe(this.lower);
    }



    public double getX()
    {
        return upper.getX();
    }

    public static class Pipe extends Rectangle {

        /** Copy Constructor.
         *
         * @param o other pipe
         */
        public Pipe(Pipe o)
        {
            super(o.getWidth(), o.getHeight(), o.getFill());
            this.setX(o.getX());
            this.setY(o.getY());
        }

        public Pipe(double x, boolean isTop, double width, double height, Color color){
            super(width, height, color);
            this.setX(x);
            this.setY((isTop)?0:GameManager.S_HEIGHT-height);
        }


        public void moveLeft(double amount)
        {
            setX(getX()-amount);
        }


    }
}