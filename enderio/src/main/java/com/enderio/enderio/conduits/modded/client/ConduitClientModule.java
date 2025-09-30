package com.enderio.enderio.conduits.modded.client;

import net.neoforged.bus.api.IEventBus;

public interface ConduitClientModule {
    void initialize(IEventBus modEventBus);
}
