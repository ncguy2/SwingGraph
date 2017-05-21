package net.ncguy.graph.data;

import net.ncguy.graph.data.factories.ExecForNode;
import net.ncguy.graph.data.factories.StartExecNode;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultNodeFactories {

    private static DefaultNodeFactories instance;
    public static DefaultNodeFactories instance() {
        if (instance == null)
            instance = new DefaultNodeFactories();
        return instance;
    }

    private DefaultNodeFactories() {}

    public List<NodeFactory> GetFactories() {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new StartExecNode.StartExecFactory());
        factories.add(new ExecForNode.ExecForFactory());
        return factories;
    }


}
