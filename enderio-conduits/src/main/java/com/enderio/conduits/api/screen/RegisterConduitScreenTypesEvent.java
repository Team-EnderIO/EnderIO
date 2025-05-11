package com.enderio.conduits.api.screen;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

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
