package net.ncguy.graph.scene.logic.render;

import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.PinDropEvent;
import net.ncguy.graph.scene.logic.PinHoverEvent;
import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Guy on 24/10/2016.
 */
public class PinComponent extends JPanel implements PinDropEvent.PinDropListener, PinHoverEvent.PinHoverListener {

    public NodeComponent parentNodeComponent;
    public Pin pin;

    static BufferedImage pinImage;
    static BufferedImage pinImageConnected;

    public static BufferedImage getPinImage() {
        if (pinImage == null) {
            try {
                pinImage = ImageIO.read(PinComponent.class.getResource("icons/pinDisconnected.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pinImage;
    }

    public static BufferedImage getPinImageConnected() {
        if (pinImageConnected == null) {
            try {
                pinImageConnected = ImageIO.read(PinComponent.class.getResource("icons/pinConnected.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pinImageConnected;
    }

    //    static {
//        try {
//            pinImage = ImageIO.read(PinComponent.class.getResource("icons/pin.png"));
//            pinImageConnected = ImageIO.read(PinComponent.class.getResource("icons/pinConnected.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private boolean drawLegacy = false;

    private boolean beginDragged = false;
    public boolean isHovered = false;

    public PinComponent(NodeComponent parentNodeComponent, Pin pin) {
        this.parentNodeComponent = parentNodeComponent;
        this.pin = pin;

        SceneGraphForm.instance.continuousRenderables.add(this);

        setSize(getPinImage().getWidth(), getPinImage().getHeight());

        EventBus.instance().register(this);


        addMouseListener(new MouseAdapter() {
            // Source Drag
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                e.consume();

                if(SceneGraphForm.instance.isAltPressed) {
                    if(isConnected()) {
                        pin.disconnectFrom(pin.connected);
                    }
                    return;
                }
                beginDragged = true;

                SceneGraphForm.instance.getGraphRenderer().SetFreeWire(PinComponent.this.pin);
                // TODO initiate drag
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }

            // Source drop
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if(!beginDragged) return;
                new PinDropEvent(PinComponent.this).fire();
                beginDragged = false;
                isHovered = false;
                // TODO resolve drop
                SceneGraphForm.instance.getGraphRenderer().RemoveFreeWire(PinComponent.this.pin);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }
        });

    }

    public boolean isConnected() {
        return pin.connected != null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(drawLegacy)
            paintLegacy(g);
        else {
            Graphics2D g2d = (Graphics2D) g;

            BufferedImage i;
            if(isConnected()) i = getPinImageConnected();
            else i = getPinImage();

//            int rule = AlphaComposite.SRC_OVER;
//            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//            Composite comp = AlphaComposite.getInstance(rule, 0.99f);
//            g2d.setComposite(comp);
            g2d.drawImage(i, 0, 1, getWidth(), getHeight()-2, this);
//            g2d.setComposite(AlphaComposite.getInstance(rule, 1f));
        }

        if(isConnected()) {
            g.setColor(pin.wireColour);
            int midX = getWidth() / 2;
            int midY = getHeight() / 2;
            midX -= 1;
            g.drawOval(midX-1, midY-1, 2, 2);
            g.setColor(getForeground());
        }

    }

    protected void paintLegacy(Graphics g) {
        g.setColor(parentNodeComponent.getBackground());
        g.drawRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground());
        if(isHovered)
            g.setColor(Color.GREEN);
        g.drawOval(1, 1, getWidth()-2, getHeight()-2);
        g.setColor(getForeground());
    }

    @Override
    public void onPinDrop(PinDropEvent event) {
        if(beginDragged) return;
        if(!isHovered) return;
        if(event.component == null || event.component == this) return;
        if(event.component.pin == null) return;
        System.out.println("Connection attempt");
        pin.tryConnect(event.component.pin);
    }

    @Override
    public void onPinHover(PinHoverEvent event) {
        if(event.component == null) return;
        setHover(event.isHovering());
    }

    public void setHover(boolean hover) {
        this.isHovered = hover;
    }

    public void forcePaint() {
//        paintImmediately(0, 0, getWidth(), getHeight());
        Graphics g = getGraphics();
        if(g != null) paintComponent(g);
        else repaint();
    }

}
