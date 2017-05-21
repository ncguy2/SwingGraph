package net.ncguy.graph.library;

import net.ncguy.graph.scene.logic.factory.NodeFactory;

public abstract class AudioNodeFactory extends NodeFactory {

    public AudioNodeFactory(Class<? extends AudioNodeFactory> cls) {
        this(cls.getAnnotation(AudioFactory.class));
    }

    public AudioNodeFactory(AudioFactory meta) {
        super(meta.displayName(), meta.category());
    }

    public AudioNodeFactory(String title, String category) {
        super(title, category);
    }

}
