package net.ncguy.graph.scene.logic.render;

import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.PinDropEvent;
import net.ncguy.graph.scene.logic.PinHoverEvent;
import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Guy on 24/10/2016.
 */
public class PinComponent extends JPanel implements PinDropEvent.PinDropListener, PinHoverEvent.PinHoverListener {

    public NodeComponent parentNodeComponent;
    public Pin pin;

    private boolean beginDragged = false;
    public boolean isHovered = false;

    public PinComponent(NodeComponent parentNodeComponent, Pin pin) {
        this.parentNodeComponent = parentNodeComponent;
        this.pin = pin;

        SceneGraphForm.instance.continuousRenderables.add(this);

        EventBus.instance().register(this);

        addMouseListener(new MouseAdapter() {
            // Source Drag
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                e.consume();

                if(SceneGraphForm.instance.isAltPressed) {
                    if(isConnected()) {
                        pin.disconnectFrom(pin.connected);
                    }
                    return;
                }
                beginDragged = true;
                // TODO initiate drag
            }

            // Source drop
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if(!beginDragged) return;
                new PinDropEvent(PinComponent.this).fire();
                beginDragged = false;
                isHovered = false;
                // TODO resolve drop
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }
        });

    }

    public boolean isConnected() {
        return pin.connected != null;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(parentNodeComponent.getBackground());
        g.drawRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground());
        if(isHovered)
            g.setColor(Color.GREEN);
        g.drawOval(1, 1, getWidth()-2, getHeight()-2);
        if(isConnected()) {
            g.setColor(pin.wireColour);
            int midX = getWidth() / 2;
            int midY = getHeight() / 2;
            g.drawOval(midX-1, midY-1, 2, 2);
        }
        g.setColor(getForeground());
    }

    @Override
    public void onPinDrop(PinDropEvent event) {
        if(beginDragged) return;
        if(!isHovered) return;
        if(event.component == null || event.component == this) return;
        if(event.component.pin == null) return;
        System.out.println("Connection attempt");
        pin.tryConnect(event.component.pin);
    }

    @Override
    public void onPinHover(PinHoverEvent event) {
        if(event.component == null) return;
        setHover(event.isHovering());
    }

    public void setHover(boolean hover) {
        this.isHovered = hover;
    }

    public void forcePaint() {
//        paintImmediately(0, 0, getWidth(), getHeight());
        Graphics g = getGraphics();
        if(g != null) paintComponent(g);
        else repaint();
    }

}
