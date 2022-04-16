package com.example.smartbird;

public interface ActivationFunction
{
    public double activate(double x);

    /** Get the name of the activation function.
     *
     * @return The activation function name.
     */
    public String getName();
}
