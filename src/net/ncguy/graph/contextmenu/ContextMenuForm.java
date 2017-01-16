package net.ncguy.graph.contextmenu;

import net.ncguy.graph.data.tree.*;
import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.runtime.LibraryStateChangeEvent;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;
import net.ncguy.graph.scene.render.SceneGraphForm;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
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
    private List<HideableTreeNode> leafNodes;
    DefaultMutableTreeNode rootNode;

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
        HideableTreeModel model = new HideableTreeModel(new HideableTreeNode());
        model.activateFilter(true);
        nodeTree.setModel(model);

        nodeTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                HideableTreeNode node = (HideableTreeNode) value;
                if(!node.isVisible())
                    setForeground(Color.YELLOW);
                return this;
            }
        });

        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                setVisible(false);
            }
        });

        nodeTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                TreePath path = nodeTree.getSelectionPath();
                if(path == null) return;
                Object o = path.getLastPathComponent();
                if(o != null && o instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) o;
                    if(treeNode.isLeaf())
                        spawnNode(treeNode);
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(leafNodes == null) return;
                String query = searchField.getText().toLowerCase();
                int qLen = query.length();
                leafNodes.forEach(node -> {
                    if(qLen == 0) {
                        node.setVisible(true);
                        return;
                    }
                    String nStr = node.toString();
                    int nLen = nStr.length();

                    if(qLen > nLen) {
                        node.setVisible(false);
                        return;
                    }
                    String q = nStr.substring(0, qLen);
                    if(q.equalsIgnoreCase(query)) {
                        node.setVisible(true);
                    }else node.setVisible(false);
                });
                Enumeration<TreePath> expandedDescendants = nodeTree.getExpandedDescendants(nodeTree.getPathForRow(0));
                model.reload();
                if(expandedDescendants != null) {
                    while (expandedDescendants.hasMoreElements()) {
                        try {
                            TreePath treePath = expandedDescendants.nextElement();
                            nodeTree.expandPath(treePath);
                        } catch (Exception exc) {}
                    }
                }
            }
        });
    }

    public void spawnNode(DefaultMutableTreeNode treeNode) {
        Object o = treeNode.getUserObject();
        if(o instanceof TreeObjectWrapper) {
            TreeObjectWrapper t = (TreeObjectWrapper) o;
            Object obj = t.getObject();
            System.out.println(obj != null ? obj.getClass().getSimpleName() : "Null");
            if(obj != null && obj instanceof NodeFactory) {
                System.out.println(obj);
                NodeFactory nf = (NodeFactory) obj;
                SceneGraph graph = SceneGraphForm.instance.getGraph();
                Node node = nf.buildNode(graph);
                node.location.setLocation(point);
                graph.addNode(node);
            }
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        searchField.setText("");
        if(rootNode == null) return;
        Enumeration c = rootNode.children();
        while(c.hasMoreElements()) {
            Object o = c.nextElement();
            if(o instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode t = (DefaultMutableTreeNode) o;
                collapseAll(nodeTree, new TreePath(t.getPath()));
            }
        }
    }

    private void invalidateNodeTree() {
        HideableTreeModel model = (HideableTreeModel) nodeTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        rootNode = root;
        root.removeAllChildren();

        VisitableTree<TreeObjectWrapper<NodeFactory>> tree = new VisitableTree<>(new TreeObjectWrapper<NodeFactory>("root"));
        TreePopulator.populate(tree, collectiveNodeFactories, "/", nf -> nf.category+"/"+nf.title, true);
        tree.accept(new NodeTreePopulator(root, 0));
        model.reload();

        leafNodes = model.getLeafNodes();
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
                    node = new HideableTreeNode(visitable.data());
                    parentNode.add(node);
                }else node = currentNode;
            }
            return new NodeTreePopulator(node, depth + 1);
        }

        @Override
        public void visitData(IVisitable<TreeObjectWrapper<NodeFactory>> visitable, TreeObjectWrapper<NodeFactory> data) {
            if(depth > 0) {
                DefaultMutableTreeNode node = new HideableTreeNode(data);
                parentNode.add(node);
                currentNode = node;
            }
        }
    }

    public void expandAll(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root));
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
        // tree.collapsePath(parent);
    }

    public void collapseAll(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        collapseAll(tree, new TreePath(root));
    }

    private void collapseAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                collapseAll(tree, path);
            }
        }
         tree.collapsePath(parent);
    }

}
