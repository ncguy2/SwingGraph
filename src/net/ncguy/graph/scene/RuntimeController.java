package net.ncguy.graph.scene;

import net.ncguy.graph.runtime.LibraryStateChangeEvent;
import net.ncguy.graph.runtime.RuntimeReserve;
import net.ncguy.graph.runtime.api.IRuntimeCompiler;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.components.CheckBoxListCellRenderer;
import net.ncguy.graph.scene.components.CheckBoxListCellRenderer.CheckListItem;
import net.ncguy.graph.scene.components.CheckBoxListCellRenderer.ListItem;
import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.swing.*;
import java.awt.*;
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
    private JButton compileBtn;
    private JPanel compilerDetails;
    private JList<CheckListItem<IRuntimeCore>> libraryList;

    private RuntimeController() {
        setTitle("Runtime configuration");
        getContentPane().add(rootPanel);
        setSize(300, 450);
        setLocationRelativeTo(null);

        compilerDetails.setLayout(new BorderLayout());

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



        DefaultListModel<ListItem<IRuntimeCore>> compilerModel = new DefaultListModel<>();
        runtimeMap.forEach((s, c) -> {
            if(!c.hasCompiler()) return;
            compilerModel.addElement(new ListItem<>(c, s));
        });
        compilerList.setModel(compilerModel);
        compilerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        compilerList.addListSelectionListener(e -> {
            Object o = compilerList.getSelectedValue();
            if(!(o instanceof ListItem)) return;
            ListItem<IRuntimeCore> listItem = (ListItem<IRuntimeCore>) o;
            IRuntimeCore core = listItem.getItem();

            compilerDetails.removeAll();

            if(core == null) {
                compilerDetails.add(new JLabel("Runtime core is null"));
                return;
            }
            IRuntimeCompiler compiler = core.compiler();
            if(compiler == null) {
                compilerDetails.add(new JLabel("Runtime compiler is null"));
                return;
            }
            Component comp = compiler.details();
            if(comp == null) {
                compilerDetails.add(new JLabel("Compiler details component is null"));
                return;
            }
            compilerDetails.add(comp, BorderLayout.CENTER);
        });
        compileBtn.addActionListener(e -> {
            Object o = compilerList.getSelectedValue();
            if(o == null) return;
            if(!(o instanceof ListItem)) return;
            ListItem<IRuntimeCore> listItem = (ListItem<IRuntimeCore>) o;
            IRuntimeCore core = listItem.getItem();
            core.compiler().compile(SceneGraphForm.instance.getGraph());
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
