package net.ncguy.graph.scene.logic.render;

import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.event.OpenContextMenuEvent;
import net.ncguy.graph.scene.logic.*;
import net.ncguy.graph.scene.logic.render.listener.GraphDragEventHandler;
import net.ncguy.graph.scene.render.SceneGraphForm;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.event.*;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.util.PBounds;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Guy on 14/01/2017.
 */
public class SceneGraphRenderer extends PCanvas implements PinConnectEvent.PinConnectListener,
        PinDisconnectEvent.PinDisconnectListener {

    private Map<Node, NodeWrapper> nodeWrapperMap;
    private Map<Wire, PPath> wireEdgeMap;

    private SceneGraph graph;

    private int width;
    private int height;

    PLayer nodeLayer;
    PLayer edgeLayer;
    PLayer wireLayer;

    boolean useSpline = true;

    public float spawnX = 0;
    public float spawnY = 0;

    public SceneGraphRenderer(int width, int height, SceneGraph graph) {


        this.width = width;
        this.height = height;
        this.graph = graph;
        nodeWrapperMap = new HashMap<>();
        wireEdgeMap = new HashMap<>();
        setPreferredSize(new Dimension(width, height));
        PLayer nodeLayer = getLayer();
        PLayer edgeLayer = new PLayer();
        PLayer wireLayer = new PLayer();

        this.nodeLayer = nodeLayer;
        this.edgeLayer = edgeLayer;
        this.wireLayer = wireLayer;

        EventBus.instance().register(this);

        nodeLayer.addInputEventListener(new GraphDragEventHandler(this::updateEdge, this::updateWire));

        addInputEventListener(new PBasicInputEventHandler() {

        });

        getRoot().addChild(edgeLayer);
        getRoot().addChild(wireLayer);
        getCamera().addLayer(0, edgeLayer);
        getCamera().addLayer(wireLayer);

        PInputEventListener[] listeners = getCamera().getInputEventListeners();
        for (PInputEventListener listener : listeners)
            getCamera().removeInputEventListener(listener);

        getCamera().addInputEventListener(new CustomZoomEventHandler());
        getCamera().addInputEventListener(new PPanEventHandler());

        addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseClicked(PInputEvent e) {
                super.mouseClicked(e);
                if(e.getButton() == MouseEvent.BUTTON3) {
                    Point2D d = e.getPosition();
                    spawnX = (float) d.getX();
                    spawnY = (float) d.getY();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                SceneGraphForm.instance.dispatchEvent(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                SceneGraphForm.instance.dispatchEvent(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                SceneGraphForm.instance.dispatchEvent(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton() == MouseEvent.BUTTON3) {
                    new OpenContextMenuEvent(e.getXOnScreen(), e.getYOnScreen()).fire();
                }
            }
        });

//        generateTestNodes();
        graph.addOnAddListener(this::onGraphNodeAdd);
        graph.addOnRemoveListener(this::onGraphNodeRemove);
    }

    public List<NodeComponent> getNodeComponents() {
        return getNodeComponentStream().collect(Collectors.toList());
    }

    public Stream<NodeComponent> getNodeComponentStream() {
        return nodeWrapperMap.values()
                .stream()
                .map(w -> w.nodeComponent);
    }

    public List<PinComponent> getPinComponents() {
        List<PinComponent> pins = new ArrayList<>();
        getNodeComponentStream().forEach(node -> pins.addAll(node.pinComponentList));
        return pins;
    }

    public Stream<Node> getNodeStream() {
        return getNodeComponentStream().map(nodeComponent -> nodeComponent.node);
    }

    public List<Node> getNodes() {
        return getNodeStream().collect(Collectors.toList());
    }

    public Stream<Pin> getPinStream() {
        return getPinComponents().stream().map(c -> c.pin);
    }

    public List<Pin> getPins() {
        return getPinStream().collect(Collectors.toList());
    }

    private void onGraphNodeAdd(Node node) {
        NodeWrapper wrapper = NodeComponent.create(node);

        float x = node.location.x = (spawnX - (wrapper.nodeComponent.getWidth() / 2));
        float y = node.location.y = (spawnY - (wrapper.nodeComponent.getHeight() / 2));

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

        for(int i = 0; i < numNodes; i++) {
            float x = rand.nextInt(width);
            float y = rand.nextInt(height);
            PNode wrapper = PPath.createRectangle(x, y, 20, 30);
            wrapper.addAttribute("edges", new ArrayList<>());
            nodeLayer.addChild(wrapper);
//            nodeWrapperMap.put(node, wrapper);
        }

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

    public Double lerp(double a, double b, float alpha) {
        return a + (b - a) * alpha;
    }

    public Point2D.Double lerp(Point2D.Double start, Point2D.Double end, float alpha) {
        return lerp(start, end, alpha, alpha);
    }
    public Point2D.Double lerp(Point2D.Double start, Point2D.Double end, float xAlpha, float yAlpha) {
        Point2D.Double p = new Point2D.Double();
        p.x = lerp(start.getX(), end.getX(), xAlpha);
        p.y = lerp(start.getY(), end.getY(), yAlpha);
        return p;
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

    private void updateWire(PPath edge) {
        for (Map.Entry<Wire, PPath> entry : wireEdgeMap.entrySet()) {
            if(entry.getValue().equals(edge)) {
                updateWire(entry.getKey(), edge);
                return;
            }
        }
    }

    private void updateWire(Wire wire, PPath edge) {
        PNode node1 = (PNode) ((ArrayList)edge.getAttribute("nodes")).get(0);
        PNode node2 = (PNode) ((ArrayList)edge.getAttribute("nodes")).get(1);


        if(!(node1 instanceof NodeWrapper)) return;
        if(!(node2 instanceof NodeWrapper)) return;

        NodeWrapper w1 = (NodeWrapper) node1;
        NodeWrapper w2 = (NodeWrapper) node2;

        edge.reset();

        PBounds w1Bounds = w1.getFullBoundsReference();
        PBounds w2Bounds = w2.getFullBoundsReference();

        Point2D.Double start = (Point2D.Double) w1Bounds.getCenter2D();
        Point2D.Double end = (Point2D.Double) w2Bounds.getCenter2D();

        start.x -= w1Bounds.getWidth()/2;
        start.y -= w1Bounds.getHeight()/2;

        end.x -= w2Bounds.getWidth()/2;
        end.y -= w2Bounds.getHeight()/2;

        // From top-left of node

        // Get local position as percentage
        Point2D.Float pin1 = w1.nodeComponent.getPercentagePositionOfPin(wire.a);
        Point2D.Float pin2 = w2.nodeComponent.getPercentagePositionOfPin(wire.b);

        // Transform to node scale
        pin1.x *= w1Bounds.getWidth();
        pin1.y *= w1Bounds.getHeight();

        pin2.x *= w2Bounds.getWidth();
        pin2.y *= w2Bounds.getHeight();

        // Append to existing coordinates as an offset
        start.x += pin1.x;
        start.y += pin1.y;

        end.x += pin2.x;
        end.y += pin2.y;


        // Should be positioned on pins by here
        edge.moveTo((float) start.getX(), (float) start.getY());
        if(useSpline) {
            Point2D.Double[] ps = new Point2D.Double[]{
                    lerp(start, end, 0f, 0f),
                    lerp(start, end, 0.25f, 0f),
                    lerp(start, end, 0.5f, 0.5f),
                    lerp(start, end, 0.75f, 1.0f),
                    lerp(start, end, 1.0f, 1.0f)
            };
            edge.setPaint(null);
            edge.setStroke(new BasicStroke(2));
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

    public NodeWrapper getWrapperFromPin(Pin pin) {
        return nodeWrapperMap.get(pin.owningNode);
    }

    @Override
    public void onPinConnect(PinConnectEvent event) {
        NodeWrapper wA = getWrapperFromPin(event.getA());
        NodeWrapper wB = getWrapperFromPin(event.getB());
        PPath edge = PPath.createLine(0, 0, 0, 0);

        ((ArrayList)wA.getAttribute("edges")).add(edge);
        ((ArrayList)wB.getAttribute("edges")).add(edge);
        edge.addAttribute("nodes", new ArrayList());
        ((ArrayList)edge.getAttribute("nodes")).add(wA);
        ((ArrayList)edge.getAttribute("nodes")).add(wB);
        wireLayer.addChild(edge);

        Wire wire = new Wire(event.getA(), event.getB());

        edge.addAttribute("type", "wire");

        updateWire(wire, edge);
        wireEdgeMap.put(wire, edge);
    }

    @Override
    public void onPinDisconnect(PinDisconnectEvent event) {
        NodeWrapper wA = getWrapperFromPin(event.getA());
        NodeWrapper wB = getWrapperFromPin(event.getB());
        Wire wire = new Wire(event.getA(), event.getB());

        PPath edge = wireEdgeMap.get(wire);
        if(edge == null) return;
        ((ArrayList)wA.getAttribute("edges")).remove(edge);
        ((ArrayList)wB.getAttribute("edges")).remove(edge);
        ((ArrayList)edge.getAttribute("nodes")).clear();

        edge.reset();
        wireLayer.removeChild(edge);
    }


    public static class CustomZoomEventHandler extends PZoomEventHandler {
        public CustomZoomEventHandler() {
            super();
            setEventFilter(new PInputEventFilter(InputEvent.BUTTON2_MASK));
        }
    }

    public static class Wire {
        public Pin a;
        public Pin b;

        public Wire(Pin a, Pin b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(!(obj instanceof Wire)) return false;
            Wire w = (Wire) obj;
            if(this.a.equals(w.a) && this.b.equals(w.b))
                return true;

            if(this.a.equals(w.b) && this.b.equals(w.a))
                return true;

            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return a.hashCode()+b.hashCode();
        }
    }

}
