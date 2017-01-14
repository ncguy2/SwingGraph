package net.ncguy.graph.scene.logic.render;

import net.ncguy.graph.scene.logic.Pin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Guy on 24/10/2016.
 */
public class PinComponent extends JPanel {

    public NodeComponent parentNodeComponent;
    public Pin pin;

    public PinComponent(NodeComponent parentNodeComponent, Pin pin) {
        this.parentNodeComponent = parentNodeComponent;
        this.pin = pin;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                // TODO initiate drag
                e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                // TODO resolve drop
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.drawOval(1, 1, getWidth()-2, getHeight()-2);
    }

}
