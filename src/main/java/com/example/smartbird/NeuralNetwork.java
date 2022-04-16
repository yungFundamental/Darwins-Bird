package com.example.smartbird;

import java.util.Iterator;
import java.util.LinkedList;

public class NeuralNetwork
{
    private LinkedList<Layer> layers;
    private final int parameters;         //number of neurons in the input layer.


    public NeuralNetwork(int parameters){
        layers = new LinkedList<>();
        this.parameters = parameters;
    }

    /** Copy constructor.
     *
     * @param other The network to copy.
     */
    public NeuralNetwork(NeuralNetwork other)
    {
        layers = new LinkedList<>();
        this.parameters = other.getParameters();
        // copy the list of layers of other to this.
        for (Layer layer: other.layers)
            this.layers.add(new Layer(layer));
    }

//    public NeuralNetwork(String str)
//    {
//        StringBuilder template = new StringBuilder("NeuralNetwork\nNumber of neurons: [")
//    }

    public int getParameters() {
        return parameters;
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


    public void mutate(double chance, double min_weight, double max_weight, double min_bias, double max_bias) {
        // each bias and weight has the specified chance to be replaced with a random value

        for (Layer layer:this.layers){  // for each layer

            double [][]weights = layer.getWeights();
            for (int i = 0; i<weights.length; i++)
                for (int j = 0; j<weights[0].length; j++)   // for each weight
                    if (Math.random() <= chance)                // if the chance was met
                        weights[i][j] = min_weight + (max_weight-min_weight) * Math.random();   //set as random value.
            layer.setWeights(weights);

            double []biases = layer.getBiases();
            for (int i = 0; i<biases.length; i++)   //for each bias
                if (Math.random() <= chance)            // if the chance was met
                    biases[i] = min_bias + (max_bias-min_bias) * Math.random();
            layer.setBiases(biases);
        }
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder("layers:\n[");
        StringBuilder NeuronCounts = new StringBuilder("[" + this.parameters);

        int i = 0;
        for (Layer l:this.layers) {
            temp.append("layer").append(i++).append(": ").append(l.toString()).append('\n');
            NeuronCounts.append(",").append(l.getNeuronCount());
        }
        NeuronCounts.append(']');
        temp.append(']');
        return "NeuralNetwork {\nNumber_of_neurons: " + NeuronCounts + '\n' + temp + "\n}";
    }
}
