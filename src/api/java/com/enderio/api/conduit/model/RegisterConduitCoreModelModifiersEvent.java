package com.enderio.api.conduit.model;

import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterConduitCoreModelModifiersEvent extends Event implements IModBusEvent {
    public interface ConduitCoreModelModifierFactory {
        ConduitCoreModelModifier createModifier();
    }

    private final Map<ConduitType<?>, ConduitCoreModelModifierFactory> modifiers = new ConcurrentHashMap<>();

    public void register(ConduitType<? extends Conduit<?>> type, ConduitCoreModelModifierFactory modifierFactory) {
        modifiers.put(type, modifierFactory);
    }

    public Map<ConduitType<?>, ConduitCoreModelModifierFactory> getModifiers() {
        return Map.copyOf(modifiers);
    }
}
