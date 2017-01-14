package net.ncguy.graph.event;

/**
 * Created by Guy on 14/01/2017.
 */
public abstract class AbstractEvent {

    public void fire() {
        EventBus.instance().post(this);
    }

}
