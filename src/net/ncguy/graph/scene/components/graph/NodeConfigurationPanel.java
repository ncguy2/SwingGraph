package net.ncguy.graph.scene.components.graph;

import com.esotericsoftware.tablelayout.swing.Table;
import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.scene.events.NodeSelectedEvent;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.SceneGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by Guy on 16/01/2017.
 */
public class NodeConfigurationPanel extends JPanel implements NodeSelectedEvent.NodeSelectedListener {

    private SceneGraph graph;
    private JScrollPane pane;
    private Table panel;

    public NodeConfigurationPanel(SceneGraph graph) {
        this.graph = graph;
        EventBus.instance().register(this);

        setLayout(new BorderLayout());
        panel = new Table();
        pane = new JScrollPane(panel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                pane.setBounds(0, 0, getWidth(), getHeight());
                assertTable();
            }
        });
        add(pane, BorderLayout.CENTER);


    }

    @Override
    public void onNodeSelected(NodeSelectedEvent event) {
        panel.clear();
        Node node = event.node;
        IRuntimeCore runtime = node.runtime();
        if(runtime == null) return;
        IRuntimeLibrary library = runtime.library();
        if(library == null) return;
        Table component = library.getConfigComponent(node);

        if(component != null)
            panel.addCell(component).expand().fill().row();

        assertTable();

        panel.revalidate();
        panel.repaint();
//        revalidate();
//        repaint();
    }

    public void assertTable() {
        panel.setBounds(0, 0, pane.getWidth(), pane.getHeight());
        panel.setBackground(Color.RED);
    }

}
