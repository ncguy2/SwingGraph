package net.ncguy.graph.scene.logic;

import net.ncguy.graph.runtime.api.IRuntimeCore;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Guy on 14/01/2017.
 */
public abstract class Node {

    public String title;
    public Point2D.Float location;
    public SceneGraph sceneGraph;

    protected List<Pin> pinList;

    public Node(SceneGraph graph, String title) {
        this.sceneGraph = graph;
        this.title = title;
        location = new Point2D.Float();
        pinList = new ArrayList<>();
    }

    public void addPin(Pin pin) {
        long count = pinList.stream().filter(p -> p.onLeft == pin.onLeft).count();
        pin.index = Math.toIntExact(count);
        pinList.add(pin);
    }

    public void remove() {
        clear();
        sceneGraph.removeNode(this);
    }

    public Optional<Pin> getPinFromIndex(int index, boolean onLeft) {
        return pinList.stream()
                .filter(p -> p.index == index)
                .filter(p -> p.onLeft == onLeft)
                .findFirst();
    }

    public List<Pin> getPinList() {
        return pinList;
    }

    public Node clear() {
        pinList.forEach(Pin::clear);
        return this;
    }

    public abstract IRuntimeCore runtime();

}
