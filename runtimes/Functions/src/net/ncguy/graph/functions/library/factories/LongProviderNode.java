package net.ncguy.graph.functions.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.functions.FunctionsRuntime;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.List;

public class LongProviderNode extends Node {

    public Pin pin;
    public MutableProperty<Long> pinProperty;

    public LongProviderNode(SceneGraph graph) {
        super(graph, "Long provider");

        pinProperty = new MutableProperty<>("Value", 0L);
        addPin(pin = new Pin(this, "Value", false), pinProperty);
    }

    @Override
    public Object GetValueFromOutputPin(Pin pin) {
        if(propertyMap.containsKey(pin)) {
            return propertyMap.get(pin).get();
        }
        return null;
    }

    @Override
    public IRuntimeCore runtime() {
        return FunctionsRuntime.newestInstance;
    }

    @Override
    public void GetMutableProperties(List<MutableProperty> list) {
        super.GetMutableProperties(list);
        list.add(pinProperty);
    }

    public static class LongProviderFactory extends NodeFactory {

        public LongProviderFactory() {
            super("Long", "Functions/Mathematics");
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new LongProviderNode(graph);
        }
    }

}
