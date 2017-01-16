package net.ncguy.graph.compiler;

import net.ncguy.graph.data.icons.Icons;
import net.ncguy.graph.event.ToastEvent;
import net.ncguy.graph.library.GLSLNode;
import net.ncguy.graph.library.factories.GLSLRootNode;
import net.ncguy.graph.runtime.api.IRuntimeCompiler;
import net.ncguy.graph.scene.logic.SceneGraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Guy on 15/01/2017.
 */
public class GLSLCompiler implements IRuntimeCompiler<String> {

    @Override
    public String compile(SceneGraph graph) {
        List<GLSLRootNode> rootNodes = graph.nodes.stream()
                .filter(node -> node instanceof GLSLRootNode)
                .map(node -> (GLSLRootNode) node)
                .collect(Collectors.toList());

        if(rootNodes.size() <= 0) {
            new ToastEvent("No root node found, compile failed!").setImagePath(Icons.Icon.WARNING_WHITE).fire();
            return "";
        }else if(rootNodes.size() >= 2) {
            new ToastEvent("Multiple root nodes found, compile failed!").setImagePath(Icons.Icon.WARNING_WHITE).fire();
            return "";
        }

        GLSLRootNode rootNode = rootNodes.get(0);

        prepare(graph);

        Map<GLSLNode, String> fragmentMap = new HashMap<>();
        graph.nodes.stream()
                .filter(node -> node instanceof GLSLNode)
                .map(node -> (GLSLNode) node)
                .forEach(node -> fragmentMap.put(node, node.getUniforms()));

        StringBuilder fragmentBuilder = new StringBuilder();
        fragmentBuilder.append("#version 450\n\n");

        /*
        addPin(new Pin(this, "Diffuse", true));
        addPin(new Pin(this, "Normal", true));
        addPin(new Pin(this, "Metallic", true));
        addPin(new Pin(this, "Roughness", true));
        addPin(new Pin(this, "Emissive", true));
        addPin(new Pin(this, "Specular", true));
         */

        fragmentBuilder.append("layout (location = 0) out vec4 texDiffuse;\n");
        fragmentBuilder.append("layout (location = 1) out vec4 texNormal;\n");
        fragmentBuilder.append("layout (location = 2) out vec4 texEmissive;\n");
        fragmentBuilder.append("layout (location = 3) out vec4 texComp;\n\n");

        fragmentBuilder.append("in vec4 Position;\n");
        fragmentBuilder.append("in vec3 Normal;\n");
        fragmentBuilder.append("in vec2 TexCoords;\n\n");

        fragmentMap.values().forEach(e -> append(fragmentBuilder, e, false));

        StringBuilder fragmentShaderBody = new StringBuilder();

        fragmentShaderBody.append("\t// Single-use fragments, typically used for texture sampling\n");
        graph.nodes.stream()
                .filter(node -> node instanceof GLSLNode)
                .map(node -> (GLSLNode)node)
                .filter(GLSLNode::singleUseFragment)
                .forEach(n -> append(fragmentShaderBody, n.getFragment()));

        // Diffuse
        fragmentShaderBody.append("\n");
        GLSLNode diffuseNode = (GLSLNode) rootNode.diffusePin.connectedNode();
        if(diffuseNode != null) {
            append(fragmentShaderBody, diffuseNode.getFragment());
            append(fragmentShaderBody, "texDiffuse = " + diffuseNode.getVariable(rootNode.diffusePin.connected));
        }else{
            append(fragmentShaderBody, "texDiffuse = vec4(1.0);");
        }

        // Normal
        fragmentShaderBody.append("\n");
        GLSLNode normalNode = (GLSLNode) rootNode.normalPin.connectedNode();
        if(normalNode != null) {
            append(fragmentShaderBody, normalNode.getFragment());
            append(fragmentShaderBody, "texNormal = " + normalNode.getVariable(rootNode.normalPin.connected));
        }else{
            append(fragmentShaderBody, "texNormal = vec4(1.0);");
        }

        // Emissive
        fragmentShaderBody.append("\n");
        GLSLNode emissiveNode = (GLSLNode) rootNode.emissivePin.connectedNode();
        if(emissiveNode != null) {
            append(fragmentShaderBody, emissiveNode.getFragment());
            append(fragmentShaderBody, "texEmissive = " + emissiveNode.getVariable(rootNode.emissivePin.connected));
        }else{
            append(fragmentShaderBody, "texEmissive = vec4(0.0);");
        }

        // Metallic
        fragmentShaderBody.append("\n");
        GLSLNode metallicNode = (GLSLNode) rootNode.metallicPin.connectedNode();
        if(metallicNode != null) {
            append(fragmentShaderBody, metallicNode.getFragment());
            append(fragmentShaderBody, "float internalMetallic = " + metallicNode.getVariable(rootNode.metallicPin.connected));
        }else{
            append(fragmentShaderBody, "float internalMetallic = 0.0;");
        }

        // Roughness
        fragmentShaderBody.append("\n");
        GLSLNode roughnessNode = (GLSLNode) rootNode.roughnessPin.connectedNode();
        if(roughnessNode != null) {
            append(fragmentShaderBody, roughnessNode.getFragment());
            append(fragmentShaderBody, "float internalRoughness = " + roughnessNode.getVariable(rootNode.roughnessPin.connected));
        }else{
            append(fragmentShaderBody, "float internalRoughness = 0.0;");
        }

        // Specular
        fragmentShaderBody.append("\n");
        GLSLNode specularNode = (GLSLNode) rootNode.specularPin.connectedNode();
        if(specularNode != null) {
            append(fragmentShaderBody, specularNode.getFragment());
            append(fragmentShaderBody, "float internalSpecular = " + specularNode.getVariable(rootNode.specularPin.connected));
        }else{
            append(fragmentShaderBody, "float internalSpecular = 0.0;");
        }

        append(fragmentShaderBody, "texComp = vec4(internalMetallic, internalRoughness, internalSpecular, 1.0);");

        String shader = fragmentBuilder.toString();

        shader += "void main() { \n";
        shader += fragmentShaderBody.toString();
        shader += "} \n";

        File file = new File("shader.txt");
        Path path = file.toPath();
        try {
            Files.deleteIfExists(path);
            Files.write(file.toPath(), shader.getBytes(), StandardOpenOption.CREATE_NEW, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new ToastEvent("Successfully compiled shader").setImagePath(Icons.Icon.TICK_WHITE).fire();

        return shader;
    }

    public void prepare(SceneGraph graph) {
        List<GLSLNode> staticNodes = graph.nodes.stream()
                .filter(node -> node instanceof GLSLNode)
                .map(node -> (GLSLNode) node)
                .collect(Collectors.toList());
        staticNodes.forEach(GLSLNode::resetStaticCache);
        staticNodes.forEach(GLSLNode::resetCache);
    }

    private void append(StringBuilder sb, String text) {
        append(sb, text, true);
    }
    private void append(StringBuilder sb, String text, boolean indent) {
        if(text.replace(" ", "").length() <= 0) return;
        if(indent) sb.append("\t");
        sb.append(text).append("\n");
    }


}
