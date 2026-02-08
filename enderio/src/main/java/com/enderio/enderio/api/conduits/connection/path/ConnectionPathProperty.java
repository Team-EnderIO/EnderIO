package com.enderio.enderio.api.conduits.connection.path;

import java.util.List;
import java.util.function.Function;

public class ConnectionPathProperty<T> {
    private final Function<List<T>, T> aggregator;

    public ConnectionPathProperty(Function<List<T>, T> aggregator) {
        this.aggregator = aggregator;
    }

    public static ConnectionPathProperty<Integer> minInt() {
        return new ConnectionPathProperty<>(values -> values.stream().min(Integer::compare).orElse(null));
    }

    public static ConnectionPathProperty<Integer> maxInt() {
        return new ConnectionPathProperty<>(values -> values.stream().max(Integer::compare).orElse(null));
    }

    public static ConnectionPathProperty<Integer> sumInt() {
        return new ConnectionPathProperty<>(values -> values.stream().reduce(0, Integer::sum));
    }

    public static ConnectionPathProperty<Integer> avgInt() {
        return new ConnectionPathProperty<>(values -> values.stream().reduce(0, Integer::sum) / values.size());
    }

    public T aggregate(List<T> values) {
        return aggregator.apply(values);
    }
}
