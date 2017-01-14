package net.ncguy.graph.runtime.api;

import net.ncguy.graph.runtime.RuntimeType;

/**
 * Created by Guy on 14/01/2017.
 * <br><br>
 * The core interface for an external runtime. implement this class, provide a zero-argument constructor,
 * and reference your compiler / library.
 * <br>
 * Internal systems should take care of the rest
 */
public interface IRuntimeCore {

    /**
     * Should be the same as is declared in the {@link RuntimeMeta} annotation
     * @return The display name of the runtime
     */
    default String name() {
        RuntimeMeta meta = getClass().getAnnotation(RuntimeMeta.class);
        if(meta == null) return "Undefined";
        return meta.name();
    }

    /**
     * Should be the same as is declared in the {@link RuntimeMeta} annotation
     * @return The runtime type
     */
    default RuntimeType type() {
        RuntimeMeta meta = getClass().getAnnotation(RuntimeMeta.class);
        if(meta == null) return RuntimeType.UNDEFINED;
        return meta.type();
    }

    /**
     * Performs a quick check on the defined type to see if a compiler is supported
     * @return Whether the defined type supports a compiler
     */
    default boolean hasCompiler() {
        return type() == RuntimeType.RUNTIME || type() == RuntimeType.COMPILER;
    }

    /**
     * Gets the compiler
     * @param <T> The compiled result type
     * @return A compiler instance, or null if a compiler is not supported
     */
    default <T> IRuntimeCompiler<T> compiler() {
        return null;
    }

    /**
     * Performs a quick check on the defined type to see if a library is supported
     * @return Whether the defined type supports a library
     */
    default boolean hasLibrary() {
        return type() == RuntimeType.RUNTIME || type() == RuntimeType.LIBRARY;
    }

    /**
     * Gets the library
     * @return A library instance, or null if a library is not supported
     */
    default IRuntimeLibrary library() {
        return null;
    }


}
