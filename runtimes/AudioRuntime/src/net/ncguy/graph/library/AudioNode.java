package net.ncguy.graph.library;

import net.ncguy.graph.AudioRuntime;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.SceneGraph;

public abstract class AudioNode extends Node {

    public AudioNode(SceneGraph graph, String title) {
        super(graph, title);
    }

    @Override
    public IRuntimeCore runtime() {
        return AudioRuntime.newestInstance;
    }

    public abstract void Invoke();

}
