package net.ncguy.graph.scene.logic;

import net.ncguy.graph.scene.listeners.ZoomAndPanListener;
import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by Guy on 24/10/2016.
 */
@Deprecated
public class SceneGraph_dep extends JPanel {

    int counter = 0;
    boolean useGraphicsContext = true;
    TexturePaint checkerTexture;
    Point currentPos;
    ZoomAndPanListener zoomAndPanListener;

    public SceneGraph_dep() {
        init();
    }

    public void init() {
        checkerTexture = makeCheckerTexture();
        setLayout(null);
        initListener();
    }

    public void initListener() {
        addDragListeners();
//        zoomAndPanListener = new ZoomAndPanListener(this);
//        addMouseListener(zoomAndPanListener);
//        addMouseMotionListener(zoomAndPanListener);
//        addMouseWheelListener(zoomAndPanListener);
    }


    boolean init = true;

    private static TexturePaint makeCheckerTexture() {
        int cs = 20;
        int sz = cs*cs;
        BufferedImage img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setPaint(Color.GRAY);
        for(int i = 0; i * cs < sz; i++) {
            for(int j = 0; j * cs < sz; j++) {
                if((i + j) % 2 == 0) {
                    g2.fillRect(i * cs, j * cs, cs, cs);
                }
            }
        }
        g2.dispose();

        return new TexturePaint(img, new Rectangle(0, 0, sz, sz));
    }

    protected Point anchorPoint;
    private void addDragListeners() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                anchorPoint = e.getPoint();
                currentPos = e.getPoint();
                e.consume();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point parentOnScreen = getParent().getLocationOnScreen();
                Point mouseOnScreen = e.getLocationOnScreen();

                Point position = new Point(mouseOnScreen.x - parentOnScreen.x - anchorPoint.x, mouseOnScreen.y - parentOnScreen.y - anchorPoint.y);
                Container parent = getParent();
                if(parent instanceof JViewport) {
                    Rectangle rect = new Rectangle();
                    rect.setSize(parent.getWidth(), parent.getHeight());
                    rect.setLocation((currentPos.x) - position.x, (currentPos.y) - position.y);
                    ((JViewport) parent).scrollRectToVisible(rect);
                    SceneGraphForm.instance.setTitle(position.toString()+", "+rect.toString());
                }
                e.consume();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
//        Container parent = getParent();
//        if(parent instanceof JViewport) {
//            AffineTransform transform = zoomAndPanListener.getCoordTransform();
//            Rectangle rect = new Rectangle();
//            rect.setSize(parent.getWidth(), parent.getHeight());
//            rect.setLocation((int) transform.getTranslateX()-(parent.getWidth()/2), (int)transform.getTranslateY()-(parent.getHeight()/2));
//            rect.x = Math.max(0, Math.min(getWidth()-(parent.getWidth()/2), rect.x));
//            rect.y = Math.max(0, Math.min(getHeight()-(parent.getHeight()/2), rect.y));
//            ((JViewport) parent).scrollRectToVisible(rect);
//            SceneGraphForm.instance.setTitle(rect.toString());
//            parent.revalidate();
//        }
        super.paint(g);
    }
}
