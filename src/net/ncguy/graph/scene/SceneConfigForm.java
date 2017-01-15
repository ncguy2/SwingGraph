package net.ncguy.graph.scene;

import net.ncguy.graph.scene.logic.render.SceneGraphRenderer;

import javax.swing.*;

/**
 * Created by Guy on 15/01/2017.
 */
public class SceneConfigForm extends JFrame {
    private JComboBox<String> normalQuality;
    private JComboBox<String> animatingQuality;
    private JComboBox<String> interactingQuality;
    private JPanel rootPanel;

    private String[] qualities = new String[] {
            "Low",
            "High"
    };
    private SceneGraphRenderer renderer;

    public SceneConfigForm(SceneGraphRenderer renderer) {
        this.renderer = renderer;
        setTitle("Graph Configuration");
        setSize(300, 200);
        setVisible(false);

        getContentPane().add(rootPanel);


        JComboBox[] boxes = new JComboBox[] {
                normalQuality, animatingQuality, interactingQuality
        };
        for (JComboBox box : boxes)
            for (int i = 0; i < qualities.length; i++)
                box.addItem(qualities[i]);


        normalQuality.setSelectedIndex(renderer.getNormalRenderQuality());
        animatingQuality.setSelectedIndex(renderer.getAnimatingRenderQuality());
        interactingQuality.setSelectedIndex(renderer.getInteractingRenderQuality());


        normalQuality.addItemListener(e -> renderer.setDefaultRenderQuality(normalQuality.getSelectedIndex()));
        animatingQuality.addItemListener(e -> renderer.setAnimatingRenderQuality(animatingQuality.getSelectedIndex()));
        interactingQuality.addItemListener(e -> renderer.setInteractingRenderQuality(interactingQuality.getSelectedIndex()));
    }

}
