package com.enderio.endergy.common.lang;

import com.enderio.endergy.EnderIOEndergy;
import net.minecraft.network.chat.Component;

public class EndergyConduitComponents {
    public static final Component STELLAR_ENERGY = create("stellar_energy");

    private static Component create(String key) {
        return Component.translatable("item." + EnderIOEndergy.MOD_ID + ".conduit." + key);
    }
}
