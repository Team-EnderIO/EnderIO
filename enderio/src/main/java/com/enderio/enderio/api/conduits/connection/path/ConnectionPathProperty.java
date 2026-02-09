package com.enderio.enderio.api.conduits.connection.path;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a connection path property, and explains how values from an entire path should be aggregated.
 * Provides some helpers for creating integer properties.
 * @param <T> the value type of the property
 */
@ApiStatus.AvailableSince("8.1.0")
public class ConnectionPathProperty<T> {
    private final Function<List<T>, Optional<T>> aggregator;
    private final T defaultValue;

    public ConnectionPathProperty(Function<List<T>, Optional<T>> aggregator, T defaultValue) {
        this.aggregator = aggregator;
        this.defaultValue = defaultValue;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public T aggregate(List<T> values) {
        return aggregator.apply(values).orElse(defaultValue);
    }

    // region Integer Helpers

    public static ConnectionPathProperty<Integer> minInt(int defaultValue) {
        return new ConnectionPathProperty<>(values -> values.stream().min(Integer::compare), defaultValue);
    }

    public static ConnectionPathProperty<Integer> maxInt(int defaultValue) {
        return new ConnectionPathProperty<>(values -> values.stream().max(Integer::compare), defaultValue);
    }

    public static ConnectionPathProperty<Integer> sumInt(int defaultValue) {
        return new ConnectionPathProperty<>(values -> {
            if (values.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(values.stream().reduce(0, Integer::sum));
        }, defaultValue);
    }

    public static ConnectionPathProperty<Integer> avgInt(int defaultValue) {
        return new ConnectionPathProperty<>(values -> {
            if (values.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(values.stream().reduce(0, Integer::sum) / values.size());
        }, defaultValue);
    }

    // endregion

    // region Long Helpers

    public static ConnectionPathProperty<Long> minLong(long defaultValue) {
        return new ConnectionPathProperty<>(values -> values.stream().min(Long::compare), defaultValue);
    }

    public static ConnectionPathProperty<Long> maxLong(long defaultValue) {
        return new ConnectionPathProperty<>(values -> values.stream().max(Long::compare), defaultValue);
    }

    public static ConnectionPathProperty<Long> sumLong(long defaultValue) {
        return new ConnectionPathProperty<>(values -> {
            if (values.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(values.stream().reduce(0L, Long::sum));
        }, defaultValue);
    }

    public static ConnectionPathProperty<Long> avgLong(long defaultValue) {
        return new ConnectionPathProperty<>(values -> {
            if (values.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(values.stream().reduce(0L, Long::sum) / values.size());
        }, defaultValue);
    }

    // endregion
}
