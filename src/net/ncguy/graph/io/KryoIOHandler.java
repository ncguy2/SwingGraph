package net.ncguy.graph.io;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.SceneGraph;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

/**
 * Created by Guy on 18/01/2017.
 */
public class KryoIOHandler {

    private String path;
    private Kryo kryo;

    public KryoIOHandler(String path) {
        this.path = path;
        this.kryo = new Kryo();
    }

    public void save(SceneGraph graph) throws FileNotFoundException {
        Set<Node> nodes = graph.nodes;
        Output output = new Output(new FileOutputStream(this.path));



        output.close();
    }

    public void load(SceneGraph graph) {
        Set<Node> nodes = graph.nodes;
        nodes.clear();
    }

    public KryoIOHandler setFile(String path) {
        this.path = path;
        return this;
    }

}
