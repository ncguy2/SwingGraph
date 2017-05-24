package net.ncguy.graph.data;

public abstract class PropertyConstraint<T> {

    public PropertyConstraint() {}

    public abstract void Test(MutableProperty<T> property);

}
