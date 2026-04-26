package com.enderio.enderio.config.base.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BaseClientConfig {
    public final ModConfigSpec.BooleanValue MACHINE_PARTICLES;
    public final ModConfigSpec.IntValue TRAVEL_KEY_HOLD_DELAY;

    public BaseClientConfig(ModConfigSpec.Builder builder) {
        builder.push("behavior");
        TRAVEL_KEY_HOLD_DELAY = builder.comment("Ticks before Staff Of Traveling Keybind is considered 'held' for anchor teleports ").defineInRange("travelKeyHoldDelayTicks", 8, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("visual");
        MACHINE_PARTICLES = builder.comment("Enable machine particles").define("machineParticles", true);
        builder.pop();

    }
}
