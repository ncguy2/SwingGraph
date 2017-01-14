package net.ncguy.graph;

import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SceneGraphForm::new);
    }
}
