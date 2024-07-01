package com.enderio.api.conduit.screen;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.Conduit;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterConduitScreenExtensionsEvent extends Event implements IModBusEvent {
    public interface ConduitScreenExtensionFactory<T extends ConduitData<T>> {
        ConduitScreenExtension<T> createExtension();
    }

    private final Map<Conduit<?, ?, ?>, ConduitScreenExtensionFactory<?>> extensions = new ConcurrentHashMap<>();

    public <T extends ConduitData<T>> void register(Conduit<?, ?, T> type, ConduitScreenExtensionFactory<T> extensionFactory) {
        extensions.put(type, extensionFactory);
    }

    public Map<Conduit<?, ?, ?>, ConduitScreenExtensionFactory<?>> getExtensions() {
        return Map.copyOf(extensions);
    }
}
