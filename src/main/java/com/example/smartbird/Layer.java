package com.example.smartbird;

/**
 * Note: this class represents hidden and output layers, but the input layer of the neural network will be represented
 * by a simple double array.
 */
public class Layer
{
    private double[] input;         //the values of the neurons from the previous layer
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

    public void setInput(double[] input) {
        this.input = input;
    }

    public double[] getOutput() {
        return output.clone();
    }

    /** forward propagation.
     *
     */
    public void calculateOutput(){
        for (int i = 0; i < output.length; i++){
            output[i] = biases[i];
            for (int j = 0; j <  input.length; j++)
                output[i] += weights[i][j]*input[j];
            output[i] = activationFunc.activate(output[i]);
        }

    }

    /** Randomize all weights and biases and associated with this layer.
     *
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
}
