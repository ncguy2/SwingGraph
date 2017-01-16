package net.ncguy.graph.scene.components;

import net.ncguy.graph.scene.components.graph.GraphConfigurationPanel;
import net.ncguy.graph.scene.components.graph.NodeConfigurationPanel;
import net.ncguy.graph.scene.logic.SceneGraph;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Guy on 16/01/2017.
 */
public class RootConfigurationPanel extends JPanel {

    JSplitPane splitPane;

    GraphConfigurationPanel graphConfig;
    NodeConfigurationPanel nodeConfig;

    public RootConfigurationPanel(SceneGraph graph) {
        super();
        graphConfig = new GraphConfigurationPanel(graph);
        nodeConfig = new NodeConfigurationPanel(graph);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, graphConfig, nodeConfig);

        splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> {
            nodeConfig.setLocation(0, splitPane.getDividerLocation());
            nodeConfig.revalidate();
            nodeConfig.repaint();
        });

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

}
