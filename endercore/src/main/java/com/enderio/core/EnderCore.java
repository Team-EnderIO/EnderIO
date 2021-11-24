package com.enderio.core;

import com.enderio.core.common.network.EnderNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Mod(EnderCore.MODID)
public class EnderCore {
    public static final @Nonnull String MODID = "endercore";
    public static final @Nonnull String DOMAIN = "endercore";

    public static final Logger LOGGER = LogManager.getLogManager().getLogger(MODID);

    public EnderCore() {
        EnderNetwork.getNetwork();
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(DOMAIN, path);
    }
}
