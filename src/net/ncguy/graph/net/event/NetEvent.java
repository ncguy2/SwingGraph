package net.ncguy.graph.net.event;

import net.ncguy.graph.event.AbstractEvent;
import net.ncguy.graph.net.NetworkManager;

/**
 * Created by Guy on 18/01/2017.
 */
public abstract class NetEvent extends AbstractEvent {

    public NetOrigin origin;

    public NetEvent(NetOrigin origin) {
        this.origin = origin;
    }

    /**
     * Reliable events use the tcp connection, while unreliable events use udp
     * @return
     */
    public boolean reliable() {
        return false;
    }

    @Override
    public void fire() {
        NetworkManager.instance().handleNetEvent(this);
    }
}
