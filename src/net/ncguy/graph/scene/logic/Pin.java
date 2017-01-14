package net.ncguy.graph.scene.logic;

/**
 * Created by Guy on 14/01/2017.
 */
public class Pin {

    public Node owningNode;
    public boolean onLeft;

    public Pin(Node owningNode, boolean onLeft) {
        this.owningNode = owningNode;
        this.onLeft = onLeft;
    }

    public void clear() {

    }

    public boolean canConnect(Pin other) {
        if(this.owningNode.equals(other.owningNode))
            return false;
        if(this.onLeft == other.onLeft)
            return false;

        return true;
    }

    public void tryConnect(Pin other) {
        // TODO perform validity checks
        if(canConnect(other))
            connect(other);
    }
    public void connect(Pin other) {
        // TODO connect pins together and propagate to PinComponent
    }

}
