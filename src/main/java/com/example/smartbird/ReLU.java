package com.example.smartbird;

/**
 * ReLU activation function.
 */
public class ReLU implements ActivationFunction
{
    @Override
    public double activate(double val){
        return Math.max(0,val);
    }

    /**
     * Get the name of the activation function.
     *
     * @return The activation function name.
     */
    @Override
    public String getName(){
        return "ReLu";
    }
}
