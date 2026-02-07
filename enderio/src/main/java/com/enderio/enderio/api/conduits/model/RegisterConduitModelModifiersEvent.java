package com.enderio.enderio.api.conduits.model;

import com.enderio.enderio.api.conduits.ConduitType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterConduitModelModifiersEvent extends Event implements IModBusEvent {
    public interface ConduitCoreModelModifierFactory {
        ConduitModelModifier createModifier();
    }

    private final Map<ConduitType<?, ?>, ConduitCoreModelModifierFactory> modifiers = new ConcurrentHashMap<>();

    public void register(ConduitType<?, ?> type, ConduitCoreModelModifierFactory modifierFactory) {
        modifiers.put(type, modifierFactory);
    }

    public Map<ConduitType<?, ?>, ConduitCoreModelModifierFactory> getModifiers() {
        return Map.copyOf(modifiers);
    }
}
