package net.ncguy.graph.data.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.ExecNode;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

public class ExecForNode extends ExecNode {

    Pin iterAmtPin;

    Pin iterExecPin;
    Pin iterIndexPin;

    MutableProperty<Integer> iterAmtProperty;
    MutableProperty<Integer> iterIndexProperty;

    public ExecForNode(SceneGraph graph) {
        super(graph, "For loop");

        iterAmtProperty = new MutableProperty<>("Iterations", 1);
        addPin(iterAmtPin = new Pin(this, "Iterations", true), iterAmtProperty);

        iterExecPin = outputPin;
        iterExecPin.label = "Loop";

        iterIndexProperty = new MutableProperty<>("Index", 0);
        addPin(iterIndexPin = new Pin(this, "Index", false), iterIndexProperty);
        addPin(outputPin = new Pin(this, "Then", false));
    }

    @Override
    protected void Process() {
        int max = Integer.parseInt(GetValueFromInputPin(iterAmtPin).toString());
        for(int i = 0; i < max; i++) {
            iterIndexProperty.set(i);
            Node node = iterExecPin.connectedNode();
            if (node != null && node instanceof ExecNode)
                CompileNode((ExecNode) node);
        }
    }

    protected void CompileNode(ExecNode node) {
        node.Next().ifPresent(this::CompileNode);
    }

    @Override
    public IRuntimeCore runtime() {
        return null;
    }

    public static class ExecForFactory extends NodeFactory {

        public ExecForFactory() {
            super("For loop", "Core/Structure");
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new ExecForNode(graph);
        }
    }

}
