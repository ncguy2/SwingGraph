package net.ncguy.graph.library.factories;

import net.ncguy.graph.library.GLSLFactory;
import net.ncguy.graph.library.GLSLNode;
import net.ncguy.graph.library.GLSLNodeFactory;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;

/**
 * Created by Guy on 15/01/2017.
 */
public class TextureCoordsNode extends GLSLNode {

    public TextureCoordsNode(SceneGraph graph) {
        super(graph, "TexCoords");
        addPin(new Pin(this, "UV", false));  // UV
    }


    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        return "TexCoords";
    }

    @Override
    public String getFragment() {
        return "";
    }

    @GLSLFactory(displayName = "Texture Coordinates", category = "GLSL/data")
    public static class TextureCoordsFactory extends GLSLNodeFactory {

        public TextureCoordsFactory() {
            super(TextureCoordsFactory.class);
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new TextureCoordsNode(graph);
        }
    }


}