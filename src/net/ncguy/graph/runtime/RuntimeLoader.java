package net.ncguy.graph.runtime;

import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtime.api.RuntimeMeta;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Guy on 14/01/2017.
 */
public class RuntimeLoader {

    private static RuntimeLoader instance;
    public static RuntimeLoader instance() {
        if (instance == null)
            instance = new RuntimeLoader();
        return instance;
    }

    private Reflections reflections;

    private RuntimeLoader() {
        ConfigurationBuilder cfgBuilder = new ConfigurationBuilder();
        cfgBuilder.addClassLoader(RuntimeCartographer.getInstance().getClassLoader());
//        cfgBuilder.setClassLoaders(new ClassLoader[]{RuntimeCartographer.getInstance().getClassLoader()});
        cfgBuilder.addUrls(RuntimeCartographer.getInstance().getUrls());
        reflections = new Reflections(cfgBuilder);
    }

    public void loadRuntimes() {
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(RuntimeMeta.class);
        if(set.size() <= 0) {
            System.err.println("No types annotated with @RuntimeMeta found");
            return;
        }
        final Set<IRuntimeCore> cores = new LinkedHashSet<>();
        set.forEach(cls -> {
            if(IRuntimeCore.class.isAssignableFrom(cls)) {
                try {
                    Constructor<?> ctor = cls.getConstructor();
                    if (ctor != null) {
                        Object o = ctor.newInstance();
                        if(o instanceof IRuntimeCore)
                            cores.add((IRuntimeCore) o);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }else{
                System.err.println(cls+" does not implement IRuntimeCore, ignoring...");
            }
        });
        cores.forEach(RuntimeReserve.instance()::addRuntime);
    }

}
