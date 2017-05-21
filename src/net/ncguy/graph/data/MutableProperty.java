package net.ncguy.graph.data;

public class MutableProperty<T> {

    protected String name;
    protected T object;

    public MutableProperty(String name, T object) {
        this.name = name;
        this.object = object;
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
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

}
