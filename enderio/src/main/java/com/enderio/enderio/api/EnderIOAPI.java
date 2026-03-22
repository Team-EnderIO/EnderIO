package com.enderio.enderio.api;

import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.ApiStatus;

public class EnderIOAPI {
    public static final String MOD_ID = "enderio";

    @ApiStatus.AvailableSince("8.2.0")
    public static final ResourceKey<CreativeModeTab> MAIN_CREATIVE_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, EnderIOAPI.rl("enderio"));

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
