package net.ncguy.graph.scene;

import net.ncguy.graph.runtime.LibraryStateChangeEvent;
import net.ncguy.graph.runtime.RuntimeReserve;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.components.CheckBoxListCellRenderer;
import net.ncguy.graph.scene.components.CheckBoxListCellRenderer.CheckListItem;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Created by Guy on 14/01/2017.
 */
public class RuntimeController extends JFrame {
    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;
    private JList compilerList;
    private JButton selectBtn;
    private JPanel compilerDetails;
    private JList<CheckListItem<IRuntimeCore>> libraryList;

    private RuntimeController() {
        setTitle("Runtime configuration");
        getContentPane().add(rootPanel);
        setSize(300, 450);
        setLocationRelativeTo(null);

        libraryList.setCellRenderer(new CheckBoxListCellRenderer());



        Map<String, IRuntimeCore> runtimeMap = RuntimeReserve.instance().runtimeMap;
        DefaultListModel<CheckListItem<IRuntimeCore>> model = new DefaultListModel<>();
        runtimeMap.forEach((s, c) -> {
            if(!c.hasLibrary()) return;
            model.addElement(new CheckListItem<>(c, s));
        });

        libraryList.setModel(model);
        libraryList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList list = (JList) e.getSource();
                int index = list.locationToIndex(e.getPoint());

                CheckListItem<IRuntimeCore> item = (CheckListItem<IRuntimeCore>) list.getModel().getElementAt(index);
                item.setSelected(!item.isSelected());
                onItemToggle(item);
                list.repaint(list.getCellBounds(index, index));
            }
        });

    }

    private void onItemToggle(CheckListItem<IRuntimeCore> item) {
        if(!item.getItem().hasLibrary()) return;
        LibraryStateChangeEvent.LibraryState state = LibraryStateChangeEvent.LibraryState.ENABLED;
        if(!item.isSelected())
            state = LibraryStateChangeEvent.LibraryState.DISABLED;
        new LibraryStateChangeEvent(item.getItem().library(), state).fire();
    }


    private static RuntimeController instance;
    public static RuntimeController instance() {
        if (instance == null)
            instance = new RuntimeController();
        return instance;
    }

}
