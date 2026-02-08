package com.enderio.enderio.api.conduits.connection.path;

public interface ConnectionPathPropertyConsumer {
    <T> void accept(ConnectionPathProperty<T> property, T value);
}
