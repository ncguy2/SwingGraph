package net.ncguy.graph.runtime;

import net.ncguy.graph.runtime.api.IRuntimeCore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guy on 14/01/2017.
 */
public class RuntimeReserve {

    public final Map<String, IRuntimeCore> runtimeMap;

    private static RuntimeReserve instance;
    public static RuntimeReserve instance() {
        if (instance == null)
            instance = new RuntimeReserve();
        return instance;
    }

    private RuntimeReserve() {
        this.runtimeMap = new HashMap<>();
    }

    public void addRuntime(IRuntimeCore runtimeCore) {
        addRuntime(runtimeCore.name(), runtimeCore);
    }

    public void addRuntime(String name, IRuntimeCore runtime) {
        runtimeMap.put(name, runtime);
    }

    public void populate() {
        // Load classes into classloader
        RuntimeCartographer.getInstance().executeProcess();
        // Load library cores into reserve
        RuntimeLoader.instance().loadRuntimes();
    }

}