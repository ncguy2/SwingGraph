package net.ncguy.graph.runtimes.hue;

import net.ncguy.graph.runtime.RuntimeType;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.runtime.api.RuntimeMeta;
import net.ncguy.graph.runtimes.hue.compiler.HueCompiler;
import net.ncguy.graph.runtimes.hue.library.HueLibrary;

@RuntimeMeta(name = "Hue", type = RuntimeType.RUNTIME)
public class HueRuntime implements IRuntimeCore {

    public static HueRuntime newestInstance;

    public HueRuntime() {
        newestInstance = this;
    }

    HueCompiler compiler;
    HueLibrary library;

    @Override
    public HueCompiler compiler() {
        if (compiler == null)
            compiler = new HueCompiler();
        return compiler;
    }

    @Override
    public IRuntimeLibrary library() {
        if (library == null)
            library = new HueLibrary();
        return library;
    }
}
