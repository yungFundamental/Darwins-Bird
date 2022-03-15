package com.example.smartbird;

public class Tanh implements ActivationFunction
{
    @Override
    public double activate(double x) {
        return Math.tanh(x);
    }
}

