package com.enderio.enderio.api.conduits.connection.path;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("8.1.0")
public interface ConnectionPathPropertyConsumer {
    /**
     * Collects property values for a connection path (generally when gathering information from a single node).
     * @param property the property to set the value of
     * @param value the value to set
     * @param <T> type of the value
     */
    <T> void accept(ConnectionPathProperty<T> property, T value);
}
