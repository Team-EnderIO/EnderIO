package com.enderio.api.conduit.screen;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterConduitScreenExtensionsEvent extends Event implements IModBusEvent {
    public interface ConduitScreenExtensionFactory<T extends ConduitData<T>> {
        ConduitScreenExtension<T> createExtension();
    }

    private final Map<ConduitType<?, ?, ?>, ConduitScreenExtensionFactory<?>> extensions = new ConcurrentHashMap<>();

    public <T extends ConduitData<T>> void register(ConduitType<?, ?, T> type, ConduitScreenExtensionFactory<T> extensionFactory) {
        extensions.put(type, extensionFactory);
    }

    public Map<ConduitType<?, ?, ?>, ConduitScreenExtensionFactory<?>> getExtensions() {
        return Map.copyOf(extensions);
    }
}
