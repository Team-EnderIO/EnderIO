package com.enderio.enderio.content.conduits;

import com.enderio.enderio.EnderIO;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConduitLang {

    // region Common Gui Labels

    // TODO: Common GUI labels should probably be exposed in the API for other screen types to consume.
    public static final MutableComponent CHANNEL = gui("channel");
    public static final MutableComponent REDSTONE_CHANNEL = gui("redstone_channel");
    public static final MutableComponent ROUND_ROBIN_ENABLED = gui("round_robin/enabled");
    public static final MutableComponent ROUND_ROBIN_DISABLED = gui("round_robin/disabled");
    public static final MutableComponent SELF_FEED_ENABLED = gui("self_feed/enabled");
    public static final MutableComponent SELF_FEED_DISABLED = gui("self_feed/disabled");

    public static final MutableComponent INSERT = gui("insert");
    public static final MutableComponent EXTRACT = gui("extract");
    public static final MutableComponent INPUT = gui("input");
    public static final MutableComponent OUTPUT = gui("output");
    public static final MutableComponent PRIORITY = gui("priority");

    public static final MutableComponent ERROR_NO_SCREEN_TYPE = gui("error/no_screen_type");

    // endregion

    // region Fluid Gui Labels

    public static final MutableComponent FLUID_CONDUIT_CHANGE_FLUID1 = gui("fluid/change_fluid1");
    public static final MutableComponent FLUID_CONDUIT_CHANGE_FLUID2 = gui("fluid/change_fluid2");
    public static final MutableComponent FLUID_CONDUIT_CHANGE_FLUID3 = gui("fluid/change_fluid3");

    // endregion

    // region Redstone Gui Labels

    public static final MutableComponent REDSTONE_CONDUIT_SIGNAL_COLOR = gui("redstone/signal_color");
    public static final MutableComponent REDSTONE_CONDUIT_STRONG_SIGNAL = gui("redstone/strong_signal");

    // endregion

    // region Conduit Tooltips

    public static final MutableComponent GRAPH_TICK_RATE_TOOLTIP = tooltip("graph_tick_rate");
    public static final MutableComponent ENERGY_RATE_TOOLTIP = tooltip("energy/rate");
    public static final MutableComponent FLUID_RAW_RATE_TOOLTIP = tooltip("fluid/raw_rate");
    public static final MutableComponent FLUID_EFFECTIVE_RATE_TOOLTIP = tooltip("fluid/effective_rate");
    public static final MutableComponent MULTI_FLUID_TOOLTIP = tooltip("fluid/multi");
    public static final MutableComponent ITEM_RAW_RATE_TOOLTIP = tooltip("item/raw_rate");
    public static final MutableComponent ITEM_EFFECTIVE_RATE_TOOLTIP = tooltip("item/effective_rate");

    // endregion

    // region Facade Tooltips

    public static final MutableComponent TRANSPARENT_FACADE_TOOLTIP = tooltip("facade/transparent");
    public static final MutableComponent BLAST_RESIST_FACADE_TOOLTIP = tooltip("facade/blast_resist");

    // endregion

    // region Probe

    public static final MutableComponent CONDUIT_PROBE_MODE_TOOLTIP = tooltip("probe/mode");
    public static final MutableComponent CONDUIT_PROBE_STATE_PROBE = tooltip("probe/probe");
    public static final MutableComponent CONDUIT_PROBE_STATE_COPY_PASTE = tooltip("probe/copy_paste");
    public static final MutableComponent CONDUIT_PROBE_CONTAINS_COPIED = tooltip("probe/mode/contains_copied");
    public static final MutableComponent CONDUIT_PROBE_MESSAGE_SWITCHED_MODE = chat("probe/switched_mode");
    public static final MutableComponent CONDUIT_PROBE_MESSAGE_COPIED = chat("probe/copied");
    public static final MutableComponent CONDUIT_PROBE_MESSAGE_PASTED = chat("probe/pasted");

    // endregion

    private static MutableComponent gui(String path) {
        return create("gui", path);
    }

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent chat(String path) {
        return create("chat", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.rl("conduit/" + path)));
    }
}
