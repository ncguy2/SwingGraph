package net.ncguy.graph.scene.components;

import com.esotericsoftware.tablelayout.swing.Table;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by Guy on 13/01/2017.
 */
public class DraggableTable extends JPanel {

    public Table root;

    public DraggableTable() {
        root = new Table();
        add(root);
        root.setBounds(0, 0, getWidth(), getHeight());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                root.setBounds(0, 0, getWidth(), getHeight());
            }
        });
    }
}
