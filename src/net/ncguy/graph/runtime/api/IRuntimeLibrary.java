package net.ncguy.graph.runtime.api;

import com.esotericsoftware.tablelayout.swing.Table;
import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.data.MutablePropertyControlRegistry;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Guy on 14/01/2017.
 */
public interface IRuntimeLibrary {

    default void RegisterControlAdapters() {}
    List<NodeFactory> getNodeFactories();

    default Table getConfigComponent(Node node) {
        final Table t = new Table();
        t.addCell(node.title).expandX().fillX().colspan(2).padBottom(4).row();

        List<MutableProperty> mutableProperties = new ArrayList<>();
        node.GetMutableProperties(mutableProperties);
        mutableProperties.forEach(prop -> {
            Optional<Pin> pin = node.PropertyToPin(prop);
            t.addCell(prop.getName()).expandX().fillX().padLeft(4).padBottom(2);
            Class<?> type = prop.get().getClass();
            JComponent build = MutablePropertyControlRegistry.instance().Build(type, prop);
            t.addCell(build).expandX().fillX().padBottom(2).row();
            if(pin.isPresent() && pin.get().onLeft && pin.get().isConnected()) {
                build.setEnabled(false);
                build.createToolTip().setTipText("Disabled due to existing pin connection");
            }
        });

        return t;
    }

}
