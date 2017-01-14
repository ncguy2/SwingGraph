package net.ncguy.graph.scene.logic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 14/01/2017.
 */
public class Node {

    public String title;
    public Point2D.Float location;

    public List<Pin> pinList;

    public Node(String title) {
        this.title = title;
        location = new Point2D.Float();
        pinList = new ArrayList<>();
    }

    public Node clear() {
        pinList.forEach(Pin::clear);
        return this;
    }
}
