package net.ncguy.graph.tween;

import aurelienribon.tweenengine.TweenAccessor;
import net.ncguy.graph.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class JComponentTweenAccessor implements TweenAccessor<JComponent> {


    public static final int COLOUR = 1;
    public static final int ALPHA = 2;

    public static final int FULL = COLOUR | ALPHA;

    @Override
    public int getValues(JComponent jComponent, int i, float[] floats) {
        int index = 0;
        Color col = jComponent.getBackground();
        if(SwingUtils.isMasked(i, COLOUR)) {
            floats[index++] = col.getRed();
            floats[index++] = col.getGreen();
            floats[index++] = col.getBlue();
        }
        if(SwingUtils.isMasked(i, ALPHA)) {
            floats[index++] = col.getAlpha();
        }
        return index;
    }

    @Override
    public void setValues(JComponent jComponent, int i, float[] floats) {
        int index = 0;
        float r, g, b, a;
        if(SwingUtils.isMasked(i, COLOUR)) {
            r = floats[index++] / 255.f;
            g = floats[index++] / 255.f;
            b = floats[index++] / 255.f;
        }else{
            r = jComponent.getBackground().getRed() / 255.f;
            g = jComponent.getBackground().getGreen() / 255.f;
            b = jComponent.getBackground().getBlue() / 255.f;
        }

        if(SwingUtils.isMasked(i, ALPHA)) {
            a = floats[index++] / 255.f;
        }else{
            a = jComponent.getBackground().getAlpha() / 255.f;
        }

        jComponent.setBackground(new Color(r, g, b, a));
        jComponent.repaint();
    }



}
