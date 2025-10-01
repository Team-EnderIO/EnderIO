package com.enderio.modded_conduits.client;

import net.neoforged.bus.api.IEventBus;

public interface ConduitClientModule {
    void initialize(IEventBus modEventBus);
}
