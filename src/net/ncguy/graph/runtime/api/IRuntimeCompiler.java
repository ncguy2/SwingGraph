package net.ncguy.graph.runtime.api;

import net.ncguy.graph.scene.logic.SceneGraph;

import javax.swing.*;

/**
 * Created by Guy on 14/01/2017.
 */
public interface IRuntimeCompiler<T> {

    T compile(SceneGraph graph);
    default JComponent details() {
        return new JLabel("No details provided");
    }

}
