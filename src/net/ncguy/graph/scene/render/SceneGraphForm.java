package net.ncguy.graph.scene.render;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import net.ncguy.graph.contextmenu.ContextMenuHost;
import net.ncguy.graph.runtime.RuntimeReserve;
import net.ncguy.graph.scene.RuntimeController;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.render.SceneGraphRenderer;
import net.ncguy.graph.tween.MutableColour;
import net.ncguy.graph.tween.PNodeTweenAccessor;
import org.piccolo2d.PNode;
import org.piccolo2d.extras.pswing.PSwingRepaintManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Guy on 24/10/2016.
 */
public class SceneGraphForm extends JFrame {

    public static SceneGraphForm instance;

    SceneGraph graph;
    SceneGraphRenderer graphRenderer;
    Timer tweenTimer;
    long lastMillis;

    public TweenManager tweenManager;
    public float tweenRateScalar = 1;
    public ContextMenuHost contextHost;

    public SceneGraphForm() {
        instance = this;
        init();
    }

    public void init() {
        contextHost = new ContextMenuHost();
        RuntimeReserve.instance().populate();

        RepaintManager.setCurrentManager(new PSwingRepaintManager());

        setTitle("Scene graph");
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new AbstractAction("Add node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                graph.addNode(new Node("Testing"));
            }
        });

        JMenu runtimeMenu = new JMenu("Runtimes");
//        Map<String, IRuntimeCore> coreMap = RuntimeReserve.instance().runtimeMap;
//        if(coreMap.size() <= 0) {
//            JMenuItem item = new JMenuItem("No runtimes found");
//            item.setEnabled(false);
//            runtimeMenu.add(item);
//        }else{
//            coreMap.forEach((s, c) -> {
//                runtimeMenu.add(new AbstractAction(s) {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        System.out.printf("Switching to runtime: %s, \n" +
//                                "\tType: %s\n" +
//                                "\tHas compiler: %s\n" +
//                                "\tHas library: %s\n",
//                                s, c.type(), c.hasCompiler(), c.hasLibrary());
//                    }
//                });
//            });
//        }
        runtimeMenu.add(new AbstractAction("Configure runtimes") {
            @Override
            public void actionPerformed(ActionEvent e) {
                RuntimeController.instance().setVisible(true);
            }
        });

        graph = new SceneGraph();
        graphRenderer = new SceneGraphRenderer(1600, 900, graph);

        menuBar.add(fileMenu);
        menuBar.add(runtimeMenu);

        setJMenuBar(menuBar);

        getContentPane().add(graphRenderer);

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

        Tween.registerAccessor(MutableColour.class, new MutableColour(0));
        Tween.registerAccessor(PNode.class, new PNodeTweenAccessor());

        tweenTimer.setRepeats(true);
        lastMillis = System.currentTimeMillis();
        tweenTimer.start();
    }

    public SceneGraph getGraph() {
        return graph;
    }
}
