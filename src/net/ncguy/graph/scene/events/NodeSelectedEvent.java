package net.ncguy.graph.scene.events;

import net.ncguy.graph.event.AbstractEvent;
import net.ncguy.graph.event.Subscribe;
import net.ncguy.graph.scene.logic.Node;

/**
 * Created by Guy on 16/01/2017.
 */
public class NodeSelectedEvent extends AbstractEvent {

    public Node node;

    public NodeSelectedEvent() {
    }

    public NodeSelectedEvent(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public static interface NodeSelectedListener {
        @Subscribe
        void onNodeSelected(NodeSelectedEvent event);
    }

}
