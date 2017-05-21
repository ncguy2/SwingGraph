package net.ncguy.graph.functions.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.functions.FunctionsRuntime;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.List;

public class IntProviderNode extends Node {

    public Pin pin;
    public MutableProperty<Integer> pinProperty;

    public IntProviderNode(SceneGraph graph) {
        super(graph, "Integer provider");

        pinProperty = new MutableProperty<>("Value", 0);
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

    public static class IntProviderFactory extends NodeFactory {

        public IntProviderFactory() {
            super("Integer", "Functions/Mathematics");
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new IntProviderNode(graph);
        }
    }

}
