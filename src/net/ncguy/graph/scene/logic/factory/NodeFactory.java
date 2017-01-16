package net.ncguy.graph.scene.logic.factory;

import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.SceneGraph;

/**
 * Created by Guy on 14/01/2017.
 */
public abstract class NodeFactory {

    public String title;
    public String category;

    public NodeFactory(String title, String category) {
        this.title = title;
        this.category = category;
    }

    public abstract Node buildNode(SceneGraph graph);

    @Override
    public String toString() {
        return title;
    }
}
