package com.example.smartbird;

/*
This class was created so that none javafx threads may access panes.
 */

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;
import java.util.Queue;

public class CommandHandler
{
    private final Queue<ImageCommand> queue;
    private final Pane pane;

    public CommandHandler(Pane pane) {
        this.pane = pane;
        queue = new LinkedList<>();
    }

    public void demand(Node node, boolean toAdd){
        queue.add(new ImageCommand(node, toAdd));
    }
    public void execute(){
        if (queue.isEmpty()) {
            return;
        }
        ImageCommand command = queue.remove();
        if (command.isAdd()) {
//            if (command.getNode() instanceof Rectangle)
//                System.out.println("Inserting rectangle.");
            pane.getChildren().add(command.getNode());
        }
        else {
            if (command.getNode() instanceof Rectangle)
                System.out.println("Rectangle found: " + pane.getChildren().contains(command.getNode()));
            pane.getChildren().remove(command.getNode());
        }
    }
}
