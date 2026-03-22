package com.enderio.enderio.config.conduits.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConduitsCommonConfig {
    public final ModConfigSpec.BooleanValue CAN_MIX_ENERGY_CONDUIT_TIERS;
    public final ModConfigSpec.BooleanValue CAN_MIX_FLUID_CONDUIT_TIERS;
    public final ModConfigSpec.BooleanValue CAN_MIX_ITEM_CONDUIT_TIERS;

    public ConduitsCommonConfig(ModConfigSpec.Builder builder) {
        builder.push("conduits");

        CAN_MIX_ENERGY_CONDUIT_TIERS = builder
            .comment("Whether players can mix and match energy conduits")
            .define("canMixEnergyConduitTiers", true);

        CAN_MIX_FLUID_CONDUIT_TIERS = builder
            .comment("Whether players can mix and match fluid conduits")
            .define("canMixFluidConduitTiers", true);

        CAN_MIX_ITEM_CONDUIT_TIERS = builder
            .comment("Whether players can mix and match item conduits")
            .define("canMixItemConduitTiers", true);

        builder.pop();
    }
}
