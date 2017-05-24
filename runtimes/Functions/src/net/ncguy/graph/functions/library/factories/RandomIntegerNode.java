package net.ncguy.graph.functions.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.functions.FunctionsRuntime;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.utils.FastRand;

public class RandomIntegerNode extends Node {

    public Pin pin;
    public MutableProperty<Integer> pinProperty;
    public FastRand rand;

    public RandomIntegerNode(SceneGraph graph) {
        super(graph, "Random Integer provider");
        rand = new FastRand();
        pinProperty = new MutableProperty<>("Value", 0);
        addPin(pin = new Pin(this, "Value", false), pinProperty);
    }

    @Override
    public Object GetValueFromOutputPin(Pin pin) {
        pinProperty.set(rand.randAbsInt());
        return super.GetValueFromOutputPin(pin);
    }

    @Override
    public IRuntimeCore runtime() {
        return FunctionsRuntime.newestInstance;
    }
}
