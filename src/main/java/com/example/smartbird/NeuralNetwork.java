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

    public int getParameters() {
        return parameters;
    }

    public void addLayer(int neurons, ActivationFunction activationFunction){
        int inputSize = parameters;
        if(!layers.isEmpty())
            inputSize = layers.getLast().getNeuronCount();
        layers.add(new Layer(inputSize, neurons, activationFunction));
    }

    private void addLayer(Layer layer)
    {
        layers.add(layer);
    }

    /** Set each weight and bias as a random value in a given range.
     *
     * @param min_weight Minimum value for each weight.
     * @param max_weight Maximum value for each weight.
     * @param min_bias Minimum value for each bias.
     * @param max_bias Maximum value for each bias.
     */
    public void randomize(double min_weight, double max_weight, double min_bias, double max_bias){
        for (Layer layer:layers) {
            layer.randomize(min_weight, max_weight, min_bias, max_bias);
        }
    }

    /** Update all neurons in accordance to input layer neurons.
     *
     * @param input Values of input layer neurons. Order of parameters is important.
     * @return Values of output layer neurons after forward propagation.
     */
    public double[] forwardPropagation(double[] input){
        // in = the value of the neurons of the previous layer
        double[] in = input;
        // for every layer, starting from the first hidden layer:
        for (Layer layer:layers) {
            // calculate output with in as the value of the previous layer
            layer.calculateOutput(in);
            // in = current layer.
            in = layer.getOutput();
        }
        // finally, the values of in are the values of the output layer neurons
        return in;
    }

    /** each value (weight and bias) has a chance to be changed into a random value in a range.
     *
     * @param chance What chance each value has to be mutated.
     * @param min_weight minimum value of a mutated weight.
     * @param max_weight maximum value of a mutated weight.
     * @param min_bias minimum value of a mutated bias.
     * @param max_bias maximum value of a mutated bias.
     */
    public void mutate(double chance, double min_weight, double max_weight, double min_bias, double max_bias) {
        // each bias and weight has the specified chance to be replaced with a random value

        for (Layer layer:this.layers){  // for each layer

            double [][]weights = layer.getWeights();
            // for each weight
            for (int i = 0; i<weights.length; i++)
                for (int j = 0; j<weights[0].length; j++)
                    //a random number between 0-1 is smaller than x, has a chance of x.
                    if (Math.random() <= chance)
                        // if the chance was met:
                        // set the weight as a random value between min_weight and max_weight.
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
        return "NeuralNetwork{\nNumber_of_neurons: " + NeuronCounts + '\n' + temp + "\n}";
    }

    /** Converts a toString of a neural network back into a neural network.
     *
     * @param str The resulting string of a toString function of a neural network instance.
     * @return The neural network.
     */
    public static NeuralNetwork fromString(String str){
        //split by lines
        String[] lines = str.split("\n", 0);
        NeuralNetwork res;
        int lineNumber;

        int[] number_of_neurons;
        String[] strArr = lines[0].split("[\\[\\],]",0);

        // fill the number of neurons array in accordance to the Number of neurons line (second line)
        //string before '['  is not an element, therefore there are strArr.length-1 elements
        number_of_neurons = new int[strArr.length-1];
        for (int i=1; i< strArr.length; i++)
            number_of_neurons[i-1] = Integer.parseInt(strArr[i]);

        //parameters = the number of neurons in the first layer.
        res = new NeuralNetwork(number_of_neurons[0]);

        lineNumber = 2;     //the first line of a layer instance is at line 3 (index 2)
        while (!lines[lineNumber].equals("]")){
            Layer layer = Layer.fromString(lines[lineNumber], number_of_neurons[lineNumber-2],number_of_neurons[lineNumber-1]);
            if (layer == null)
                return null;
            res.addLayer(layer);
            lineNumber++;
        }
        return res;
    }
}

