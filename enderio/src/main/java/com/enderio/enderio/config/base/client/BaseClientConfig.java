package com.enderio.enderio.config.base.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BaseClientConfig {
    public final ModConfigSpec.BooleanValue MACHINE_PARTICLES;
    public final ModConfigSpec.IntValue TRAVEL_KEY_HOLD_DELAY;
    public final ModConfigSpec.BooleanValue ANCHORS_USE_ICON_AS_BLOCK;
    public final ModConfigSpec.BooleanValue NOSTALGIC_SOUL_NAME_OVERLAY;
    public final ModConfigSpec.BooleanValue SOUL_NAME_OVERLAY_FALLBACK;

    public BaseClientConfig(ModConfigSpec.Builder builder) {
        builder.push("behavior");
        TRAVEL_KEY_HOLD_DELAY = builder.comment("Ticks before Staff Of Traveling Keybind is considered 'held' for anchor teleports ")
            .defineInRange("travelKeyHoldDelayTicks", 8, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("visual");
        MACHINE_PARTICLES = builder.comment("Enable machine particles").define("machineParticles", true);
        ANCHORS_USE_ICON_AS_BLOCK = builder.comment("Whether Travel Anchors try to render items as blocks")
            .define("anchorsUseIconAsBlock", true);
        NOSTALGIC_SOUL_NAME_OVERLAY = builder.comment("Always show the name of the stored mob as scrolling blue text, rather than a small spawn egg.")
            .define("nostalgicSoulNameOverlay", false);
        SOUL_NAME_OVERLAY_FALLBACK = builder.comment("Fall back onto the classic blue text overlay if no spawn egg exists for the stored mob.")
            .define("soulNameOverlayFallback", true);
        builder.pop();
    }
}
