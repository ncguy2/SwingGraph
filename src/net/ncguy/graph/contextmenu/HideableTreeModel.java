package net.ncguy.graph.contextmenu;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Guy on 16/01/2017.
 */
public class HideableTreeModel extends DefaultTreeModel {

    protected boolean filterIsActive;

    public HideableTreeModel(TreeNode root) {
        this(root, false);
    }

    public HideableTreeModel(TreeNode root, boolean asksAllowsChildren) {
        this(root, asksAllowsChildren, false);
    }

    public HideableTreeModel(TreeNode root, boolean asksAllowsChildren, boolean filterIsActive) {
        super(root, asksAllowsChildren);
        this.filterIsActive = filterIsActive;
    }

    public void activateFilter(boolean newValue) {
        filterIsActive = newValue;
    }

    public boolean isActivatedFilter() {
        return filterIsActive;
    }

    public Object getChild(Object parent, int index) {
        if(filterIsActive) {
            if(parent instanceof HideableTreeNode) {
                return ((HideableTreeNode) parent).getChildAt(index, filterIsActive);
            }
        }
        return ((TreeNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        if(filterIsActive) {
            if(parent instanceof HideableTreeNode) {
                return ((HideableTreeNode) parent).getChildCount(filterIsActive);
            }
        }
        return ((TreeNode) parent).getChildCount();
    }

    public List<HideableTreeNode> getLeafNodes() {
        List<HideableTreeNode> allNodes = new ArrayList<>();
        getNodes((HideableTreeNode) root, allNodes);
        return allNodes.stream()
                .filter(DefaultMutableTreeNode::isLeaf)
                .collect(Collectors.toList());
    }

    public void getNodes(HideableTreeNode node, List<HideableTreeNode> nodeList) {
        Enumeration children = node.children();
        while(children.hasMoreElements()) {
            Object o = children.nextElement();
            if(o instanceof HideableTreeNode) {
                nodeList.add((HideableTreeNode) o);
                getNodes((HideableTreeNode) o, nodeList);
            }
        }
    }

}
