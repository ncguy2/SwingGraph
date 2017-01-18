package net.ncguy.graph.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

/**
 * Created by Guy on 18/01/2017.
 */
public class NetworkClient {

    private static NetworkClient instance;
    public static NetworkClient instance() {
        if (instance == null)
            instance = new NetworkClient();
        return instance;
    }

    public final Client client;

    private NetworkClient() {
        client = new Client();
    }

    public Kryo getKryo() {
        return client.getKryo();
    }

    public void connect(String address, int tcp, int udp) throws IOException {
        client.start();
        client.connect(5000, address, tcp, udp);
    }



}
