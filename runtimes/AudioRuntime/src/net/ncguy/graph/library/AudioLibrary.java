package net.ncguy.graph.library;

import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.ArrayList;
import java.util.List;

public class AudioLibrary implements IRuntimeLibrary {

    @Override
    public List<NodeFactory> getNodeFactories() {
        List<NodeFactory> factories = new ArrayList<>();
        return factories;
    }
}
