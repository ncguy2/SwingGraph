package net.ncguy.graph.scene.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Guy on 14/01/2017.
 */
@Deprecated
public class CheckBoxList<T> extends JList<T> {

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public CheckBoxList() {
        setCellRenderer(new CellRenderer());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if(index != -1) {
                    JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    repaint();
                }
            }
        });

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    protected class CellRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JCheckBox checkBox = (JCheckBox) value;
            checkBox.setBackground(isSelected ? getSelectionBackground() : getBackground());
            checkBox.setForeground(isSelected ? getSelectionForeground() : getForeground());
            checkBox.setEnabled(isEnabled());
            checkBox.setFont(getFont());
            checkBox.setFocusPainted(false);
            checkBox.setBorderPainted(true);
//            checkBox.setBorderPaintedFlat(true);
            checkBox.setBorder(isSelected ?
                UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

            return checkBox;
        }
    }

}
