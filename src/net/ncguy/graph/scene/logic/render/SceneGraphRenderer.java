package net.ncguy.graph.scene.logic.render;

import net.ncguy.graph.event.OpenContextMenuEvent;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.render.listener.GraphDragEventHandler;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.event.PInputEventListener;
import org.piccolo2d.event.PPanEventHandler;
import org.piccolo2d.event.PZoomEventHandler;
import org.piccolo2d.nodes.PPath;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Guy on 14/01/2017.
 */
public class SceneGraphRenderer extends PCanvas {

    private Map<Node, NodeWrapper> nodeWrapperMap;

    private SceneGraph graph;

    private int width;
    private int height;

    PLayer nodeLayer;
    PLayer edgeLayer;

    boolean useSpline = true;

    public SceneGraphRenderer(int width, int height, SceneGraph graph) {
        this.width = width;
        this.height = height;
        this.graph = graph;
        nodeWrapperMap = new HashMap<>();
        setPreferredSize(new Dimension(width, height));
        PLayer nodeLayer = getLayer();
        PLayer edgeLayer = new PLayer();

        this.nodeLayer = nodeLayer;
        this.edgeLayer = edgeLayer;

        nodeLayer.addInputEventListener(new GraphDragEventHandler(this::updateEdge));

        getRoot().addChild(edgeLayer);
        getCamera().addLayer(0, edgeLayer);

        PInputEventListener[] listeners = getCamera().getInputEventListeners();
        for (PInputEventListener listener : listeners)
            getCamera().removeInputEventListener(listener);

        getCamera().addInputEventListener(new CustomZoomEventHandler());
        getCamera().addInputEventListener(new PPanEventHandler());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton() == MouseEvent.BUTTON3)
                    new OpenContextMenuEvent(e.getXOnScreen(), e.getYOnScreen()).fire();
            }
        });

//        generateTestNodes();
        graph.addOnAddListener(this::onGraphNodeAdd);
        graph.addOnRemoveListener(this::onGraphNodeRemove);
    }

    private void onGraphNodeAdd(Node node) {
        float x = node.location.x;
        float y = node.location.y;
        NodeWrapper wrapper = NodeComponent.create(node);
        wrapper.translate(x, y);
        wrapper.addAttribute("edges", new ArrayList<>());
        nodeLayer.addChild(wrapper);
        nodeWrapperMap.put(node, wrapper);
    }

    private void onGraphNodeRemove(Node node) {
        NodeWrapper wrapper = nodeWrapperMap.get(node);
        if(wrapper == null) return;
        ArrayList edges = (ArrayList) wrapper.getAttribute("edges");
        if(edges != null) {
            edges.stream()
                    .filter(edge -> edge instanceof PPath)
                    .forEach(edge -> {
                        PPath path = (PPath) edge;
                        ArrayList nodes = (ArrayList) path.getAttribute("nodes");
                        nodes.forEach(n -> ((ArrayList)((PNode)n).getAttribute("edges")).clear());
                        edgeLayer.removeChild(path);
                        updateEdge(path);
            });
        }
        nodeLayer.removeChild(wrapper);
    }

    private void generateTestNodes() {
        int numNodes = 7;
        int numEdges = 7;
        Random rand = new Random();

        for (int i = 0; i < numEdges; i++) {
            int n1 = rand.nextInt(numNodes);
            int n2 = rand.nextInt(numNodes);

            // Make sure we have two distinct nodes.
            while (n1 == n2) {
                n2 = rand.nextInt(numNodes);
            }

            PNode node1 = nodeLayer.getChild(n1);
            PNode node2 = nodeLayer.getChild(n2);
            PPath edge;
            edge = PPath.createLine(0, 0, 0, 0);

            ((ArrayList)node1.getAttribute("edges")).add(edge);
            ((ArrayList)node2.getAttribute("edges")).add(edge);
            edge.addAttribute("nodes", new ArrayList());
            ((ArrayList)edge.getAttribute("nodes")).add(node1);
            ((ArrayList)edge.getAttribute("nodes")).add(node2);
            edgeLayer.addChild(edge);
            updateEdge(edge);
        }
    }

    public void updateEdge(PPath edge) {
        // Note that the nodeComponent's "FullBounds" must be used
        // (instead of just the "Bounds") because the nodes
        // have non-identity transforms which must be included
        // when determining their position.


        PNode node1 = (PNode) ((ArrayList)edge.getAttribute("nodes")).get(0);
        PNode node2 = (PNode) ((ArrayList)edge.getAttribute("nodes")).get(1);
        Point2D start = node1.getFullBoundsReference().getCenter2D();
        Point2D end = node2.getFullBoundsReference().getCenter2D();
        edge.reset();

        Function<Float, Double> lerpX = alpha -> start.getX() + (end.getX() - start.getX()) * alpha;
        Function<Float, Double> lerpY = alpha -> start.getY() + (end.getY() - start.getY()) * alpha;

        BiFunction<Float, Float, Point2D.Double> lerp = (a, b) -> {
            Point2D.Double p = new Point2D.Double();
            p.x = lerpX.apply(a);
            p.y = lerpY.apply(b);
            return p;
        };

        edge.moveTo((float) start.getX(), (float) start.getY());
        if(useSpline) {
            Point2D.Double[] ps = new Point2D.Double[]{
                    lerp.apply(0f, 0f),
                    lerp.apply(0.25f, 0f),
                    lerp.apply(0.5f, 0.5f),
                    lerp.apply(0.75f, 1.0f),
                    lerp.apply(1.0f, 1.0f)
            };
            edge.setPaint(null);
            edge.setStroke(new BasicStroke(8));
            edge.curveTo(ps[0].x, ps[0].y,
                    ps[1].x, ps[1].y,
                    ps[2].x, ps[2].y);
            edge.curveTo(ps[2].x, ps[2].y,
                    ps[3].x, ps[3].y,
                    ps[4].x, ps[4].y);
        }else {
            edge.lineTo((float) end.getX(), (float) end.getY());
        }
    }

    public void addNode() {
//        add(new NodeComponent("asdf"+(counter++)));
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        super.paint(g);

    }

    private void drawGrid(Graphics g) {
        Point2D t = getCamera().getViewBounds().getOrigin();

        double s = getCamera().getViewScale();

        int xDiff = (int) (10 * s);
        int yDiff = (int) (10 * s);

        int xOffset = (int) -t.getX();
        int yOffset = (int) -t.getY();

        for(int x = xOffset; x < getWidth()+xOffset; x+=xDiff) {
            g.drawLine((int) (x * s), (int)(yOffset * s), (int)(x * s), (int) ((getHeight()+yOffset) * s));
        }
        for(int y = yOffset; y < getHeight()+yOffset; y+=yDiff) {
            g.drawLine((int)(xOffset * s), (int)(y * s), (int) ((getWidth()+xOffset) * s), (int)(y * s));
        }
    }

    public static class CustomZoomEventHandler extends PZoomEventHandler {
        public CustomZoomEventHandler() {
            super();
            setEventFilter(new PInputEventFilter(InputEvent.BUTTON2_MASK));
        }
    }

}
