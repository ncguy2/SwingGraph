package net.ncguy.graph.event;

/**
 * Created by Guy on 14/01/2017.
 */
public class OpenContextMenuEvent extends AbstractEvent {

    public float x;
    public float y;

    public OpenContextMenuEvent() {
    }

    public OpenContextMenuEvent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public static interface OpenContextMenuListener {
        @Subscribe
        void onOpenContextMenu(OpenContextMenuEvent event);
    }

}