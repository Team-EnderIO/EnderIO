package com.enderio.enderio.api.conduits.connection.path;

import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;

/**
 * Provides information about the path between two {@link ConduitBlockConnection}s.
 * Contains the length of the path and any properties gathered from {@link com.enderio.enderio.api.conduits.Conduit#collectNodePathProperties(ConduitNode, ConnectionPathPropertyConsumer)}
 */
@ApiStatus.AvailableSince("8.1.0")
public final class ConduitConnectionPath {
    private final ConduitBlockConnection start;
    private final ConduitBlockConnection end;
    private final int length;
    private final Map<ConnectionPathProperty<?>, ?> properties;

    public ConduitConnectionPath(ConduitBlockConnection start, ConduitBlockConnection end, int length, Map<ConnectionPathProperty<?>, ?> properties) {
        this.start = start;
        this.end = end;
        this.length = length;
        this.properties = properties == null || properties.isEmpty() ? Map.of() : Map.copyOf(properties);
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
        return properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T property(ConnectionPathProperty<T> property) {
        Objects.requireNonNull(property);
        return (T) properties.get(property);
    }

    @SuppressWarnings("unchecked")
    public <T> T propertyOrDefault(ConnectionPathProperty<T> property, T defaultValue) {
        Objects.requireNonNull(property);

        if (!properties.containsKey(property)) {
            return defaultValue;
        }

        return (T) properties.get(property);
    }

    public ConduitConnectionPath reverse() {
        return new ConduitConnectionPath(end, start, length, properties);
    }
}
