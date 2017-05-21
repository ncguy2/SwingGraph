package net.ncguy.graph;

import net.ncguy.graph.library.AudioLibrary;
import net.ncguy.graph.runtime.RuntimeType;
import net.ncguy.graph.runtime.api.IRuntimeCompiler;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.runtime.api.RuntimeMeta;

@RuntimeMeta(name = "Audio", type = RuntimeType.RUNTIME)
public class AudioRuntime implements IRuntimeCore {

    public static AudioRuntime newestInstance;

    public AudioRuntime() {
        newestInstance = this;
    }

    AudioLibrary library;

    @Override
    public IRuntimeCompiler<Void> compiler() {
        return null;
    }

    @Override
    public IRuntimeLibrary library() {
        if (library == null)
            library = new AudioLibrary();
        return library;
    }
}
