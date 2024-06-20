package com.enderio.api.conduit.model;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterConduitCoreModelModifiersEvent extends Event implements IModBusEvent {
    public interface ConduitCoreModelModifierFactory<T extends ConduitData<T>> {
        ConduitCoreModelModifier<T> createModifier();
    }

    private final Map<ConduitType<?>, ConduitCoreModelModifierFactory<?>> modifiers = new ConcurrentHashMap<>();

    public <T extends ConduitData<T>> void register(ConduitType<T> type, ConduitCoreModelModifierFactory<T> modifierFactory) {
        modifiers.put(type, modifierFactory);
    }

    public Map<ConduitType<?>, ConduitCoreModelModifierFactory<?>> getModifiers() {
        return Map.copyOf(modifiers);
    }
}
