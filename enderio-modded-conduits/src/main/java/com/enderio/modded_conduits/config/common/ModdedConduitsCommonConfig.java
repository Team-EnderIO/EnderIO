package com.enderio.modded_conduits.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModdedConduitsCommonConfig {
    public final AE2Config AE2;
    public final MekanismConfig MEKANISM;

    public ModdedConduitsCommonConfig(ForgeConfigSpec.Builder builder) {
        AE2 = new AE2Config(builder);
        MEKANISM = new MekanismConfig(builder);
    }
}
