package net.ncguy.graph.data.constraints;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.data.PropertyConstraint;

public class IntegerPropertyConstraint extends PropertyConstraint<Integer> {

    private final int min;
    private final int max;

    public IntegerPropertyConstraint(int min, int max) {
        super();
        this.min = min;
        this.max = max;
    }

    @Override
    public void Test(MutableProperty<Integer> property) {
        Integer current = property.get();
        if(current > this.max)
            property.set(this.max);
        if(current < this.min)
            property.set(this.min);
    }
}
