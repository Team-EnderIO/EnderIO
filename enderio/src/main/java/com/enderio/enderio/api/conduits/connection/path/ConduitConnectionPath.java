package com.enderio.enderio.api.conduits.connection.path;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Provides information about the path between two {@link ConduitBlockConnection}s.
 * Contains the length of the path and any properties gathered from {@link com.enderio.enderio.api.conduits.Conduit#collectNodePathProperties(ConduitNode, ConnectionPathPropertyConsumer)}
 */
@ApiStatus.AvailableSince("8.1.0")
public final class ConduitConnectionPath {
    private final ConduitBlockConnection start;
    private final ConduitBlockConnection end;
    private final int length;
    private volatile Map<ConnectionPathProperty<?>, ?> properties;
    private volatile Supplier<? extends Map<ConnectionPathProperty<?>, ?>> propertiesSupplier;

    public ConduitConnectionPath(ConduitBlockConnection start, ConduitBlockConnection end, int length, Map<ConnectionPathProperty<?>, ?> properties) {
        this.start = start;
        this.end = end;
        this.length = length;
        this.properties = properties == null || properties.isEmpty() ? Map.of() : Map.copyOf(properties);
    }

    /**
     * Create a path whose properties are only computed when first needed.
     * Aggregating properties along a long path is expensive, and most paths never have their properties read.
     * The supplier is invoked at most once, on the thread that first reads the properties.
     */
    @ApiStatus.Internal
    public ConduitConnectionPath(ConduitBlockConnection start, ConduitBlockConnection end, int length, Supplier<? extends Map<ConnectionPathProperty<?>, ?>> propertiesSupplier) {
        this.start = start;
        this.end = end;
        this.length = length;
        this.propertiesSupplier = propertiesSupplier;
    }

    public ConduitBlockConnection start() {
        return start;
    }

    public ConduitBlockConnection end() {
        return end;
    }

    public int length() {
        return length;
    }

    public Map<ConnectionPathProperty<?>, ?> properties() {
        var supplier = propertiesSupplier;
        if (supplier != null) {
            var computed = supplier.get();
            properties = computed == null || computed.isEmpty() ? Map.of() : Map.copyOf(computed);
            propertiesSupplier = null;
        }

        return properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T property(ConnectionPathProperty<T> property) {
        Objects.requireNonNull(property);

        var props = properties();
        if (!props.containsKey(property)) {
            return property.defaultValue();
        }

        return (T) props.get(property);
    }

    public ConduitConnectionPath reverse() {
        if (propertiesSupplier != null) {
            // Share the lazily computed properties rather than forcing them.
            return new ConduitConnectionPath(end, start, length, this::properties);
        }

        return new ConduitConnectionPath(end, start, length, properties);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ConduitConnectionPath that)) {
            return false;
        }

        return length == that.length
            && Objects.equals(start, that.start)
            && Objects.equals(end, that.end)
            && Objects.equals(properties(), that.properties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, length, properties());
    }
}
