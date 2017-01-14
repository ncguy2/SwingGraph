package net.ncguy.graph.contextmenu;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * Created by Guy on 14/01/2017.
 */
public class ContextMenuForm extends JFrame {
    private JTextField searchField;
    private JTree nodeTree;
    private JScrollPane nodeScroller;
    private JPanel rootPanel;

    public ContextMenuForm() {
        super();
        getContentPane().add(rootPanel);
        setUndecorated(true);
        setOpacity(0.9f);
        setVisible(true);
        setSize(368, 256);

        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                setVisible(false);
            }
        });
    }
}
