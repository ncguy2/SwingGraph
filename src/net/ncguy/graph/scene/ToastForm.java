package net.ncguy.graph.scene;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;
import net.ncguy.graph.event.ToastEvent;
import net.ncguy.graph.scene.components.ImagePanel;
import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Created by Guy on 15/01/2017.
 */
public class ToastForm extends JFrame {
    private JLabel text;
    private JPanel rootPanel;
    private ImagePanel imagePanel;

    public ToastForm(ToastEvent e) {
        setUndecorated(true);
        rootPanel.setBorder(BorderFactory.createSoftBevelBorder(0, Color.LIGHT_GRAY, Color.BLACK));

        imagePanel.setImage(e.icon.loadIcon());

        text.setText(e.getMessage());

        getContentPane().add(rootPanel);

        setAlwaysOnTop(true);
        setOpacity(0);
        setVisible(true);

        Graphics g = getGraphics();
        FontMetrics met;
        Font f = text.getFont();
        if(f != null)
            met = g.getFontMetrics(text.getFont());
        else met = g.getFontMetrics();

        int width = met.stringWidth(e.getMessage());
        int height = met.getHeight() + 10;
        text.setPreferredSize(new Dimension(width, Math.max(64, height)));
        pack();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - (getWidth() + 10);
        int y = (int) rect.getMaxY() - (getHeight() + 10);
        setLocation(x, y);



        Timeline.createSequence()
                .push(Tween.set(this, 0).target(0))
                .push(Tween.to(this, 0, .5f).target(1))
                .pushPause(e.seconds)
                .push(Tween.to(this, 0, .5f).target(0).setCallback((i, baseTween) -> {
                    if(i == TweenCallback.COMPLETE)
                        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                }))
                .start(SceneGraphForm.instance.tweenManager);

    }

    public static class ToastTweenAccessor implements TweenAccessor<ToastForm> {

        @Override
        public int getValues(ToastForm toastForm, int i, float[] floats) {
            floats[0] = toastForm.getOpacity();
            return 1;
        }

        @Override
        public void setValues(ToastForm toastForm, int i, float[] floats) {
            toastForm.setOpacity(floats[0]);
        }
    }

}
