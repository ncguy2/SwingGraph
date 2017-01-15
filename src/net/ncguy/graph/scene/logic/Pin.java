package net.ncguy.graph.scene.logic;

import net.ncguy.graph.event.ToastEvent;

import java.awt.*;

/**
 * Created by Guy on 14/01/2017.
 */
public class Pin {

    public Node owningNode;
    public boolean onLeft;
    public Pin connected;
    public Color wireColour;
    public String label;

    public Pin(Node owningNode, String label, boolean onLeft) {
        this.owningNode = owningNode;
        this.label = label;
        this.onLeft = onLeft;
        wireColour = Color.BLACK;
    }

    public void clear() {

    }

    public boolean canConnect(Pin other) {
        if(this.owningNode.equals(other.owningNode)) {
            new ToastEvent("Pins share the same parent").fire();
            return false;
        }
        if(this.onLeft == other.onLeft) {
            new ToastEvent("Pins are of opposing directions").fire();
            return false;
        }

        return true;
    }

    public void tryConnect(Pin other) {
        // TODO perform validity checks
        if(canConnect(other))
            connectTo(other, true);
    }

    public void connectTo(Pin other, boolean isInstigator) {
        if(connected == other) return;
        if(connected != null)
            disconnectFrom(connected);
        connected = other;
        if(isInstigator) {
            other.connectTo(this, false);
            new PinConnectEvent(this, other).fire();
        }
    }

    public void disconnectFrom(Pin other) {
        if(other == this) return;
        other.connected = null;
        connected = null;
        new PinDisconnectEvent(this, other).fire();
    }

}
