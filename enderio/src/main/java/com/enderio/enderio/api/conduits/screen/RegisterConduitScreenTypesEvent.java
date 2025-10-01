package com.enderio.enderio.api.conduits.screen;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterConduitScreenTypesEvent extends Event implements IModBusEvent {
    private final Map<ConduitType<?>, ConduitScreenType<?>> screenTypes = new ConcurrentHashMap<>();

    public <T extends ConnectionConfig> void register(ConduitType<? extends Conduit<?, T>> conduitType,
            ConduitScreenType<T> screenType) {
        screenTypes.put(conduitType, screenType);
    }

    public Map<ConduitType<?>, ConduitScreenType<?>> getScreenTypes() {
        return Map.copyOf(screenTypes);
    }
}
