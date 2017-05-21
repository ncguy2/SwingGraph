package net.ncguy.graph.scene.render;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import net.ncguy.graph.contextmenu.ContextMenuHost;
import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.event.ToastEvent;
import net.ncguy.graph.io.DBGraphSerializer;
import net.ncguy.graph.runtime.RuntimeReserve;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.RuntimeController;
import net.ncguy.graph.scene.SceneConfigForm;
import net.ncguy.graph.scene.ToastForm;
import net.ncguy.graph.scene.components.graph.GraphConfigurationPanel;
import net.ncguy.graph.scene.components.graph.NodeConfigurationPanel;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.render.SceneGraphRenderer;
import net.ncguy.graph.tween.JComponentTweenAccessor;
import net.ncguy.graph.tween.PNodeTweenAccessor;
import org.piccolo2d.PNode;
import org.piccolo2d.extras.pswing.PSwingRepaintManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Guy on 24/10/2016.
 */
public class SceneGraphForm extends JFrame implements ToastEvent.ToastListener {

    public static SceneGraphForm instance;

    SceneConfigForm configForm;

    JSplitPane splitPane;

    JTabbedPane configPane;
    SceneGraph graph;
    SceneGraphRenderer graphRenderer;
    Timer tweenTimer;
    long lastMillis;

    public boolean isAltPressed = false;

    public TweenManager tweenManager;
    public float tweenRateScalar = 1;
    public ContextMenuHost contextHost;

    public Set<JComponent> continuousRenderables;

    public SceneGraphForm() {
        instance = this;
        init();
    }

    public void init() {
        continuousRenderables = new LinkedHashSet<>();
        EventBus.instance().register(this);

        contextHost = new ContextMenuHost();
        RuntimeReserve.instance().populate();

        RepaintManager.setCurrentManager(new PSwingRepaintManager());

        setTitle("Scene graph");
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ALT)
                    isAltPressed = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_ALT)
                    isAltPressed = false;
            }
        });

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new AbstractAction("Graph config") {
            @Override
            public void actionPerformed(ActionEvent e) {
                configForm.setVisible(true);
            }
        });
        fileMenu.addSeparator();
        fileMenu.add(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DBGraphSerializer.save(new File("Working/test.db"), graph);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        JMenu runtimeMenu = new JMenu("Runtimes");
        runtimeMenu.add(new AbstractAction("Configure runtimes") {
            @Override
            public void actionPerformed(ActionEvent e) {
                RuntimeController.instance().setVisible(true);
            }
        });

        Map<String, IRuntimeCore> runtimeMap = RuntimeReserve.instance().runtimeMap;
        JMenu compilerMenu = new JMenu("Compilers");
        runtimeMap.forEach((s, c) -> {
            if(!c.hasCompiler()) return;
            compilerMenu.add(new JMenuItem(new AbstractAction(c.name()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    c.compiler().compile(graph);
                }
            }));
        });

        graph = new SceneGraph();
        graphRenderer = new SceneGraphRenderer(1600, 900, graph);

        configForm = new SceneConfigForm(graphRenderer);

        menuBar.add(fileMenu);
        menuBar.add(runtimeMenu);
        menuBar.add(compilerMenu);

        setJMenuBar(menuBar);


        configPane = new JTabbedPane();
        configPane.add(new GraphConfigurationPanel(graph), "Graph Configuration");
        configPane.add(new NodeConfigurationPanel(graph), "Node Configuration");

        configPane.setMinimumSize(new Dimension(128, 128));
        graphRenderer.setMinimumSize(new Dimension(128, 128));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, configPane, graphRenderer);

        getContentPane().add(splitPane);

        initTweenManager();
    }

    private void initTweenManager() {
        tweenManager = new TweenManager();
        tweenTimer = new Timer(1000 / 60, e -> {
            final long millis = System.currentTimeMillis();
            final long delta = millis - lastMillis;
            lastMillis = millis;

            tweenManager.update((delta/1000f) * tweenRateScalar);
            setTitle("Delta: "+delta/1000f);
        });

//        Tween.registerAccessor(MutableColour.class, new MutableColour(0));
        Tween.registerAccessor(PNode.class, new PNodeTweenAccessor());
        Tween.registerAccessor(JComponent.class, new JComponentTweenAccessor());
        Tween.registerAccessor(JPanel.class, new JComponentTweenAccessor());
        Tween.registerAccessor(ToastForm.class, new ToastForm.ToastTweenAccessor());

        tweenTimer.setRepeats(true);
        lastMillis = System.currentTimeMillis();
        tweenTimer.start();
    }

    public SceneGraph getGraph() {
        return graph;
    }

    public SceneGraphRenderer getGraphRenderer() {
        return graphRenderer;
    }

    @Override
    public void onToast(ToastEvent event) {
        System.out.println(event.getMessage());
        new ToastForm(event);
    }
}
