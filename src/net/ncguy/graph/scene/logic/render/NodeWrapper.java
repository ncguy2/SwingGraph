package net.ncguy.graph.scene.logic.render;

import org.piccolo2d.PInputManager;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.pswing.PSwing;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Guy on 14/01/2017.
 */
public class NodeWrapper extends PSwing {

    NodeComponent nodeComponent;

    public NodeWrapper(NodeComponent nodeComponent) {
        super(nodeComponent);
        this.nodeComponent = nodeComponent;
        addInputEventListener(new PInputManager(){
            @Override
            public void mousePressed(PInputEvent event) {
                super.mousePressed(event);
                MouseEvent mouseEvent = new MouseEvent(nodeComponent, MouseEvent.MOUSE_PRESSED, event.getWhen(), event.getModifiers(),
                        (int) event.getPosition().getX(), (int) event.getPosition().getY(), event.getClickCount(), event.isPopupTrigger(), event.getButton());
                nodeComponent.dispatchEvent(mouseEvent);
                if(mouseEvent.isConsumed())
                    event.setHandled(true);
            }
        });

        nodeComponent.getXOverride = () -> (int) getXOffset();
        nodeComponent.getYOverride = () -> (int) getYOffset();
    }

    @Override
    public void setPaint(Paint newPaint) {
        super.setPaint(newPaint);
        if(newPaint instanceof Color) {
            if(newPaint.equals(Color.WHITE))
                this.nodeComponent.resetBackground();
            else this.nodeComponent.setBackground((Color) newPaint);
        }
    }
}
