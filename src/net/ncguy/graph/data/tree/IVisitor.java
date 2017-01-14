package net.ncguy.graph.data.tree;

/**
 * Created by Guy on 14/01/2017.
 */
public interface IVisitor<T> {

    IVisitor<T> visit(IVisitable<T> visitable);
    void visitData(IVisitable<T> visitable, T data);

}
