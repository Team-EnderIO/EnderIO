package com.enderio.enderio.common.init;

import com.enderio.enderio.common.EnderIO;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ConduitLang {

    // region Conduit Types

    public static final Component ENERGY_CONDUIT = addTranslation("item", EnderIO.rl("conduit.energy"),
            "Energy Conduit");
    public static final Component ENHANCED_ENERGY_CONDUIT = addTranslation("item",
            EnderIO.rl("conduit.enhanced_energy"), "Enhanced Energy Conduit");
    public static final Component ENDER_ENERGY_CONDUIT = addTranslation("item", EnderIO.rl("conduit.ender_energy"),
            "Ender Energy Conduit");
    public static final Component REDSTONE_CONDUIT = addTranslation("item", EnderIO.rl("conduit.redstone"),
            "Redstone Conduit");
    public static final Component FLUID_CONDUIT = addTranslation("item", EnderIO.rl("conduit.fluid"), "Fluid Conduit");
    public static final Component PRESSURIZED_FLUID_CONDUIT = addTranslation("item",
            EnderIO.rl("conduit.pressurized_fluid"), "Pressurized Fluid Conduit");
    public static final Component ENDER_FLUID_CONDUIT = addTranslation("item", EnderIO.rl("conduit.ender_fluid"),
            "Ender Fluid Conduit");
    public static final Component ITEM_CONDUIT = addTranslation("item", EnderIO.rl("conduit.item"), "Item Conduit");
    public static final Component ENHANCED_ITEM_CONDUIT = addTranslation("item", EnderIO.rl("conduit.enhanced_item"),
            "Enhanced Item Conduit");
    public static final Component ENDER_ITEM_CONDUIT = addTranslation("item", EnderIO.rl("conduit.ender_item"),
            "Ender Item Conduit");

    // endregion

    // region Conduit Screen Tooltips

    public static final Component CONDUIT_CHANNEL = addTranslation("gui", EnderIO.rl("conduit_channel"), "Channel");
    public static final Component REDSTONE_CHANNEL = addTranslation("gui", EnderIO.rl("redstone_channel"),
            "Signal Color");

    public static final Component ROUND_ROBIN_ENABLED = addTranslation("gui", EnderIO.rl("round_robin.enabled"),
            "Round Robin Enabled");
    public static final Component ROUND_ROBIN_DISABLED = addTranslation("gui", EnderIO.rl("round_robin.disabled"),
            "Round Robin Disabled");
    public static final Component SELF_FEED_ENABLED = addTranslation("gui", EnderIO.rl("self_feed.enabled"),
            "Self Feed Enabled");
    public static final Component SELF_FEED_DISABLED = addTranslation("gui", EnderIO.rl("self_feed.disabled"),
            "Self Feed Disabled");
    public static final Component FLUID_CONDUIT_CHANGE_FLUID1 = addTranslation("gui",
            EnderIO.rl("fluid_conduit.change_fluid1"), "Locked Fluid:");
    public static final Component FLUID_CONDUIT_CHANGE_FLUID2 = addTranslation("gui",
            EnderIO.rl("fluid_conduit.change_fluid2"), "Click to reset!");
    public static final MutableComponent FLUID_CONDUIT_CHANGE_FLUID3 = addTranslation("gui",
            EnderIO.rl("fluid_conduit.change_fluid3"), "Fluid: %s");

    // endregion

    public static final MutableComponent GRAPH_TICK_RATE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit.debug.tick_rate"), "Network Ticks: %s/sec");

    public static final MutableComponent ENERGY_RATE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit.energy.rate"), "Max Output %s \u00B5I/t");

    public static final MutableComponent FLUID_RAW_RATE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit.fluid.raw_rate"), "Rate: %s mB/network tick");
    public static final MutableComponent FLUID_EFFECTIVE_RATE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit.fluid.effective_rate"), "Effective Rate: %s mB/t");

    public static final Component MULTI_FLUID_TOOLTIP = addTranslation("tooltip", EnderIO.rl("conduit.fluid.multi"),
            "Allows multiple fluids to be transported on the same line");

    public static final MutableComponent ITEM_RAW_RATE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit.item.raw_rate"), "Rate: %s Items/network tick");
    public static final MutableComponent ITEM_EFFECTIVE_RATE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit.item.effective_rate"), "Effective Rate: %s Items/sec");

    public static final Component CONDUIT_ERROR_NO_SCREEN_TYPE = addTranslation("gui",
            EnderIO.rl("conduit.error.no_screen_type"), "Error: No screen type defined");

    public static final Component CONDUIT_INSERT = addTranslation("gui", EnderIO.rl("conduit.insert"), "Insert");
    public static final Component CONDUIT_EXTRACT = addTranslation("gui", EnderIO.rl("conduit.extract"), "Extract");
    public static final Component CONDUIT_INPUT = addTranslation("gui", EnderIO.rl("conduit.input"), "Input");
    public static final Component CONDUIT_OUTPUT = addTranslation("gui", EnderIO.rl("conduit.output"), "Output");
    public static final Component CONDUIT_PRIORITY = addTranslation("gui", EnderIO.rl("conduit.priority"), "Priority");

    // Redstone Conduit
    public static final Component CONDUIT_REDSTONE_SIGNAL_COLOR = addTranslation("gui",
            EnderIO.rl("conduit.redstone.signal_color"), "Signal Color");
    public static final Component CONDUIT_REDSTONE_STRONG_SIGNAL = addTranslation("gui",
            EnderIO.rl("conduit.redstone.strong_signal"), "Strong Signal");

    public static final MutableComponent TRANSPARENT_FACADE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit_facade.transparent"),
            "Transparent: Hides conduits when painted with a translucent block");
    public static final MutableComponent BLAST_RESIST_FACADE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit_facade.blast_resist"), "Hardened: Resists breaking and explosions");

    public static final MutableComponent CONDUIT_PROBE_MODE_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("conduit_probe.mode"), "Mode: %s");
    public static final MutableComponent CONDUIT_PROBE_CONTAINS_COPIED = addTranslation("tooltip",
            EnderIO.rl("conduit_probe.mode.contains_copied"), "Contains copied conduit data:");
    public static final MutableComponent CONDUIT_PROBE_STATE_PROBE = addTranslation("tooltip",
            EnderIO.rl("conduit_probe.state.probe"), "Probe");
    public static final MutableComponent CONDUIT_PROBE_STATE_COPY_PASTE = addTranslation("tooltip",
            EnderIO.rl("conduit_probe.state.copy_paste"), "Copy/Paste");
    public static final MutableComponent CONDUIT_PROBE_MESSAGE_SWITCHED_MODE = addTranslation("gui",
            EnderIO.rl("conduit_probe.message.switched_mode"), "Switched conduit probe mode to %s");
    public static final MutableComponent CONDUIT_PROBE_MESSAGE_COPIED = addTranslation("gui",
            EnderIO.rl("conduit_probe.message.copied"), "Copied data: %s");
    public static final MutableComponent CONDUIT_PROBE_MESSAGE_PASTED = addTranslation("gui",
            EnderIO.rl("conduit_probe.message.pasted"), "Pasted data: %s");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIO.REGILITE.addTranslation(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name,
            String translation) {
        return EnderIO.REGILITE.addTranslation(prefix,
                ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath() + "/" + name), translation);
    }

    public static void register() {
    }
}
