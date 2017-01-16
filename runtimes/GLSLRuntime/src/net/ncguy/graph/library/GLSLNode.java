package net.ncguy.graph.library;

import net.ncguy.graph.GLSLRuntime;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;

/**
 * Created by Guy on 15/01/2017.
 */
public abstract class GLSLNode extends Node {

    public GLSLNode(SceneGraph graph, String title) {
        super(graph, title);
    }

    public String getVariable(Pin pin) {
        return getVariable(pin.index);
    }

    public String getVariable() {
        return getVariable(0);
    }

    public abstract String getUniforms();
    public abstract String getVariable(int pinId);
    public abstract String getFragment();

    public boolean singleUseFragment() {
        return false;
    }

    public void resetStaticCache() {}
    public void resetCache() {}

    @Override
    public IRuntimeCore runtime() {
        return GLSLRuntime.newestInstance;
    }
}
