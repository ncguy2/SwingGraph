package net.ncguy.graph.library.factories;

import net.ncguy.graph.library.GLSLFactory;
import net.ncguy.graph.library.GLSLNode;
import net.ncguy.graph.library.GLSLNodeFactory;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;

import java.util.Optional;

/**
 * Created by Guy on 15/01/2017.
 */
public class MakeVec2Node extends GLSLNode {

    public MakeVec2Node(SceneGraph graph) {
        super(graph, "Make Vector 2");
        addPin(new Pin(this, "X", true));
        addPin(new Pin(this, "Y", true));
        addPin(new Pin(this, "Vec2", false));
    }

    static int globalId;

    int id;
    boolean fragmentUsed;

    @Override
    public String getUniforms() {
        return "vec2 "+getVariable()+";";
    }

    @Override
    public String getVariable(int pinId) {
        return "Vector2"+id;
    }

    @Override
    public String getFragment() {
        if(fragmentUsed)
            return "";
        fragmentUsed = true;

        Optional<Pin> pinX = getPinFromIndex(0, true);
        Optional<Pin> pinY = getPinFromIndex(1, true);

        String xS = "0.0";
        String yS = "0.0";
        String pre = "";
        Pin p;
        Node node;
        if(pinX.isPresent()) {
            p = pinX.get();
            node = p.connectedNode();
            if(node != null && node instanceof GLSLNode) {
                pre += ((GLSLNode) node).getFragment();
                xS = ((GLSLNode) node).getVariable(p.connected);
            }
        }
        if(pinY.isPresent()) {
            p = pinY.get();
            node = p.connectedNode();
            if(node != null && node instanceof GLSLNode) {
                pre += ((GLSLNode) node).getFragment();
                yS = ((GLSLNode) node).getVariable(p.connected);
            }
        }

        return String.format("%s%s = vec2(%s, %s);", pre, getVariable(), xS, yS);
    }

    @Override
    public void resetStaticCache() {
        globalId = 0;
    }

    @Override
    public void resetCache() {
        id = globalId++;
        fragmentUsed = false;
    }

    @GLSLFactory(displayName = "Make Vector 2", category = "GLSL/data")
    public static class MakeVec2Factory extends GLSLNodeFactory {

        public MakeVec2Factory() {
            super(MakeVec2Factory.class);
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new MakeVec2Node(graph);
        }
    }


}