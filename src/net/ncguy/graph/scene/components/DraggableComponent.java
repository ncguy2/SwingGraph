package net.ncguy.graph.scene.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by Guy on 24/10/2016.
 */
public class DraggableComponent extends JPanel {

    private boolean draggable = true;
    protected Point anchorPoint;
    protected Cursor draggingCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    protected boolean overbearing = false;

    public DraggableComponent() {
        addDragListeners();
        setOpaque(true);
        setBackground(new Color(240, 240, 240));
    }

    private void addDragListeners() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                anchorPoint = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                e.consume();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point parentOnScreen = getParent().getLocationOnScreen();
                Point mouseOnScreen = e.getLocationOnScreen();

                Point parentPos = getParent().getLocation();
                Point mousePos = e.getPoint();

                Point position = new Point(mouseOnScreen.x - parentOnScreen.x - anchorPoint.x, mouseOnScreen.y - parentOnScreen.y - anchorPoint.y);
//                Point position = new Point(mousePos.x - parentPos.x - anchorPoint.x, mousePos.y - parentPos.y - anchorPoint.y);
                setLocation(position);

//                if(overbearing) {
//                    getParent().setComponentZOrder(DraggableComponent.this, 0);
//                    repaint();
//                }

                e.consume();
            }
        });
    }

    private void removeDragListeners() {
        for (MouseMotionListener listener : this.getMouseMotionListeners())
            removeMouseMotionListener(listener);
        setCursor(Cursor.getDefaultCursor());
    }


    public boolean isDraggable() { return draggable; }
    public void setDraggable(boolean draggable) { this.draggable = draggable; }

    public Cursor getDraggingCursor() { return draggingCursor; }
    public void setDraggingCursor(Cursor draggingCursor) { this.draggingCursor = draggingCursor; }

    public boolean isOverbearing() { return overbearing; }
    public void setOverbearing(boolean overbearing) { this.overbearing = overbearing; }

}
