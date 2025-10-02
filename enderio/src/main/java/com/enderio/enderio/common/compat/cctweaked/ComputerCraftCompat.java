package com.enderio.enderio.common.compat.cctweaked;

import dan200.computercraft.api.ComputerCraftAPI;

public class ComputerCraftCompat {

    public static void init() {
        ComputerCraftAPI.registerBundledRedstoneProvider(new EIOBundledRedstoneProvider());
    }
}
