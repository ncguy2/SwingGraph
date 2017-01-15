package net.ncguy.graph.scene.logic;

import net.ncguy.graph.event.AbstractEvent;
import net.ncguy.graph.event.Subscribe;

/**
 * Created by Guy on 15/01/2017.
 */
public class PinConnectEvent extends AbstractEvent {

    Pin a;
    Pin b;

    public PinConnectEvent() {
    }

    public PinConnectEvent(Pin a, Pin b) {
        this.a = a;
        this.b = b;
    }

    public Pin getA() {
        return a;
    }

    public void setA(Pin a) {
        this.a = a;
    }

    public Pin getB() {
        return b;
    }

    public void setB(Pin b) {
        this.b = b;
    }

    public static interface PinConnectListener {
        @Subscribe
        void onPinConnect(PinConnectEvent event);
    }

}

