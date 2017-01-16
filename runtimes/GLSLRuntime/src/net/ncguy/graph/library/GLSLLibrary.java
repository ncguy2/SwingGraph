package net.ncguy.graph.library;

import com.esotericsoftware.tablelayout.swing.Table;
import net.ncguy.graph.library.factories.GLSLRootNode;
import net.ncguy.graph.library.factories.MakeVec2Node;
import net.ncguy.graph.library.factories.TextureCoordsNode;
import net.ncguy.graph.library.factories.TextureSampleNode;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 14/01/2017.
 */
public class GLSLLibrary implements IRuntimeLibrary {

    @Override
    public List<NodeFactory> getNodeFactories() {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new TextureSampleNode.TextureSampleFactory());
        factories.add(new TextureCoordsNode.TextureCoordsFactory());
        factories.add(new MakeVec2Node.MakeVec2Factory());
        factories.add(new GLSLRootNode.GLSLRootFactory());
        return factories;
    }

    @Override
    public Table getConfigComponent(Node node) {
        Table t = new Table();
        t.addCell(node.title).expandX().fillX().row();
        t.addCell("").expand().fill().row();
        return t;
    }
}
