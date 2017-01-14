package net.ncguy.graph.contextmenu;

import net.ncguy.graph.data.tree.*;
import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.runtime.LibraryStateChangeEvent;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.factory.NodeFactory;
import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 14/01/2017.
 */
public class ContextMenuForm extends JFrame implements LibraryStateChangeEvent.LibraryStateChangeListener {

    private JTextField searchField;
    private JTree nodeTree;
    private JScrollPane nodeScroller;
    private JPanel rootPanel;

    public Point point;

    private List<NodeFactory> collectiveNodeFactories;

    public ContextMenuForm() {
        super();
        point = new Point();
        EventBus.instance().register(this);
        collectiveNodeFactories = new ArrayList<>();
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

        nodeTree.addTreeSelectionListener(e -> {
            Object o = e.getPath().getLastPathComponent();
            if(o instanceof DefaultMutableTreeNode)
                o = ((DefaultMutableTreeNode) o).getUserObject();
            if(o instanceof TreeObjectWrapper) {
                TreeObjectWrapper t = (TreeObjectWrapper) o;
                Object obj = t.getObject();
                System.out.println(obj != null ? obj.getClass().getSimpleName() : "Null");
                if(obj != null && obj instanceof NodeFactory) {
                    System.out.println(obj);
                    NodeFactory nf = (NodeFactory) obj;
                    Node node = nf.buildNode();
                    node.location.setLocation(point);
                    SceneGraphForm.instance.getGraph().addNode(node);
                }
            }
        });
    }

    private void invalidateNodeTree() {
        DefaultTreeModel model = (DefaultTreeModel) nodeTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();

        VisitableTree<TreeObjectWrapper<NodeFactory>> tree = new VisitableTree<>(new TreeObjectWrapper<NodeFactory>("root"));
        TreePopulator.populate(tree, collectiveNodeFactories, "/", nf -> nf.category+"/"+nf.title, true);
        tree.accept(new NodeTreePopulator(root, 0));
        model.reload();
    }

    @Override
    public void onLibraryStateChange(LibraryStateChangeEvent event) {
        switch(event.getState()) {
            case ENABLED: enableLibrary(event.getLibrary()); break;
            case DISABLED: disableLibrary(event.getLibrary()); break;
        }
    }

    public void enableLibrary(IRuntimeLibrary lib) {
        if(lib != null)
            collectiveNodeFactories.addAll(lib.getNodeFactories());
        invalidateNodeTree();
    }

    public void disableLibrary(IRuntimeLibrary lib) {
        if(lib != null)
            collectiveNodeFactories.removeAll(lib.getNodeFactories());
        invalidateNodeTree();
    }

    public static class NodeTreePopulator implements IVisitor<TreeObjectWrapper<NodeFactory>> {

        DefaultMutableTreeNode parentNode;
        DefaultMutableTreeNode currentNode;
        int depth;

        public NodeTreePopulator(DefaultMutableTreeNode parentNode, int depth) {
            this.parentNode = parentNode;
            this.depth = depth;
        }

        @Override
        public IVisitor<TreeObjectWrapper<NodeFactory>> visit(IVisitable<TreeObjectWrapper<NodeFactory>> visitable) {
            DefaultMutableTreeNode node;
            if(depth <= 0) {
                node = parentNode;
            }else{
                if(currentNode == null) {
                    node = new DefaultMutableTreeNode(visitable.data());
                    parentNode.add(node);
                }else node = currentNode;
            }
            return new NodeTreePopulator(node, depth + 1);
        }

        @Override
        public void visitData(IVisitable<TreeObjectWrapper<NodeFactory>> visitable, TreeObjectWrapper<NodeFactory> data) {
            if(depth > 0) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
                parentNode.add(node);
                currentNode = node;
            }
        }
    }

}
