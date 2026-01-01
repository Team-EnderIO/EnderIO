package com.enderio.enderio.foundation.lang;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;

public class EIOCommonLang {

    public static final MutableComponent CREATIVE_TAB_TITLE = create("itemGroup", "enderio");

    public static final MutableComponent TOOLTIP_ENERGY_EQUIVALENCE = tooltip("energy_equivalence");

    public static final MutableComponent BLOCK_BLAST_RESISTANT = TooltipUtil
            .style(tooltip("block/blast_resistant"));

    public static final Component REDSTONE_MODE = gui("redstone_mode");

    public static final MutableComponent TANK_EMPTY_STRING = tooltip("fluid_tank/empty_tooltip");

    // [amount]/[capacity] mb of [FluidName]
    public static final MutableComponent FLUID_TANK_TOOLTIP = tooltip("fluid_tank/contents_tooltip");

    public static final MutableComponent ENERGY_AMOUNT = create("energy", "micro_infinity");

    public static final Component VISIBLE = gui("visible");
    public static final Component NOT_VISIBLE = gui("not_visible");

    public static final MutableComponent TOOLTIP_NO_SOULBOUND = tooltip("no_soul_bound");

    // TODO: Separate lore lang?
    public static final Component SUSPICIOUS_SEED_LORE = tooltip("lore/suspicious_seed").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
    public static final Component SHOW_DETAIL_TOOLTIP = tooltip("show_advanced_tooltip").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);

    // IO Config
    public static final Component IOCONFIG = gui("ioconfig");
    public static final Component TOGGLE_NEIGHBOUR = gui("ioconfig/toggle_neighbours");

    public static final Component PUSH = gui("ioconfig/push");
    public static final Component PULL = gui("ioconfig/pull");
    public static final Component BOTH = gui("ioconfig/both");
    public static final Component DISABLED = gui("ioconfig/disabled");
    public static final Component NONE = gui("ioconfig/none");

    // region Grinding balls

    public static final MutableComponent GRINDINGBALL_MAIN_OUTPUT = tooltip("grinding_ball_main_output");
    public static final MutableComponent GRINDINGBALL_BONUS_OUTPUT = tooltip("grinding_ball_bonus_output");
    public static final MutableComponent GRINDINGBALL_POWER_USE = tooltip("grinding_ball_power_use");

    // endregion

    // region Temp

    // TODO: Need to move these around - just want to get rid of Regilite first.

    public static final Component DARK_STEEL_LADDER_FASTER = TooltipUtil.style(tooltip("dark_steel_ladder.faster"));

    public static final MutableComponent TOO_MANY_LEVELS = message("too_many_levels");

    // endregion

    private static MutableComponent gui(String path) {
        return create("gui", path);
    }

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent message(String path) {
        return create("message", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.id(path)));
    }
}
