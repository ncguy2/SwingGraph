package net.ncguy.graph.tween;

import aurelienribon.tweenengine.TweenAccessor;
import org.piccolo2d.PNode;

import java.awt.*;

/**
 * Created by Guy on 14/01/2017.
 */
public class PNodeTweenAccessor implements TweenAccessor<PNode> {

    public static final int COLOUR = 1;
    public static final int ALPHA = 2;

    @Override
    public int getValues(PNode pNode, int i, float[] floats) {
        int index = 0;
        if(isMasked(i, COLOUR)) {
            Paint p = pNode.getPaint();
            if(p instanceof Color) {
                Color c = (Color) p;
                floats[index++] = c.getRed() / 255f;
                floats[index++] = c.getGreen() / 255f;
                floats[index++] = c.getBlue() / 255f;
            }
        }
        if(isMasked(i, ALPHA)) {
            floats[index++] = pNode.getTransparency();
        }
        return index;
    }

    @Override
    public void setValues(PNode pNode, int i, float[] floats) {
        int index = 0;
        if(isMasked(i, COLOUR)) {
            try {
                Color c = new Color(floats[index++], floats[index++], floats[index++]);
                pNode.setPaint(c);
            }catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if(isMasked(i, ALPHA)) pNode.setTransparency(floats[index++]);
    }

    public static boolean isMasked(int composite, int mask) {
        return (composite & mask) != 0;
    }

}
