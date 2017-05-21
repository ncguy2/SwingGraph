package net.ncguy.graph.runtimes.hue.compiler;

import net.ncguy.graph.data.factories.StartExecNode;
import net.ncguy.graph.data.icons.Icons;
import net.ncguy.graph.event.ToastEvent;
import net.ncguy.graph.runtime.api.IRuntimeCompiler;
import net.ncguy.graph.scene.logic.ExecNode;
import net.ncguy.graph.scene.logic.SceneGraph;

import java.util.concurrent.atomic.AtomicInteger;

public class HueCompiler implements IRuntimeCompiler<Void> {

    @Override
    public Void compile(SceneGraph graph) {
        AtomicInteger threadCount = new AtomicInteger(0);
        graph.nodes.stream()
            .filter(node -> node instanceof StartExecNode)
            .map(node -> (StartExecNode) node)
            .forEach(node -> {
                threadCount.incrementAndGet();
                Thread t = new Thread(() -> CompileNode(node), "Thread-"+node.title);
                t.setDaemon(true);
                t.start();
            });
        new ToastEvent(threadCount.get() + " Hue execution threads started").setImagePath(Icons.Icon.INFO_WHITE).fire();
        return null;
    }

    public void CompileNode(ExecNode rootNode) {
        rootNode.Next().ifPresent(this::CompileNode);
    }

}
