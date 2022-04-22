package com.example.smartbird;

/**
 * Sigmoid activation function.
 */
public class Sigmoid implements ActivationFunction
{

    @Override
    public double activate(double x) {
        return 1/(1+Math.exp(-x));
    }

    /**
     * Get the name of the activation function.
     *
     * @return The activation function name.
     */
    @Override
    public String getName() {
        return "Sigmoid";
    }
}
