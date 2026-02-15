package com.enderio.enderio.config.base.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BaseClientConfig {
    public final ModConfigSpec.ConfigValue<Boolean> MACHINE_PARTICLES;
    public final ModConfigSpec.ConfigValue<Integer> SOT_KEY_HOLD_DELAY;

    public BaseClientConfig(ModConfigSpec.Builder builder) {
        builder.push("behavior");
        SOT_KEY_HOLD_DELAY = builder.comment("Ticks before Staff Of Traveling Keybind is considered 'held' for anchor teleports ").define("sotKeyHoldDelayTicks", 8);
        builder.pop();
        builder.push("visual");
        MACHINE_PARTICLES = builder.comment("Enable machine particles").define("machineParticles", true);
        builder.pop();

    }
}
