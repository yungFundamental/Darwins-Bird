package com.example.smartbird;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ImageCommand {
    private boolean add;    //true - add the following object, false - remove the following object.
    private Node node;      //the object to be removed/added.

    public ImageCommand(Node node, boolean toAdd) {
        add = toAdd;
        this.node = node;
    }

    public boolean isAdd() {
        return add;
    }

    public Node getNode() {
        return node;
    }
}
