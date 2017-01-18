package net.ncguy.graph.net;

import net.ncguy.graph.net.event.NetEvent;
import net.ncguy.graph.net.event.NetEventMeta;
import org.reflections.Reflections;

import java.util.Comparator;
import java.util.Set;

/**
 * Created by Guy on 18/01/2017.
 */
public class NetworkManager {

    private static NetworkManager instance;
    public static NetworkManager instance() {
        if (instance == null)
            instance = new NetworkManager();
        return instance;
    }

    private NetworkManager() {
        server = NetworkServer.instance();
        client = NetworkClient.instance();
        registerEvents();
    }

    public final NetworkServer server;
    public final NetworkClient client;

    public void registerEvents() {
        Reflections ref = new Reflections("net.ncguy.graph.net");
        Set<Class<?>> set = ref.getTypesAnnotatedWith(NetEventMeta.class);
        set.stream().sorted(Comparator.comparing(Class::getSimpleName)).forEach(cls -> {
            server.getKryo().register(cls);
            client.getKryo().register(cls);
        });
    }

    public void handleNetEvent(NetEvent event) {
        
    }


}
