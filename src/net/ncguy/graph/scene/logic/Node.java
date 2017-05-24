package net.ncguy.graph.scene.logic;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.render.NodeComponent;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by Guy on 14/01/2017.
 */
public abstract class Node {

    public String uuid;
    public String title;
    public final String baseTitle;
    public Point2D.Float location;
    public SceneGraph sceneGraph;

    protected List<Pin> pinList;
    protected Map<Pin, MutableProperty> propertyMap;

    public transient NodeComponent renderingComponent;

    public Node(SceneGraph graph, String title) {
        this.sceneGraph = graph;
        this.title = title;
        this.baseTitle = title;
        location = new Point2D.Float();
        pinList = new ArrayList<>();
        propertyMap = new HashMap<>();
    }

    public Optional<Pin> PropertyToPin(MutableProperty property) {
        for (Map.Entry<Pin, MutableProperty> entry : propertyMap.entrySet()) {
            if(property.equals(entry.getValue()))
                return Optional.of(entry.getKey());
        }
        return Optional.empty();
    }

    public void SetTitle(String title) {
        this.title = title;
    }

    public String GetTitle() {
        return this.title;
    }

    public String GetBaseTitle() {
        return this.baseTitle;
    }

    public void addPin(Pin pin) {
        addPin(pin, null);
    }

    public void addPin(Pin pin, MutableProperty property) {
        long count = pinList.stream().filter(p -> p.onLeft == pin.onLeft).count();
        pin.index = Math.toIntExact(count);
        pinList.add(pin);
        if(property != null)
            propertyMap.put(pin, property);
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

    public <T> T GetCastValueFromInputPin(Pin pin, T def) {
        Object o = GetValueFromInputPin(pin);
        try{
            T t = (T) o;
            return t;
        }catch (ClassCastException e) {
            e.printStackTrace();
        }
        return def;
    }

    public Object GetValueFromInputPin(Pin pin) {

        if(pin.isConnected())
            return pin.connectedNode().GetValueFromOutputPin(pin.connected);

        if(propertyMap.containsKey(pin))
            return propertyMap.get(pin).get();

        return null;
    }
    public Object GetValueFromOutputPin(Pin pin) {
        if(propertyMap.containsKey(pin)) {
            return propertyMap.get(pin).get();
        }
        return null;
    }

    public Object GetValueFromInputPin(int index, boolean onLeft) {
        Optional<Pin> pinFromIndex = getPinFromIndex(index, onLeft);
        if(pinFromIndex.isPresent()) {
            Pin pin = pinFromIndex.get();
            GetValueFromInputPin(pin);
        }
        return null;
    }

    public abstract IRuntimeCore runtime();

    public List<MutableProperty> GetMutableProperties() {
        List<MutableProperty> properties = new ArrayList<>();
        GetMutableProperties(properties);
        return properties;
    }
    public void GetMutableProperties(List<MutableProperty> list) {}

    public void removePin(Pin pin) {
        // TODO reindex pins on side pin was removed from
        pinList.remove(pin);
        if(propertyMap.containsKey(pin))
            propertyMap.remove(pin);
    }
}
