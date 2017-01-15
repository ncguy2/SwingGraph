package net.ncguy.graph.scene.logic.render;

import com.esotericsoftware.tablelayout.swing.Table;
import net.ncguy.graph.scene.components.DraggableTable;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Created by Guy on 24/10/2016.
 */
public class NodeComponent extends DraggableTable {

    public Node node;
    public String title;
    public Color defaultBG;
    java.util.List<PinComponent> pinComponentList;

    Table left;
    Table right;

    public Supplier<Integer> getXOverride;
    public Supplier<Integer> getYOverride;

    public Runnable onRepaintRequest;

    public NodeComponent(Node node) {
        super();
        this.node = node;
        this.title = node.title;
        pinComponentList = new ArrayList<>();
        defaultBG = getBackground();
        init();
    }

    public PinComponent getComponentFromPin(Pin pin) {
        for (PinComponent pinComponent : pinComponentList) {
            if(pin.equals(pinComponent.pin))
                return pinComponent;
        }
        return null;
    }

    public Point2D.Float getPercentagePositionOfPin(Pin pin) {
        return getPercentagePositionOfPin(pin, .5f, .5f);
    }

    public Point2D.Float getPercentagePositionOfPin(Pin pin, float xWeight, float yWeight) {
        PinComponent c = getComponentFromPin(pin);
        if(c == null) return null;
        Point2D.Float p = new Point2D.Float();

        int localX = (int) (c.getWidth()  * xWeight);
        int localY = (int) (c.getHeight() * yWeight);
        Point abs = SwingUtilities.convertPoint(c, localX, localY, this);

        p.x = (float)abs.x / (float)getWidth();
        p.y = (float)abs.y / (float)getHeight();

        return p;
    }

    public void init() {

        left = new Table();
        right = new Table();

        addPins();

        setSize(400, 400);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                mouseMoved(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                boolean overlap;
                Rectangle rect;
                Point point;
                for (PinComponent p : pinComponentList) {

                    rect = p.getBounds();
                    rect.setLocation(SwingUtilities.convertPoint(p, 0, 0, NodeComponent.this));
                    point = new Point(e.getX() - getX(), e.getY() - getY());
//                    System.out.printf("Rect: [%s, %s, %s, %s], Point: [%s, %s], Hovering: [%s]\n",
//                            rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(),
//                            point.getX(), point.getY(),
//                            rect.contains(point));
                    overlap = rect.contains(point);
                    if(overlap != p.isHovered) {
                        p.setHover(overlap);
                        requestRepaint();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                for(PinComponent p : pinComponentList)
                    p.dispatchEvent(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                propagateEvent(e);
            }
        });


        root.addCell(title).colspan(3).padRight(10).padBottom(4).row();
        root.addCell(left).left().padBottom(4);
        root.addCell("").padBottom(4);
        root.addCell(right).right().padBottom(4).row();
        defaultBG = getBackground();
    }

    private void propagateEvent(MouseEvent e) {
        propagateEvent(e, false);
    }
    private void propagateEvent(MouseEvent e, boolean invert) {
        boolean overlap;
        Rectangle rect;
        Point point;
        for (PinComponent p : pinComponentList) {

            rect = p.getBounds();
            rect.setLocation(SwingUtilities.convertPoint(p, 0, 0, NodeComponent.this));
            point = new Point(e.getX() - getX(), e.getY() - getY());
            overlap = rect.contains(point);

            if(invert)
                overlap = !overlap;

            if(overlap)
                p.dispatchEvent(e);
        }
    }

    public void addPins() {
        node.pinList.forEach(pin -> {
            Table t;
            if(pin.onLeft) t = left;
            else t = right;

            PinComponent comp = new PinComponent(this, pin);

            if(pin.onLeft) {
                t.addCell(comp);
                t.addCell(pin.label);
            }else{
                t.addCell(pin.label);
                t.addCell(comp);
            }
            t.row();

            pinComponentList.add(comp);
        });
    }

    @Override
    public int getX() {
        if(getXOverride != null)
            return getXOverride.get();
        return super.getX();
    }

    @Override
    public int getY() {
        if(getYOverride != null)
            return getYOverride.get();
        return super.getY();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public static NodeWrapper create(Node node) {
        return new NodeWrapper(new NodeComponent(node));
    }

    public void resetBackground() {
        setBackground(defaultBG);
    }

    public void requestRepaint() {
        if(onRepaintRequest == null) return;
        onRepaintRequest.run();
    }

}
