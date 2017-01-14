package net.ncguy.graph.scene.logic;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Guy on 14/01/2017.
 */
public class SceneGraph {

    public Set<Node> nodes;

    public Set<Consumer<Node>> onAddNode;
    public Set<Consumer<Node>> onRemoveNode;

    public SceneGraph() {
        nodes = new LinkedHashSet<>();
        onAddNode = new LinkedHashSet<>();
        onRemoveNode = new LinkedHashSet<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
        fireOnAdd(node);
    }

    public void removeNode(Node node) {
        nodes.remove(node.clear());
        fireOnRemove(node);
    }

    protected void fireOnAdd(Node node) {
        fireEvent(onAddNode, node);
    }
    protected void fireOnRemove(Node node) {
        fireEvent(onRemoveNode, node);
    }

    protected void fireEvent(Set<Consumer<Node>> set, Node node) {
        set.forEach(c -> c.accept(node));
    }

    public void addOnAddListener(Consumer<Node> onAdd) {
        onAddNode.add(onAdd);
    }
    public void addOnRemoveListener(Consumer<Node> onRemove) {
        onRemoveNode.add(onRemove);
    }

}
