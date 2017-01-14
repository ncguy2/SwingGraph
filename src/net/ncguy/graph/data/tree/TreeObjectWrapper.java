package net.ncguy.graph.data.tree;

/**
 * Created by Guy on 14/01/2017.
 */
public class TreeObjectWrapper<T> {

    private T object;
    private String label;

    public TreeObjectWrapper(String label) {
        this.label = label;
    }

    public TreeObjectWrapper(T object, String label) {
        this.object = object;
        this.label = label;
    }

    @Override
    public String toString() {
        if(object == null)
            return label;
        return object.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(this.object != null)
            if (this.object.equals(obj))
                return true;
        if (this.label.equals(obj)) return true;
        if (obj instanceof TreeObjectWrapper) {
            TreeObjectWrapper o = (TreeObjectWrapper) obj;
            if(this.object != null) if (this.object.equals(o.object)) return true;
            if (this.label.equals(o.label)) return true;
        }
        return super.equals(obj);
    }

    public T getObject() {
        return object;
    }

    public String getLabel() {
        return label;
    }
}
