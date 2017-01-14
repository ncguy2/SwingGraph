package net.ncguy.graph.runtime;

import net.ncguy.graph.event.AbstractEvent;
import net.ncguy.graph.event.Subscribe;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;

/**
 * Created by Guy on 14/01/2017.
 */
public class LibraryStateChangeEvent extends AbstractEvent {

    IRuntimeLibrary library;
    LibraryState state;

    public LibraryStateChangeEvent() {
    }

    public LibraryStateChangeEvent(IRuntimeLibrary library, LibraryState state) {
        this.library = library;
        this.state = state;
    }

    public IRuntimeLibrary getLibrary() {
        return library;
    }

    public void setLibrary(IRuntimeLibrary library) {
        this.library = library;
    }

    public LibraryState getState() {
        return state;
    }

    public void setState(LibraryState state) {
        this.state = state;
    }

    public static interface LibraryStateChangeListener {
        @Subscribe
        void onLibraryStateChange(LibraryStateChangeEvent event);
    }

    public static enum LibraryState {
        ENABLED,
        DISABLED,
        ;
    }

}
