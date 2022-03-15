package com.example.smartbird;

import java.util.LinkedList;

public class NeuralNetwork
{
    private LinkedList<Layer> layers;
    private final int parameters;         //number of neurons in the input layer.


    public NeuralNetwork(int parameters){
        layers = new LinkedList<>();
        this.parameters = parameters;
    }

    public void addLayer(int neurons, ActivationFunction activationFunction){
        int inputSize = parameters;
        if(!layers.isEmpty())
            inputSize = layers.getLast().getNeuronCount();
        layers.add(new Layer(inputSize, neurons, activationFunction));
    }

    public void randomize(double min_weight, double max_weight, double min_bias, double max_bias){
        for (Layer layer:layers) {
            layer.randomize(min_weight, max_weight, min_bias, max_bias);
        }
    }

    public double [] forwardPropagation(double[] input){
        double[] in = input;
        for (Layer layer:layers) {
            layer.setInput(in);
            layer.calculateOutput();
            in = layer.getOutput();
        }
        return in;
    }

    /** Create neural networks in image of a specific neural network.
     *
     * @param source The source neural network that the others will resemble.
     * @param amount The amount of neural networks that will be returned.
     * @return an array of neural networks that resemble the source.
     */
    public NeuralNetwork[] inImageOf(NeuralNetwork source, int amount){
        return null;
        //TODO insert code.
    }
}
