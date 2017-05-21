package net.ncguy.graph.functions.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.functions.FunctionsRuntime;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MethodNode extends Node {

    private final Method method;
    MutableProperty[] props;

    public MethodNode(Method method, SceneGraph graph, String title) {
        super(graph, title);
        this.method = method;

        Class<?> rType = this.method.getReturnType();
        if(rType != Void.TYPE) {
            addPin(new Pin(this, "Value", false));
        }
        Parameter[] params = this.method.getParameters();
        props = new MutableProperty[params.length];
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            props[i] = new MutableProperty(param.getName(), null);
            addPin(new Pin(this, param.getName(), true), props[i]);
        }
    }

    @Override
    public Object GetValueFromOutputPin(Pin pin) {

        Object[] args = new Object[props.length];
        for (int i = 0; i < props.length; i++)
            args[i] = props[i].get();

        try {
            return this.method.invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public IRuntimeCore runtime() {
        return FunctionsRuntime.newestInstance;
    }

    public static class MethodFactory extends NodeFactory {

        private final Method method;

        public MethodFactory(Method method, String title, String category) {
            super(title, category);
            this.method = method;
        }

        @Override
        public Node buildNode(SceneGraph graph) {
            return new MethodNode(method, graph, method.getName());
        }
    }

}
