package com.enderio.api.conduit.model;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.Conduit;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterConduitCoreModelModifiersEvent extends Event implements IModBusEvent {
    public interface ConduitCoreModelModifierFactory<T extends ConduitData<T>> {
        ConduitCoreModelModifier<T> createModifier();
    }

    private final Map<Conduit<?, ?, ?>, ConduitCoreModelModifierFactory<?>> modifiers = new ConcurrentHashMap<>();

    public <T extends ConduitData<T>> void register(Conduit<?, ?, T> type, ConduitCoreModelModifierFactory<T> modifierFactory) {
        modifiers.put(type, modifierFactory);
    }

    public Map<Conduit<?, ?, ?>, ConduitCoreModelModifierFactory<?>> getModifiers() {
        return Map.copyOf(modifiers);
    }
}
