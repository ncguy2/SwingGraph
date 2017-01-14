package net.ncguy.graph.event;

import net.ncguy.graph.runtime.api.IRuntimeCore;

/**
 * Created by Guy on 14/01/2017.
 */
public class SwitchRuntimeEvent extends AbstractEvent {

    public IRuntimeCore runtime;

    public SwitchRuntimeEvent() {
    }

    public SwitchRuntimeEvent(IRuntimeCore runtime) {
        this.runtime = runtime;
    }

    public IRuntimeCore getRuntime() {
        return runtime;
    }

    public void setRuntime(IRuntimeCore runtime) {
        this.runtime = runtime;
    }

    public static interface SwitchRuntimeListener {
        @Subscribe
        void onSwitchRuntime(SwitchRuntimeEvent event);
    }

}
