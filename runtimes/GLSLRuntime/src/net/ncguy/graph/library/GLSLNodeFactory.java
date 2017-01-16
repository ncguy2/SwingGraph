package net.ncguy.graph.library;

import net.ncguy.graph.scene.logic.factory.NodeFactory;

/**
 * Created by Guy on 15/01/2017.
 */
public abstract class GLSLNodeFactory extends NodeFactory {

    public GLSLNodeFactory(Class<? extends GLSLNodeFactory> cls) {
        this(cls.getAnnotation(GLSLFactory.class));
    }

    public GLSLNodeFactory(GLSLFactory meta) {
        super(meta.displayName(), meta.category());
    }

    public GLSLNodeFactory(String title, String category) {
        super(title, category);
    }
}
