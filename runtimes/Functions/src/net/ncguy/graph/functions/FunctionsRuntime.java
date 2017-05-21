package net.ncguy.graph.functions;

import net.ncguy.graph.functions.library.FunctionsLibrary;
import net.ncguy.graph.runtime.RuntimeType;
import net.ncguy.graph.runtime.api.IRuntimeCore;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.runtime.api.RuntimeMeta;

@RuntimeMeta(name = "Functions", type = RuntimeType.LIBRARY)
public class FunctionsRuntime implements IRuntimeCore {

    public static IRuntimeCore newestInstance;

    public FunctionsRuntime() {
        newestInstance = this;
    }

    FunctionsLibrary library;

    @Override
    public IRuntimeLibrary library() {
        if (library == null)
            library = new FunctionsLibrary();
        return library;
    }
}
