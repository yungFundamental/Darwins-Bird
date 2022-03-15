package com.example.smartbird;

public class ReLU implements ActivationFunction
{
    @Override
    public double activate(double val){
        return Math.max(0,val);
    }
}
