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
public class TextureSampleNode extends GLSLNode {

    public TextureSampleNode(SceneGraph graph) {
        super(graph, "Texture Sample");
        addPin(new Pin(this, "UV", true));  // UV

        addPin(new Pin(this, "Colour", false)); // Full Colour
        addPin(new Pin(this, "R", false)); // R
        addPin(new Pin(this, "G", false)); // G
        addPin(new Pin(this, "B", false)); // B
        addPin(new Pin(this, "A", false)); // A
    }

    static int globalTexUnit = 1;

    int texUnit = 0;
    boolean fragmentUsed = false;

    @Override
    public void resetStaticCache() {
        globalTexUnit = 1;
    }

    @Override
    public void resetCache() {
        texUnit = globalTexUnit++;
        fragmentUsed = false;
    }

    @Override
    public String getUniforms() {
        return "uniform sampler2D u_texture"+texUnit+";\n" +
                "vec4 texture"+texUnit+"_colour;\n" +
                "vec2 texture"+texUnit+"_uv;\n";
    }

    @Override
    public String getVariable(int pinId) {
        String var = "";
        switch(pinId) {
            case 1: var = ".r"; break;
            case 2: var = ".g"; break;
            case 3: var = ".b"; break;
            case 4: var = ".a"; break;
        }
        return "texture"+texUnit+"_colour"+var;
    }

    @Override
    public String getFragment() {
        if(fragmentUsed)
            return "";
        fragmentUsed = true;
        Optional<Pin> pin = getPinFromIndex(0, true);
        String pre = "";
        String var = "TexCoords";
        if(pin.isPresent()) {
            Node node = pin.get().connectedNode();
            if(node != null && node instanceof GLSLNode) {
                pre = ((GLSLNode) node).getFragment();
                var = ((GLSLNode) node).getVariable(pin.get().connected);
            }
        }
        return String.format("%stexture%s_colour = texture(u_texture%s, %s);", pre, texUnit, texUnit, var);
    }

    @Override
    public boolean singleUseFragment() {
        return true;
    }

    @GLSLFactory(displayName = "Texture Sampler", category = "GLSL/texture")
    public static class TextureSampleFactory extends GLSLNodeFactory {

        public TextureSampleFactory() {
            super(TextureSampleFactory.class);
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new TextureSampleNode(graph);
        }
    }

}
