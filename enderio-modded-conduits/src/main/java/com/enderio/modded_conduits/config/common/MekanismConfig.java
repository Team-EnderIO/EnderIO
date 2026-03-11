package com.enderio.modded_conduits.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class MekanismConfig {
    public final ForgeConfigSpec.BooleanValue CAN_MIX_CHEMICAL_CONDUIT_TIERS;

    public MekanismConfig(ForgeConfigSpec.Builder builder) {
        builder.push("mekanism");

        CAN_MIX_CHEMICAL_CONDUIT_TIERS = builder
            .comment("Whether players can mix and match chemical conduits")
            .define("canMixChemicalConduitTiers", true);

        builder.pop();
    }
}
