package net.ncguy.graph.scene.components;

import com.esotericsoftware.tablelayout.swing.Table;
import net.ncguy.graph.utils.ComponentResizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Guy on 16/01/2017.
 */
public class DockableTabbedPane extends JTabbedPane {

    public DockableTabbedPane() {
        super();
        TabDragListener tabDragger = new TabDragListener();
        this.addMouseListener(tabDragger);
        this.addMouseMotionListener(tabDragger);
    }

    private class TabDragListener implements MouseListener, MouseMotionListener {

        Point p0, p0Screen;

        Component current;
        String title;

        @Override
        public void mousePressed(MouseEvent e) {
            p0 = e.getPoint();

            for(int i = 0; i < getTabCount(); i++) {
                Rectangle bounds = getBoundsAt(i);
                if(bounds.contains(p0)) {
                    current = DockableTabbedPane.this.getComponentAt(i);
                    title = DockableTabbedPane.this.getTitleAt(i);
                    p0Screen = getLocationOnScreen();
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            JFrame frame;
            if(current != null) {
                if(p.distance(p0) > 20) {
                    frame = undock(current, title);
                    current = null;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            current = null;
            title = null;
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    private UndockedFrame undock(Component current, String title) {
        Point p = current.getLocationOnScreen();
        remove(current);
        UndockedFrame frame = new UndockedFrame(this, current, title);
        p.translate(20, 20);
        frame.setLocation(p);
        fireStateChanged();
        return frame;
    }

    private class UndockedFrame extends JFrame {

        DockableTabbedPane parent;
        Component current;
        String title;

        public UndockedFrame(DockableTabbedPane parent, Component current, String title) {
            this.parent = parent;
            this.current = current;
            this.setTitle(title);

            setUndecorated(true);

            JButton redockBtn = new JButton("X");
            redockBtn.addActionListener(e -> {
                redock();
            });

            Table root = new Table();
            root.addCell(title).left().padBottom(4);
            root.addCell(redockBtn).right().padBottom(4).row();

            Rectangle b = current.getBounds();

            root.addCell(current).colspan(2).expand().fill().row();

            Container content = this.getContentPane();
            content.setLayout(new BorderLayout());
            content.add(root, BorderLayout.CENTER);

            setVisible(true);
            Graphics g = getGraphics();
            FontMetrics met = g.getFontMetrics();

            int h = met.getHeight();


            this.setBounds(b.x, b.y, (int)b.getWidth(), (int) (b.getHeight() + h + 10));
            this.addWindowListener(new UndockedFrameListener());

            ComponentResizer cr = new ComponentResizer();
            cr.registerComponent(this);
            cr.setSnapSize(new Dimension(10, 10));
        }

        @Override
        public void setTitle(String title) {
            super.setTitle(title);
            this.title = title;
        }

        public void redock() {
            parent.add(title, current);
            this.dispose();
        }

    }

    private class UndockedFrameListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            Window w = e.getWindow();
            if(w instanceof UndockedFrame) {
                ((UndockedFrame) w).redock();
            }
        }
    }

}
