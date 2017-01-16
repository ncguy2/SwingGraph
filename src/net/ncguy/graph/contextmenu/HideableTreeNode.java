package net.ncguy.graph.contextmenu;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

/**
 * Created by Guy on 16/01/2017.
 */
public class HideableTreeNode extends DefaultMutableTreeNode {

    protected boolean isVisible;

    public HideableTreeNode() {
        this(null);
    }

    public HideableTreeNode(Object userObject) {
        this(userObject, true, true);
    }

    public HideableTreeNode(Object userObject, boolean allowsChildren, boolean isVisible) {
        super(userObject, allowsChildren);
        this.isVisible = isVisible;
    }

    public TreeNode getChildAt(int index, boolean filterIsActive) {
        if(!filterIsActive)
            return super.getChildAt(index);
        if(children == null)
            throw new ArrayIndexOutOfBoundsException("Node has no children");

        int realIndex = -1;
        int visibleIndex = -1;
        Enumeration e = children.elements();
        while(e.hasMoreElements()) {
            HideableTreeNode node = (HideableTreeNode) e.nextElement();
            if(node.isVisible)
                visibleIndex++;
            realIndex++;
            if(visibleIndex == index)
                return (TreeNode) children.elementAt(realIndex);
        }
        throw new ArrayIndexOutOfBoundsException("Index unmatched");
    }

    public int getChildCount(boolean filterActive) {
        if(!filterActive)
            return super.getChildCount();

        if(children == null)
            return 0;

        int count = 0;
        Enumeration e = children.elements();
        while(e.hasMoreElements()) {
            HideableTreeNode node = (HideableTreeNode) e.nextElement();
            if(node.isVisible)
                count++;
        }
        return count;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
