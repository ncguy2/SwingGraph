package net.ncguy.graph.data.factories;

import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.ExecNode;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.Optional;

public class StartExecNode extends ExecNode {

    public StartExecNode(SceneGraph graph) {
        super(graph, "Start");

        Optional<Pin> pinFromIndex = getPinFromIndex(0, true);
        pinFromIndex.ifPresent(StartExecNode.this::removePin);
    }

    @Override
    public Object GetValueFromOutputPin(Pin pin) {
        return null;
    }

    @Override
    public IRuntimeCore runtime() {
        return null;
    }

    @Override
    protected void Process() {}

    public static class StartExecFactory extends NodeFactory {

        public StartExecFactory() {
            super("Start", "Core/Exec");
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new StartExecNode(graph);
        }
    }

}
