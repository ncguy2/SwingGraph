package net.ncguy.graph.scene.logic;

import net.ncguy.graph.event.AbstractEvent;
import net.ncguy.graph.event.Subscribe;
import net.ncguy.graph.scene.logic.render.PinComponent;

/**
 * Created by Guy on 15/01/2017.
 */
public class PinHoverEvent extends AbstractEvent {

    public PinComponent component;
    public boolean hovering;

    public PinHoverEvent() {
    }

    public PinHoverEvent(PinComponent component, boolean hovering) {
        this.component = component;
        this.hovering = hovering;
    }

    public PinComponent getComponent() {
        return component;
    }

    public void setComponent(PinComponent component) {
        this.component = component;
    }

    public boolean isHovering() {
        return hovering;
    }

    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }

    public static interface PinHoverListener {
        @Subscribe
        void onPinHover(PinHoverEvent event);
    }

}
