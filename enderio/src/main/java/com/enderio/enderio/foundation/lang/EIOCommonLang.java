package com.enderio.enderio.foundation.lang;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.glass.GlassLighting;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Locale;

public class EIOCommonLang {

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

    private static MutableComponent gui(String path) {
        return create("gui", path);
    }

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.rl(path)));
    }

    // region Items

    public static final Component DARK_STEEL_LADDER_FASTER = TooltipUtil
            .style(addTranslation("tooltip", EnderIO.rl("dark_steel_ladder.faster"), "Faster than regular ladders"));

    public static final Component TOO_MANY_LEVELS = addTranslation("info", EnderIO.rl("too_many_levels"),
            "You have more than 21862 levels, that's too much XP.");

    // endregion



    // region Grinding balls

    public static final MutableComponent GRINDINGBALL_MAIN_OUTPUT = addTranslation("tooltip",
            EnderIO.rl("grinding_ball_main_output"), "Main Output %s%%");
    public static final MutableComponent GRINDINGBALL_BONUS_OUTPUT = addTranslation("tooltip",
            EnderIO.rl("grinding_ball_bonus_output"), "Bonus Output %s%%");
    public static final MutableComponent GRINDINGBALL_POWER_USE = addTranslation("tooltip",
            EnderIO.rl("grinding_ball_power_use"), "Power Use %s%%");

    // endregion

    // region GUI

    public static final Component FILTER_ALLOW_LIST = addTranslation("gui", EnderIO.rl("filter.allow_list"),
            "Allow List");
    public static final Component FILTER_DENY_LIST = addTranslation("gui", EnderIO.rl("filter.deny_list"),
            "Deny List");
    public static final Component FILTER_MATCH_COMPONENTS = addTranslation("gui",
            EnderIO.rl("filter.match_components"), "Match Components");
    public static final Component FILTER_IGNORE_COMPONENTS = addTranslation("gui",
            EnderIO.rl("filter.ignore_components"), "Ignore Components");
    public static final Component DAMAGE_FILTER_MODE = addTranslation("gui", EnderIO.rl("filter.damage"),
            "Damage Filter");

    // endregion

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIO.REGILITE.addTranslation(prefix, id, translation);
    }

    public static void register() {
    }
}
