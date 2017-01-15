package net.ncguy.graph.scene.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Guy on 14/01/2017.
 */
public class CheckBoxListCellRenderer extends JCheckBox implements ListCellRenderer<CheckBoxListCellRenderer.CheckListItem> {

    @Override
    public Component getListCellRendererComponent(JList list, CheckListItem value, int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setEnabled(list.isEnabled());
        setSelected(value.isSelected());
        setText(value.toString());

        return this;
    }

    public static class CheckListItem<T> extends ListItem<T> {


        private boolean isSelected = false;

        public CheckListItem(T item, String label) {
            super(item, label);
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }

    public static class ListItem<T> {
        protected T item;
        protected String label;

        public ListItem(T item, String label) {
            this.item = item;
            this.label = label;
        }

        public T getItem() {
            return item;
        }

        public void setItem(T item) {
            this.item = item;
        }

        @Override
        public String toString() {
            return label;
        }

    }


}
