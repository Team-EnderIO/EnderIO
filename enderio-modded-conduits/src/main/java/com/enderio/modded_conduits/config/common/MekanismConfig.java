package com.enderio.modded_conduits.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MekanismConfig {
    public final ModConfigSpec.BooleanValue CAN_MIX_CHEMICAL_CONDUIT_TIERS;

    public MekanismConfig(ModConfigSpec.Builder builder) {
        builder.push("mekanism");

        CAN_MIX_CHEMICAL_CONDUIT_TIERS = builder
            .comment("Whether players can mix and match chemical conduits")
            .define("canMixChemicalConduitTiers", true);

        builder.pop();
    }
}
