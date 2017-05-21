package net.ncguy.graph;

import aurelienribon.tweenengine.Tween;
import com.bulenkov.darcula.DarculaLaf;
import net.ncguy.graph.data.MutablePropertyControlRegistry;
import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Tween.setCombinedAttributesLimit(4);
        MutablePropertyControlRegistry.instance().Defaults();

        try {
            UIManager.setLookAndFeel(DarculaLaf.class.getCanonicalName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(SceneGraphForm::new);
    }
}
