package com.example.smartbird;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class was created so that non-javafx-threads may access panes.
 */
public class CommandHandler
{
    private final Queue<ImageCommand> queue;
    private final Pane pane;

    public CommandHandler(Pane pane) {
        this.pane = pane;
        queue = new LinkedList<>();
    }

    /** Add a ImageCommand to the queue.
     *
     * @param node The node to add/remove.
     * @param toAdd true - add the node, false - remove the node.
     */
    public void demand(Node node, boolean toAdd){
        queue.add(new ImageCommand(node, toAdd));
    }

    /** Execute a command from the queue. If there are no commands, do nothing.
     *
     */
    public void execute(){
        // if there are no commands to execute,
        if (queue.isEmpty()) {
            // do nothing.
            return;
        }

        // get the first command (first in)
        ImageCommand command = queue.remove();

        // execute the command
        command.execute(this.pane);
    }
}
