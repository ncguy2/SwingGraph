package net.ncguy.graph.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MutableProperty<T> {

    protected String name;
    protected T object;

    protected PropertyConstraint<T> constraint;
    protected List<BiConsumer<T, T>> changeListeners = new ArrayList<>();

    public MutableProperty(String name, T object) {
        this.name = name;
        this.object = object;
    }

    public MutableProperty(String name, T object, PropertyConstraint<T> constraint) {
        this.name = name;
        this.object = object;
        this.constraint = constraint;
    }

    public T get() {
        return object;
    }

    public T set(T object) {
        T old = this.object;
        SetQuick(object);
        if(constraint != null) constraint.Test(this);
        if(old != this.object)
            OnChange(old);
        return this.object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<T> getTypeClass() {
        return (Class<T>) get().getClass();
    }

    public void CastAndSet(Object obj) {
        set((T)obj);
    }

    private void OnChange(final T old) {
        changeListeners.forEach(l -> l.accept(old, MutableProperty.this.object));
    }

    public void AddChangeListener(BiConsumer<T, T> listener) {
        changeListeners.add(listener);
    }

    public void RemoveChangeListener(BiConsumer<T, T> listener) {
        changeListeners.remove(listener);
    }

    public void SetConstraint(PropertyConstraint<T> constraint) {
        this.constraint = constraint;
    }

    /**
     * Sets the property value without notifying listeners or constraints
     * Not recommended to invoke outside of these
     * @param obj
     */
    public void SetQuick(T obj) {
        this.object = obj;
    }

}
