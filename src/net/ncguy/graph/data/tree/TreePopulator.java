package net.ncguy.graph.data.tree;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Guy on 14/01/2017.
 */
public class TreePopulator {

    public static <T> void populate(VisitableTree<TreeObjectWrapper<T>> tree, List<T> data, String delim,
                                    Function<T, String> stringLoc) {

        populate(tree, data, delim, stringLoc, false);
    }

    public static <T> void populate(VisitableTree<TreeObjectWrapper<T>> tree, List<T> data, String delim,
                                    Function<T, String> stringLoc, boolean fullPath) {
        BiFunction<T, String, TreeObjectWrapper<T>> nodeLoc = (t, s) -> {
            String[] split = s.split(delim);
            String str = split[split.length-1];
            if(s.equals(stringLoc.apply(t))) return new TreeObjectWrapper<>(t, str);
            return new TreeObjectWrapper<>(str);
        };
        populate(tree, data, delim, stringLoc, nodeLoc, fullPath);
    }

    public static <T> void populate(VisitableTree<TreeObjectWrapper<T>> tree, List<T> data, String delim,
                                    Function<T, String> stringLoc, BiFunction<T, String, TreeObjectWrapper<T>> nodeLoc) {
        populate(tree, data, delim, stringLoc, nodeLoc, false);
    }

    public static <T> void populate(VisitableTree<TreeObjectWrapper<T>> tree, List<T> data, String delim,
                                    Function<T, String> stringLoc, BiFunction<T, String, TreeObjectWrapper<T>> nodeLoc,
                                    boolean fullPath) {
        VisitableTree<TreeObjectWrapper<T>> current = tree;
        for(T t : data) {
            VisitableTree<TreeObjectWrapper<T>> root = current;
            String pathStr = stringLoc.apply(t);
            String[] path = pathStr.split(delim);
            String p = "";
            for (String cat : path) {
                if(fullPath) {
                    if(p.length() == 0) p = cat;
                    else p += delim + cat;
                }else p = cat;
                current = current.child(nodeLoc.apply(t, p));
            }
            current = root;
        }
    }

}
