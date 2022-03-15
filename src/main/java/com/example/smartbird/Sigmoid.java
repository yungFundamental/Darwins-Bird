package com.example.smartbird;

public class Sigmoid implements ActivationFunction
{

    @Override
    public double activate(double x) {
        return 1/(1+Math.exp(-x));
    }
}
