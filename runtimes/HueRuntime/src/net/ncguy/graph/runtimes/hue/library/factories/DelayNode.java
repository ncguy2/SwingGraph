package net.ncguy.graph.runtimes.hue.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtimes.hue.HueRuntime;
import net.ncguy.graph.scene.logic.ExecNode;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.List;

public class DelayNode extends ExecNode {

    Pin delayPin;
    MutableProperty<Long> delayProperty;

    public DelayNode(SceneGraph graph) {
        super(graph, "Delay");

        delayProperty = new MutableProperty<>("Delay", 1000L);
        addPin(delayPin = new Pin(this, "Delay ms", true), delayProperty);
    }

    @Override
    protected void Process() {
        String s = GetValueFromInputPin(delayPin).toString();
        try {
            Thread.sleep(Long.parseLong(s));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object GetValueFromOutputPin(Pin pin) {
        return null; // No output pins
    }

    @Override
    public IRuntimeCore runtime() {
        return HueRuntime.newestInstance;
    }

    @Override
    public void GetMutableProperties(List<MutableProperty> list) {
        super.GetMutableProperties(list);
        list.add(delayProperty);
    }

    public static class DelayFactory extends NodeFactory {

        public DelayFactory() {
            super("Delay", "Hue/System");
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new DelayNode(graph);
        }
    }

}
