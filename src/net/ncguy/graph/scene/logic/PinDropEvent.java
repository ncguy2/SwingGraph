package net.ncguy.graph.scene.logic;

import net.ncguy.graph.event.AbstractEvent;
import net.ncguy.graph.event.Subscribe;
import net.ncguy.graph.scene.logic.render.PinComponent;

/**
 * Created by Guy on 15/01/2017.
 */
public class PinDropEvent extends AbstractEvent {

    public PinComponent component;

    public PinDropEvent() {
    }

    public PinDropEvent(PinComponent component) {
        this.component = component;
    }

    public PinComponent getComponent() {
        return component;
    }

    public void setComponent(PinComponent component) {
        this.component = component;
    }

    public static interface PinDropListener {
        @Subscribe
        void onPinDrop(PinDropEvent event);
    }

}
