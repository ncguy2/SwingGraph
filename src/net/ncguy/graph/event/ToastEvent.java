package net.ncguy.graph.event;

/**
 * Created by Guy on 15/01/2017.
 */
public class ToastEvent extends AbstractEvent {

    public String message;
    public String imagePath;
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

    public ToastEvent setImagePath(String path) {
        this.imagePath = path;
        return this;
    }

    public static interface ToastListener {
         @Subscribe
         void onToast(ToastEvent event);
     }

 }
