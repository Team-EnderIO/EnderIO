package com.enderio.base.api.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinList<T> {
    private final List<T> items = new CopyOnWriteArrayList<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public void add(T item) {
        if (!items.contains(item)) {
            items.add(item);
        }
    }


    public boolean remove(T item) {
        boolean removed = items.remove(item);
        if (removed && currentIndex.get() >= items.size()) {
            currentIndex.set(0);
        }

        return removed;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    public Iterable<T> iterate() {
        return () -> new Iterator<T>() {
            private int visited = 0;
            private final int size = items.size();

            @Override
            public boolean hasNext() {
                return visited < size && size > 0;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                int index = currentIndex.getAndUpdate(i -> (i + 1) % size);
                visited++;

                return items.get(index);
            }
        };
    }
}
