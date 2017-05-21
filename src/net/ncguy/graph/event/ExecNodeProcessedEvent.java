package net.ncguy.graph.event;

import net.ncguy.graph.scene.logic.Node;

public class ExecNodeProcessedEvent extends AbstractEvent {

    public Node node;

    public ExecNodeProcessedEvent() {
    }

    public ExecNodeProcessedEvent(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public static interface ExecNodeProcessedListener {
        @Subscribe
        void onExecNodeProcessed(ExecNodeProcessedEvent event);
    }

}
