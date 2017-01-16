package net.ncguy.graph.event;

import net.ncguy.graph.data.icons.Icons;

/**
 * Created by Guy on 15/01/2017.
 */
public class ToastEvent extends AbstractEvent {

    public String message;
    public Icons.Icon icon;
    public float seconds;

    public ToastEvent() {
    }

    public ToastEvent(String message) {
        this(message, 5);
    }

    public ToastEvent(String message, float seconds) {
        this.message = message;
        this.seconds = seconds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ToastEvent setImagePath(Icons.Icon icon) {
        this.icon = icon;
        return this;
    }

    public static interface ToastListener {
         @Subscribe
         void onToast(ToastEvent event);
     }

 }
