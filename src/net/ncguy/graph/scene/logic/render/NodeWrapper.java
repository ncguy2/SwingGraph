package net.ncguy.graph.scene.logic.render;

import org.piccolo2d.PInputManager;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.pswing.PSwing;
import org.piccolo2d.util.PBounds;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ListIterator;

/**
 * Created by Guy on 14/01/2017.
 */
public class NodeWrapper extends PSwing {

    NodeComponent nodeComponent;

    public NodeWrapper(NodeComponent nodeComponent) {
        super(nodeComponent);
        this.nodeComponent = nodeComponent;

        this.nodeComponent.onRepaintRequest = this::repaint;

        addInputEventListener(new PInputManager(){

            @Override
            public void mouseClicked(PInputEvent event) {
                super.mouseClicked(event);
                propagateEvent(event);
            }

            @Override
            public void mouseWheelRotated(PInputEvent event) {
                super.mouseWheelRotated(event);
                propagateEvent(event);
            }

            @Override
            public void mouseWheelRotatedByBlock(PInputEvent event) {
                super.mouseWheelRotatedByBlock(event);
                propagateEvent(event);
            }

            @Override
            public void mouseDragged(PInputEvent event) {
                super.mouseDragged(event);
                propagateEvent(event);
            }

            @Override
            public void mouseEntered(PInputEvent event) {
                super.mouseEntered(event);
                propagateEvent(event);
            }

            @Override
            public void mouseExited(PInputEvent event) {
                super.mouseExited(event);
                propagateEvent(event);
            }

            @Override
            public void mouseMoved(PInputEvent event) {
                super.mouseMoved(event);
                propagateEvent(event);
            }

            @Override
            public void mouseReleased(PInputEvent event) {
                super.mouseReleased(event);
                propagateEvent(event);
            }

            @Override
            public void mousePressed(PInputEvent event) {
                super.mousePressed(event);
                propagateEvent(event);
            }
        });

        nodeComponent.getXOverride = () -> (int) getXOffset();
        nodeComponent.getYOverride = () -> (int) getYOffset();
    }

    private void propagateEvent(PInputEvent event) {
        InputEvent e = event.getSourceSwingEvent();


        MouseEvent mouseEvent = new MouseEvent(nodeComponent, e.getID(), event.getWhen(), event.getModifiers(),
                (int) event.getPosition().getX(), (int) event.getPosition().getY(), event.getClickCount(), event.isPopupTrigger(), event.getButton());

        Point2D canvasPosition = event.getPosition();
        NodeWrapper w = findWrapperAtLocation(event, canvasPosition.getX(), canvasPosition.getY());

        nodeComponent.dispatchEvent(mouseEvent);
        if(w != null && w != this) w.nodeComponent.dispatchEvent(mouseEvent);
        if(mouseEvent.isConsumed())
            event.setHandled(true);
    }

    private NodeWrapper findWrapperAtLocation(PInputEvent e, double x, double y) {
        PNode parent = getParent();
        if(parent == null) return null;
        ListIterator it = parent.getChildrenIterator();
        while(it.hasNext()) {
            Object obj = it.next();
            if(!(obj instanceof NodeWrapper)) continue;
            NodeWrapper n = (NodeWrapper) obj;
            PBounds bounds = n.getFullBounds();
            if(bounds.contains(x, y))
                return n;
        }
        return null;
    }

    @Override
    public void setPaint(Paint newPaint) {
        super.setPaint(newPaint);
//        if(newPaint instanceof Color) {
//            if(newPaint.equals(Color.WHITE))
//                this.nodeComponent.resetBackground();
//            else this.nodeComponent.setBackground((Color) newPaint);
//        }

    }

    public Color getTrueBackground() {
        return nodeComponent.defaultBG;
    }
}
