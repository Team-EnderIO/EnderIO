package com.enderio.enderio.api.conduits.model;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class RegisterConduitModelModifiersEvent extends Event implements IModBusEvent {
    public interface ConduitCoreModelModifierFactory {
        ConduitModelModifier createModifier();
    }

    private final Map<ConduitType<?>, ConduitCoreModelModifierFactory> modifiers = new ConcurrentHashMap<>();

    public void register(ConduitType<? extends Conduit<?, ?>> type, ConduitCoreModelModifierFactory modifierFactory) {
        modifiers.put(type, modifierFactory);
    }

    public Map<ConduitType<?>, ConduitCoreModelModifierFactory> getModifiers() {
        return Map.copyOf(modifiers);
    }
}
