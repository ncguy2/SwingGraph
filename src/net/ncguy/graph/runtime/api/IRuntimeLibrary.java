package net.ncguy.graph.runtime.api;

import com.esotericsoftware.tablelayout.swing.Table;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.List;

/**
 * Created by Guy on 14/01/2017.
 */
public interface IRuntimeLibrary {

    List<NodeFactory> getNodeFactories();

    default Table getConfigComponent(Node node) {
        return null;
    }

}
