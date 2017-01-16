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
public class GLSLRootNode extends GLSLNode {

    public Pin diffusePin;
    public Pin normalPin;
    public Pin metallicPin;
    public Pin roughnessPin;
    public Pin emissivePin;
    public Pin specularPin;

    public GLSLRootNode(SceneGraph graph) {
        super(graph, "Shader Attributes");
        addPin(diffusePin = new Pin(this, "Diffuse", true));
        addPin(normalPin = new Pin(this, "Normal", true));
        addPin(metallicPin = new Pin(this, "Metallic", true));
        addPin(roughnessPin = new Pin(this, "Roughness", true));
        addPin(emissivePin = new Pin(this, "Emissive", true));
        addPin(specularPin = new Pin(this, "Specular", true));
    }

    @Override
    public String getUniforms() {
        return "";
    }

    @Override
    public String getVariable(int pinId) {
        return "";
    }

    @Override
    public String getFragment() {
        return "";
    }

    @GLSLFactory(displayName = "Shader Attributes", category = "GLSL")
    public static class GLSLRootFactory extends GLSLNodeFactory {

        public GLSLRootFactory() {
            super(GLSLRootFactory.class);
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new GLSLRootNode(graph);
        }
    }

}
