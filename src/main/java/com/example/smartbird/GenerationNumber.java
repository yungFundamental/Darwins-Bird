package com.example.smartbird;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This class displays on screen the current generation number
 */
public class GenerationNumber extends Text
{
    private int gen;     // number of the generation (0 = the first gen)
    private final String prefix;

    public GenerationNumber(double x, double y, Font font, int gen, String prefix){
        super(x,y, prefix + gen);
        this.gen = gen;
        this.setFont(font);
        this.prefix = prefix;

    }

    /** Refresh the text shown to match the gen attribute.
     *
     */
    private void update(){
        this.setText(prefix + gen);
    }

    public int getGen() {
        return gen;
    }

    public void setGen(int gen) {
        this.gen = gen;
        update();
    }

    /** Increment the gen attribute and refresh the text.
     *
     */
    public void increment(){
        this.gen++;
        update();
    }
}

