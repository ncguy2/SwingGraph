package net.ncguy.graph.functions.library;

import net.ncguy.graph.functions.library.factories.IntProviderNode;
import net.ncguy.graph.functions.library.factories.LongProviderNode;
import net.ncguy.graph.functions.library.factories.MethodNode;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FunctionsLibrary implements IRuntimeLibrary {

    @Override
    public List<NodeFactory> getNodeFactories() {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new IntProviderNode.IntProviderFactory());
        factories.add(new LongProviderNode.LongProviderFactory());
        GetNodeFactoriesFromClass(Math.class, factories);
        return factories;
    }

    private void GetNodeFactoriesFromClass(Class cls, List<NodeFactory> factories) {
        for (Method method : cls.getDeclaredMethods()) {
            if(Modifier.isStatic(method.getModifiers()))
                factories.add(new MethodNode.MethodFactory(method, method.getName(), "Functions/"+cls.getSimpleName()));
        }
    }

}
