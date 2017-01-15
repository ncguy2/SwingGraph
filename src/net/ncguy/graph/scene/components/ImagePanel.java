package net.ncguy.graph.scene.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Guy on 15/01/2017.
 */
public class ImagePanel extends JPanel {

    BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image != null)
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
}
