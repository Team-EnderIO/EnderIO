package com.enderio.modconduits.client;

import net.neoforged.bus.api.IEventBus;

public interface ConduitClientModule {
    void initialize(IEventBus modEventBus);
}
