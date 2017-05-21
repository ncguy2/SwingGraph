package net.ncguy.graph.runtimes.hue.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtimes.hue.HueRuntime;
import net.ncguy.graph.runtimes.hue.compiler.InstructionHueCompiler;
import net.ncguy.graph.scene.logic.ExecNode;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class SendInstructionsNode extends ExecNode {

    public Pin targetHostPin;
    public Pin targetPortPin;
    public Pin instructionIdPin;
    public Pin colourAPin;
    public Pin colourBPin;
    public Pin colourCPin;
    public Pin colourDPin;

    MutableProperty<InetAddress> targetHostProperty;
    MutableProperty<Integer> targetPortProperty;
    MutableProperty<Commands> instructionProperty;
    MutableProperty<Color> colourAProperty;
    MutableProperty<Color> colourBProperty;
    MutableProperty<Color> colourCProperty;
    MutableProperty<Color> colourDProperty;

    public SendInstructionsNode(SceneGraph graph) {
        super(graph, "Send Instructions");
        InetAddress addr = null;
        try {
            addr = InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 168, 2, (byte) 255});
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        targetHostProperty = new MutableProperty<>("Target host", addr);
        targetPortProperty = new MutableProperty<>("Target port", 3300);
        instructionProperty = new MutableProperty<>("Instruction", Commands.SET);
        colourAProperty = new MutableProperty<>("Colour A", Color.WHITE);
        colourBProperty = new MutableProperty<>("Colour B", Color.WHITE);
        colourCProperty = new MutableProperty<>("Colour C", Color.WHITE);
        colourDProperty = new MutableProperty<>("Colour D", Color.WHITE);

        addPin(targetHostPin = new Pin(this, "Target host", true), targetHostProperty);
        addPin(targetPortPin = new Pin(this, "Target port", true), targetPortProperty);
        addPin(instructionIdPin = new Pin(this, "Instruction ID", true), instructionProperty);
        addPin(colourAPin = new Pin(this, "Colour A", true), colourAProperty);
        addPin(colourBPin = new Pin(this, "Colour B", true), colourBProperty);
        addPin(colourCPin = new Pin(this, "Colour C", true), colourCProperty);
        addPin(colourDPin = new Pin(this, "Colour D", true), colourDProperty);
    }

    @Override
    public void GetMutableProperties(List<MutableProperty> list) {
        list.add(targetHostProperty);
        list.add(targetPortProperty);
        list.add(instructionProperty);
        list.add(colourAProperty);
        list.add(colourBProperty);
        list.add(colourCProperty);
        list.add(colourDProperty);
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
    protected void Process() {
        new InstructionHueCompiler().CompileNode(this);
    }

    public static enum Commands {
        SET(1, "Set"),
        PULSE(2, "Pulse"),
        SWEEP(4, "Sweep"),
        WIPE(5, "Wipe"),
        DUAL_WIPE(6, "Dual wipe"),
        TRI_WIPE(7, "Tri wipe"),
        QUAD_WIPE(8, "Quad wipe"),
        TWIN_WIPE_SINGLE(9, "Twin-wipe Single"),
        TWIN_WIPE(10, "Twin wipe"),
        INST_WORM(31, "Worm"),
        INST_DUAL_WIPE(32, "Dual worm"),
        INST_TRI_WIPE(33, "Tri worm"),
        INST_QUAD_WIPE(34, "Quad worm"),
        INST_BREATHING(35, "Breathing"),
        ;

        public final byte id;
        public final String name;
        Commands(byte id) { this(id, null); }
        Commands(byte id, String name) {
            this.id = id;
            this.name = name != null ? name : name();
        }

        Commands(int i, String name) {
            this((byte)i, name);
        }
    }

    public static class SendInstructionFactory extends NodeFactory {

        public SendInstructionFactory() {
            super("Send Instruction", "Hue/System");
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new SendInstructionsNode(graph);
        }
    }

}
