package net.ncguy.graph.runtimes.hue.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.data.constraints.IntegerPropertyConstraint;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtimes.hue.HueRuntime;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;
import net.ncguy.graph.utils.StringUtils;

import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

public class MakeColourNode extends Node {

    Pin redPin;
    Pin greenPin;
    Pin bluePin;
    Pin colourPin;

    MutableProperty<Integer> redProperty;
    MutableProperty<Integer> greenProperty;
    MutableProperty<Integer> blueProperty;
    MutableProperty<Color> colourProperty;

    public MakeColourNode(SceneGraph graph) {
        super(graph, "Make Colour");

        BiConsumer<Integer, Integer> onPropertyChange = (old, cur) -> renderingComponent.UpdateTitle();

        IntegerPropertyConstraint intPropConstraint = new IntegerPropertyConstraint(0, 255);

        redProperty   = new MutableProperty<>("Red", 255, intPropConstraint);
        greenProperty = new MutableProperty<>("Green", 255, intPropConstraint);
        blueProperty  = new MutableProperty<>("Blue", 255, intPropConstraint);
        colourProperty = new MutableProperty<>("Colour", new Color(255, 255, 255));

        redProperty.AddChangeListener(onPropertyChange);
        greenProperty.AddChangeListener(onPropertyChange);
        blueProperty.AddChangeListener(onPropertyChange);

        addPin(redPin   = new Pin(this, "Red", true), redProperty);
        addPin(greenPin = new Pin(this, "Green", true), greenProperty);
        addPin(bluePin  = new Pin(this, "Blue", true), blueProperty);

        addPin(colourPin = new Pin(this, "Colour", false), colourProperty);
    }

    @Override
    public void GetMutableProperties(List<MutableProperty> list) {
        super.GetMutableProperties(list);
        list.add(redProperty);
        list.add(greenProperty);
        list.add(blueProperty);
    }

    @Override
    public String GetTitle() {
        String colStr = "#";
        colStr += StringUtils.leadingZeros(Integer.toHexString(GetCastValueFromInputPin(redPin, 255)), 2);
        colStr += StringUtils.leadingZeros(Integer.toHexString(GetCastValueFromInputPin(greenPin, 255)), 2);
        colStr += StringUtils.leadingZeros(Integer.toHexString(GetCastValueFromInputPin(bluePin, 255)), 2);
        return super.GetTitle() + " - " + colStr;
    }

    @Override
    public Object GetValueFromOutputPin(Pin pin) {
        colourProperty.set(new Color(
                GetCastValueFromInputPin(redPin, 255),
                GetCastValueFromInputPin(greenPin, 255),
                GetCastValueFromInputPin(bluePin, 255)));
        return super.GetValueFromOutputPin(pin);
    }

    @Override
    public IRuntimeCore runtime() {
        return HueRuntime.newestInstance;
    }

    public static class MakeColourFactory extends NodeFactory {

        public MakeColourFactory() {
            super("Make Colour", "Hue/Data");
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new MakeColourNode(graph);
        }
    }

}
