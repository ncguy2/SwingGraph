package net.ncguy.graph.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 * Created by Guy on 18/01/2017.
 */
public class NetworkServer {

    private static NetworkServer instance;
    public static NetworkServer instance() {
        if (instance == null)
            instance = new NetworkServer();
        return instance;
    }

    public final Server server;
    int tcp;
    int udp;

    private NetworkServer() {
        server = new Server();
    }

    public Kryo getKryo() {
        return server.getKryo();
    }

    public void start() throws IOException {
        server.start();
        server.bind(tcp, udp);
    }

    public NetworkServer bindTcp(int tcpPort) {
        this.tcp = tcpPort;
        return this;
    }
    public NetworkServer bindUdp(int udpPort) {
        this.udp = udpPort;
        return this;
    }
    public NetworkServer bind(int tcp, int udp) {
        this.tcp = tcp;
        this.udp = udp;
        return this;
    }

}
