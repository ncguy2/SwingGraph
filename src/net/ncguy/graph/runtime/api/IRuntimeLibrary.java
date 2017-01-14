package net.ncguy.graph.runtime.api;

import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.Set;

/**
 * Created by Guy on 14/01/2017.
 */
public interface IRuntimeLibrary {

    Set<NodeFactory> getNodeFactories();

}
