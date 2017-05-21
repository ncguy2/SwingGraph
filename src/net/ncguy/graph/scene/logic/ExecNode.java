package net.ncguy.graph.scene.logic;

import net.ncguy.graph.event.ExecNodeProcessedEvent;

import java.util.Optional;

public abstract class ExecNode extends Node {

    protected Pin inputPin;
    protected Pin outputPin;

    public ExecNode(SceneGraph graph, String title) {
        super(graph, title);
        addPin(inputPin  = new Pin(this, "Exec", true));
        addPin(outputPin = new Pin(this, "Then", false));
    }

    protected void Process_Call() {
        new ExecNodeProcessedEvent(this).fire();
        Process();
    }
    protected abstract void Process();

    public Optional<ExecNode> Next() {
        Process_Call();
        if(outputPin.isConnected()) {
            Node node = outputPin.connectedNode();
            if(node instanceof ExecNode)
                return Optional.of((ExecNode) node);
        }
        return Optional.empty();
    }

}
