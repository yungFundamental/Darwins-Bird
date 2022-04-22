package com.example.smartbird;

/**
 * Tanh activation function.
 */
public class Tanh implements ActivationFunction
{
    @Override
    public double activate(double x) {
        return Math.tanh(x);
    }

    /**
     * Get the name of the activation function.
     *
     * @return The activation function name.
     */
    @Override
    public String getName() {
        return "Tanh";
    }
}

