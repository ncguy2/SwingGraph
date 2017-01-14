package net.ncguy.graph.scene.logic.render;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Guy on 24/10/2016.
 */
public class PinComponent extends JPanel {

    private NodeComponent parentNodeComponent;

    public PinComponent(NodeComponent parentNodeComponent) {
        this.parentNodeComponent = parentNodeComponent;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                System.out.println("Pin clicked");
                e.consume();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.drawOval(1, 1, getWidth()-2, getHeight()-2);
    }
}
