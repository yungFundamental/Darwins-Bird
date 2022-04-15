package com.example.smartbird;

import javafx.scene.Node;

public class ImageCommand {
    private final boolean add;    //true - add the following object, false - remove the following object.
    private final Node node;      //the object to be removed/added.

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
