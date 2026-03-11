package com.enderio.modded_conduits.client;

import net.minecraftforge.eventbus.api.IEventBus;

public interface ConduitClientModule {
    void initialize(IEventBus modEventBus);
}
