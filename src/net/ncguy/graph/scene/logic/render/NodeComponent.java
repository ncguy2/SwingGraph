package net.ncguy.graph.scene.logic.render;

import net.ncguy.graph.scene.components.DraggableTable;
import net.ncguy.graph.scene.logic.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Created by Guy on 24/10/2016.
 */
public class NodeComponent extends DraggableTable {

    public Node node;
    public String title;
    Color defaultBG;
    java.util.List<PinComponent> pinComponentList;

    PinComponent left;
    PinComponent right;

    public Supplier<Integer> getXOverride;
    public Supplier<Integer> getYOverride;

    public NodeComponent(Node node) {
        this.node = node;
        this.title = node.title;
        pinComponentList = new ArrayList<>();
        pinComponentList.add(left = new PinComponent(this));
        pinComponentList.add(right = new PinComponent(this));
        init();
    }

    public void init() {
        setSize(400, 400);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                for (PinComponent p : pinComponentList) {
                    Rectangle rect = p.getBounds();
                    Point point = new Point(e.getX() - getX(), e.getY() - getY());
                    boolean overlap = rect.contains(point);
                    if(overlap)
                        p.dispatchEvent(e);
                }
            }
        });


        root.addCell(title).colspan(3).left().padRight(5).padBottom(4).row();
        root.addCell(left).left().padBottom(4);
        root.addCell("").padBottom(4);
        root.addCell(right).right().padBottom(4).row();
        defaultBG = getBackground();
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
}
