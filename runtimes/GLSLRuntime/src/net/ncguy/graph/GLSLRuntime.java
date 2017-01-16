package net.ncguy.graph;

import net.ncguy.graph.compiler.GLSLCompiler;
import net.ncguy.graph.library.GLSLLibrary;
import net.ncguy.graph.runtime.RuntimeType;
import net.ncguy.graph.runtime.api.IRuntimeCompiler;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.runtime.api.RuntimeMeta;

/**
 * Created by Guy on 14/01/2017.
 */
@RuntimeMeta(name = "GLSL", type = RuntimeType.RUNTIME)
public class GLSLRuntime implements IRuntimeCore {

    public static GLSLRuntime newestInstance;

    public GLSLRuntime() {
        newestInstance = this;
    }

    GLSLLibrary library;
    GLSLCompiler compiler;

    @Override
    public IRuntimeLibrary library() {
        if (library == null)
            library = new GLSLLibrary();
        return library;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IRuntimeCompiler<String> compiler() {
        if (compiler == null)
            compiler = new GLSLCompiler();
        return compiler;
    }
}
