package net.ncguy.graph.data.tree;

/**
 * Created by Guy on 14/01/2017.
 */
public interface IVisitable<T> {

    void accept(IVisitor<T> visitor);
    T data();

}
