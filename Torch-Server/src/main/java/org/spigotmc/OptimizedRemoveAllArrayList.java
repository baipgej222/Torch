package org.spigotmc;

import org.spigotmc.OptimizedRemoveAllArrayList.Marker;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Improved algorithim for bulk removing entries from a list
 *
 * WARNING: This system only works on Identity Based lists,
 * unlike traditional .removeAll() that operates on object equality.
 */
public class OptimizedRemoveAllArrayList <T extends Marker> extends ArrayList<T> {
    public OptimizedRemoveAllArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public OptimizedRemoveAllArrayList() {
    }

    public OptimizedRemoveAllArrayList(Collection<? extends T> c) {
        super(c);
    }

    public OptimizedRemoveAllArrayList<T> clone() {
        return new OptimizedRemoveAllArrayList<T>(this);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T t : c) {
            t.setRemovalState(false);
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(T t) {
        t.setRemovalState(false);
        return super.add(t);
    }

    @Override
    public boolean remove(Object o) {
        ((Marker) o).setRemovalState(true);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c != null) {
            for (Object o : c) {
                ((Marker) o).setRemovalState(true);
            }
        }

        int size = size();
        int insertAt = 0;

        for (int i = 0; i < size; i++) {
            T el = get(i);

            if (el != null && !el.isToBeRemoved()) {
                set(insertAt++, el);
            }
        }
        subList(insertAt, size).clear();

        return size() != size;
    }

    public interface Marker {
        boolean isToBeRemoved();
        void setRemovalState(boolean removalState);
    }
}