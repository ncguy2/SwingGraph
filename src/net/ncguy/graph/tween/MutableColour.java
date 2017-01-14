package net.ncguy.graph.tween;

import aurelienribon.tweenengine.TweenAccessor;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.lang.reflect.Field;

/**
 * Created by Guy on 14/01/2017.
 */
public class MutableColour extends Color implements TweenAccessor<MutableColour> {

    public static final int R = 1;
    public static final int G = 2;
    public static final int B = 4;
    public static final int A = 8;

    @Override
    public int getValues(MutableColour color, int i, float[] floats) {
        int index = 0;
        cascade();
        if(isMasked(i, R)) floats[index++] = r;
        if(isMasked(i, G)) floats[index++] = g;
        if(isMasked(i, B)) floats[index++] = b;
        if(isMasked(i, A)) floats[index++] = a;
        return index;
    }

    @Override
    public void setValues(MutableColour color, int i, float[] floats) {
        int index = 0;
        if(isMasked(i, R)) r = (int) floats[index++];
        if(isMasked(i, G)) g = (int) floats[index++];
        if(isMasked(i, B)) b = (int) floats[index++];
        if(isMasked(i, A)) a = (int) floats[index++];
        update();
    }

    public void cascade() {
        r = getRed();
        g = getGreen();
        b = getBlue();
        a = getAlpha();
    }

    public void update() {
        int argb = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
        try {
            getARGBField().set(this, argb);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Field argbField;
    private Field getARGBField() {
        if(argbField == null) {
            try {
                Field field = Color.class.getDeclaredField("value");
                field.setAccessible(true);
                argbField = field;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return argbField;
    }

    public int r;
    public int g;
    public int b;
    public int a;

    // Constructors

    public MutableColour(int r, int g, int b) {
        super(r, g, b);
    }

    public MutableColour(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public MutableColour(int rgb) {
        super(rgb);
    }

    public MutableColour(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
    }

    public MutableColour(float r, float g, float b) {
        super(r, g, b);
    }

    public MutableColour(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public MutableColour(ColorSpace cspace, float[] components, float alpha) {
        super(cspace, components, alpha);
    }

    public static boolean isMasked(int composite, int mask) {
        return (composite & mask) != 0;
    }

    public static MutableColour promote(Color c) {
        return new MutableColour(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

}
