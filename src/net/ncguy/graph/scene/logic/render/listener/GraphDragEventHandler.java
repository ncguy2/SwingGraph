package net.ncguy.graph.scene.logic.render.listener;

import aurelienribon.tweenengine.Tween;
import net.ncguy.graph.scene.render.SceneGraphForm;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PDragEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.nodes.PPath;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Consumer;

import static net.ncguy.graph.tween.PNodeTweenAccessor.*;

/**
 * Created by Guy on 14/01/2017.
 */
public class GraphDragEventHandler extends PDragEventHandler {

    Consumer<PPath> update;

    public GraphDragEventHandler(Consumer<PPath> update) {
        this.update = update;
    }

    {
        PInputEventFilter filter = new PInputEventFilter();
        filter.setOrMask(InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK);
        setEventFilter(filter);
    }

    @Override
    public void mouseEntered(PInputEvent event) {
        super.mouseEntered(event);
        if(event.getButton() == MouseEvent.NOBUTTON) {
            PNode node = event.getPickedNode();
            Paint paint = node.getPaint();
            if(paint instanceof Color) {
                node.addAttribute("defaultColour", ((Color) paint).getRGB());
            }
            Color newPaint = Color.RED;
            if(paint instanceof Color)
                newPaint = ((Color) paint).darker();
            Tween.to(node, COLOUR, .2f)
                .target(newPaint.getRed() / 255f, newPaint.getGreen() / 255f, newPaint.getBlue() / 255f)
                .start(SceneGraphForm.instance.tweenManager);
        }
    }

    @Override
    public void mouseExited(PInputEvent event) {
        super.mouseExited(event);
        if(event.getButton() == MouseEvent.NOBUTTON) {
            PNode node = event.getPickedNode();
            Object o = node.getAttribute("defaultColour");
            Color newPaint = Color.WHITE;
            Tween.to(node, COLOUR, .2f)
                    .target(newPaint.getRed() / 255f, newPaint.getGreen() / 255f, newPaint.getBlue() / 255f)
                    .start(SceneGraphForm.instance.tweenManager);
        }
    }

    @Override
    protected void startDrag(PInputEvent event) {
        super.startDrag(event);
        event.setHandled(true);
        event.getPickedNode().raiseToTop();
    }

    @Override
    protected void drag(PInputEvent event) {
        super.drag(event);
        ArrayList edges = (ArrayList) event.getPickedNode().getAttribute("edges");
        for(int i = 0; i < edges.size(); i++)
            update.accept((PPath) edges.get(i));
    }

}
