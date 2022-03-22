package com.example.smartbird;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.Queue;

public class RequestQueue
{
    private Queue<ImageCommand> queue;
    private final Pane pane;

    public RequestQueue(Pane pane) {
        this.pane = pane;
        queue = new LinkedList<>();
    }

    public void demand(Node node, boolean toAdd){
        queue.add(new ImageCommand(node, toAdd));
    }
    public void execute(){
        if (queue.isEmpty())
            return;
        ImageCommand command = queue.remove();
        if (command.isAdd())
            pane.getChildren().add(command.getNode());
        else
            pane.getChildren().remove(command.getNode());
    }
}
