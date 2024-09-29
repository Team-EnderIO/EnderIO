package com.enderio.conduits.common.integrations.cctweaked;

import com.enderio.base.api.integration.Integration;
import dan200.computercraft.api.ComputerCraftAPI;
import net.neoforged.bus.api.IEventBus;

public class CCIntegration implements Integration {

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        ComputerCraftAPI.registerBundledRedstoneProvider(new EIOBundledRedstoneProvider());
    }
}
