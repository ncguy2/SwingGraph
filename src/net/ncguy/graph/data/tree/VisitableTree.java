package net.ncguy.graph.data.tree;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Guy on 14/01/2017.
 */
public class VisitableTree<T> implements IVisitable<T> {

    private Set<VisitableTree<T>> children = new LinkedHashSet<>();
    private T data;

    public VisitableTree(T data) {
        this.data = data;
    }

    @Override
    public void accept(IVisitor<T> visitor) {
        visitor.visitData(this, this.data());
        children.forEach(c -> c.accept(visitor.visit(c)));
    }

    @Override
    public T data() {
        return data;
    }

    public VisitableTree<T> child(T data) {
        for (VisitableTree<T> child : children)
            if(child.data().equals(data)) return child;
        return child(new VisitableTree<>(data));
    }

    public VisitableTree<T> child(VisitableTree<T> child) {
        children.add(child);
        return child;
    }

}
