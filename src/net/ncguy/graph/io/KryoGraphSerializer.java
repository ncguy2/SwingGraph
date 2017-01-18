package net.ncguy.graph.io;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Guy on 18/01/2017.
 */
public class KryoGraphSerializer extends Serializer<SceneGraph> {

    @Override
    public void write(Kryo kryo, Output output, SceneGraph graph) {
        Set<Node> nodes = graph.nodes;
        output.writeInt(nodes.size());
        nodes.forEach(node -> {
            output.writeString(node.getClass().getCanonicalName());
            output.writeString(node.uuid);
            output.writeString(node.title);
            output.writeFloat(node.location.x);
            output.writeFloat(node.location.y);
            List<Pin> pinList = node.getPinList();
            output.writeInt(pinList.size(), true);
            pinList.forEach(pin -> {
                output.writeBoolean(pin.onLeft);
                output.writeInt(pin.index, true);
                output.writeString(pin.label);
                output.writeBoolean(pin.connected != null);
                if(pin.connected != null) {
                    output.writeAscii(pin.connected.owningNode.uuid);
                    output.writeBoolean(pin.connected.onLeft);
                    output.writeInt(pin.connected.index, true);
                }
            });
        });
    }

    @Override
    public SceneGraph read(Kryo kryo, Input input, Class<SceneGraph> aClass) {
        int nodeCount = input.readInt();
        List<NodeData> nodesData = new ArrayList<>();
        List<PinData> pinsData = new ArrayList<>();
        List<WireData> wiresData = new ArrayList<>();
        for(int i = 0; i < nodeCount; i++) {
            NodeData nodeData = new NodeData();
            nodeData.cls = input.readString();
            nodeData.uuid = input.readString();
            nodeData.title = input.readString();
            nodeData.x = input.readFloat();
            nodeData.y = input.readFloat();
            int pinCount = input.readInt();
            nodeData.pinData = new ArrayList<>();
            if(pinCount > 0) {
                for(int j = 0; j < pinCount; j++) {
                    PinData pinData = new PinData();
                    pinData.onLeft = input.readBoolean();
                    pinData.index = input.readInt();
                    pinData.label = input.readString();
                    if(input.readBoolean()) {
                        WireData wire = new WireData();
                        pinData.wire = wire;
                        pinData.wire.nodeUUID = input.readString();
                        pinData.wire.onLeft = input.readBoolean();
                        pinData.wire.index = input.readInt();
                        wiresData.add(wire);
                    }
                    nodeData.pinData.add(pinData);
                    pinsData.add(pinData);
                }
            }
            nodesData.add(nodeData);
        }
        // TODO rebuild the nodes from the data, connect the pins with the wire data, and push them into the graph
        SceneGraph graph = new SceneGraph();


        return graph;
    }

    public static class NodeData {
        public String cls;
        public String uuid;
        public String title;
        public float x;
        public float y;
        public List<PinData> pinData;
    }

    public static class PinData {
        public boolean onLeft;
        public int index;
        public String label;

        public WireData wire;
    }

    public static class WireData {
        public String nodeUUID;
        public boolean onLeft;
        public int index;
    }

}
