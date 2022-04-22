package com.example.smartbird;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Note: this class represents hidden and output layers, but the input layer of the neural network will be represented
 * by a simple double array.
 */
public class Layer
{
    private double[] output;        //the values of the current neurons
    private double[][] weights;      //weight[i][j] = weight between neuron i in output and neuron j in input.
    private double[] biases;        //biases[i] = bias of neuron i in input
    private final ActivationFunction activationFunc;    //which activation function to use.

    public Layer(int inputCount,int neuronCount, ActivationFunction activationFunction){
        output = new double[neuronCount];
        biases = new double[neuronCount];
        weights = new double[neuronCount][inputCount];
        this.activationFunc = activationFunction;
    }

    private Layer(double[] biases, double[][] weights, ActivationFunction activationFunction){
        this.biases = biases;
        this.weights = weights;
        this.activationFunc = activationFunction;
        this.output = new double[biases.length];
    }

    public Layer(Layer other){
        biases = other.getBiases();
        weights = other.getWeights();
        output = new double[other.getNeuronCount()];
        this.activationFunc = other.getActivationFunc();
    }

    public ActivationFunction getActivationFunc() {
        return activationFunc;
    }

    public void setBias(int neuron, double val) {
        this.biases[neuron] = val;
    }

    public double getBias(int neuron) {
        return biases[neuron];
    }

    public void setBiases(double []newBiases)
    {
        System.arraycopy(newBiases, 0, biases, 0, output.length);
    }

    public double[] getBiases() {
        double[] dup = new double[biases.length];
        System.arraycopy(this.biases, 0, dup, 0, biases.length);
        return dup;
    }

    public double getWeight(int inNeuron, int outNeuron) {
        return weights[outNeuron][inNeuron];
    }

    public void setWeight(int inNeuron, int outNeuron, double val) {
        weights[outNeuron][inNeuron] = val;
    }

    public double[][] getWeights() {
        //return weights;
        int len;
        double [][]dup = new double[weights.length][];
        for (int i=0; i<weights.length; i++)
        {
            len = weights[i].length;
            dup[i] = new double[len];
            System.arraycopy(weights[i], 0, dup[i], 0, len);
        }
        return dup;
    }

    public void setWeights(double [][]mat) {
        int len;
        for (int i=0; i<weights.length; i++)
        {
            len = mat[i].length;
            weights[i] = new double[len];
            System.arraycopy(mat[i], 0, weights[i], 0, len);
        }
    }

    public int getNeuronCount(){
        return output.length;
    }

    /**
     *
     * @return The amount of neurons in previous layer.
     */
    public int getInputCount() {
        return weights[0].length;
    }

    public double[] getOutput() {
        return output.clone();
    }

    /** forward propagation. Calculate the value of the neurons.
     *
     * @param input the values of the neurons in the previous layer. Size should be equal to the inputCount.
     */
    public void calculateOutput(double[] input){
        for (int i = 0; i < output.length; i++){
            output[i] = biases[i];
            for (int j = 0; j <  input.length; j++)
                output[i] += weights[i][j]*input[j];
            output[i] = activationFunc.activate(output[i]);
        }

    }

    /** Randomize all weights and biases and associated with this layer.
     *
     * @param min_weight Minimum value for each weight.
     * @param max_weight Maximum value for each weight.
     * @param min_bias Minimum value for each bias.
     * @param max_bias Maximum value for each bias.
     */
    public void randomize(double min_weight, double max_weight, double min_bias, double max_bias){
        // set all weights as random number between MIN_WEIGHT and MAX_WEIGHT
        for(int i = 0; i<weights.length; i++)
            for (int j = 0; j<weights[0].length; j++)
                weights[i][j] = min_weight + (max_weight-min_weight) * Math.random();

        // set all biases as random number between MIN_BIAS and MAX_BIAS
        for (int i=0; i<biases.length; i++)
            biases[i] = min_bias + (max_bias-min_bias) * Math.random();
    }

    @Override
    public String toString() {
        StringBuilder mat = new StringBuilder("[");
        for (double []arr: this.weights)
            mat.append(Arrays.toString(arr)).append(',');
        // remove last comma and replace with ']'
        mat.deleteCharAt(mat.length()-1);
        mat.append(']');

        return "Layer{" +
                "weights=" + mat +
                ";biases=" + Arrays.toString(biases) +
                ";activationFunc=" + activationFunc.getName() +
                '}';
    }

    /** Converts string into a new instance of Layer.
     *
     * @param str The string. result of the toString function.
     * @param inputCount The amount of neurons in the previous layer.
     * @param neuronCount The amount of neurons in the current layer.
     * @return The layer that was converted into the given string.
     */
    public static Layer fromString(String str, int inputCount, int neuronCount){
        String[] split = str.split("[{}=;]", 0);
        double[][] weights = new double[neuronCount][inputCount];
        double[] bias = new double[neuronCount];
        ActivationFunction function;
        //split[0] = "layer2: Layer"
        //split[1] = "weights"
        //split[2] is the weight matrix in string format
        String[] mat = split[2].split(",", 0);
        for (int i = 0; i<neuronCount; i++)
            for (int j=0; j<inputCount; j++)
                weights[i][j] = Double.parseDouble(mat[i*inputCount + j].replaceAll("[\\[\\] ]", ""));

        //split[3] = "biases"
        //split[4] is the biases in string format
        String[] arr = split[4].split(",",0);
        for (int i = 0; i<neuronCount; i++)
            bias[i] = Double.parseDouble(arr[i].replaceAll("[\\[\\] ]", ""));

        //split[5] = "activationFunc"
        //split[6] is the activation function name.
        //get the activation function from the name.
        switch (split[6]) {
            case "ReLu" -> function = new ReLU();
            case "Sigmoid" -> function = new Sigmoid();
            case "Tanh" -> function = new Tanh();
            default -> {
                System.out.println("Unknown activation function...");
                return null;
            }
        }

        return new Layer(bias, weights, function);
    }
}
