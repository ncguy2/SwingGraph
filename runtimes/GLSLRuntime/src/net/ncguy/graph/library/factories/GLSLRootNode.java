package net.ncguy.graph.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.library.GLSLFactory;
import net.ncguy.graph.library.GLSLNode;
import net.ncguy.graph.library.GLSLNodeFactory;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;

import java.util.List;

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

    public MutableProperty<GLSLVersions> version;

    public GLSLRootNode(SceneGraph graph) {
        super(graph, "Shader Attributes");
        addPin(diffusePin = new Pin(this, "Diffuse", true));
        addPin(normalPin = new Pin(this, "Normal", true));
        addPin(metallicPin = new Pin(this, "Metallic", true));
        addPin(roughnessPin = new Pin(this, "Roughness", true));
        addPin(emissivePin = new Pin(this, "Emissive", true));
        addPin(specularPin = new Pin(this, "Specular", true));
        version = new MutableProperty<>("GLSL Version", GLSLVersions.VER_330_CORE);
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

    @Override
    public void GetMutableProperties(List<MutableProperty> list) {
        super.GetMutableProperties(list);
        list.add(version);
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

    public enum GLSLVersions {
        VER_120("120"),
        VER_330("330"),
        VER_330_CORE("330 core"),
        VER_410("410"),
        VER_410_CORE("410 core"),
        VER_420("420"),
        VER_420_CORE("420 core"),
        VER_430("430"),
        VER_430_CORE("430 core"),
        VER_440("440"),
        VER_440_CORE("440 core"),
        VER_450("450"),
        VER_450_CORE("450 core"),
        ;

        GLSLVersions(String version) {
            this.version = version;
        }

        public final String version;

        @Override
        public String toString() {
            return version;
        }
    }

}
