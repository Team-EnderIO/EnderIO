package com.enderio.base.common.lang;

import com.enderio.EnderIO;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.misc.ApiLang;
import com.enderio.base.common.block.glass.GlassLighting;
import com.enderio.core.common.util.TooltipUtil;
import com.tterrag.registrate.Registrate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;

import java.util.Locale;

public class EIOLang {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final Component BLOCK_BLAST_RESISTANT = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("block.blast_resistant"), "Blast resistant"));

    // region Fused Quartz

    public static final Component FUSED_QUARTZ_EMITS_LIGHT = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("fused_quartz.emits_light"), "Emits light"));
    public static final Component FUSED_QUARTZ_BLOCKS_LIGHT = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("fused_quartz.blocks_light"), "Blocks light"));

    public static final Component GLASS_COLLISION_PLAYERS_PASS = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.players_pass"), "Not solid to players"));
    public static final Component GLASS_COLLISION_PLAYERS_BLOCK = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.players_block"), "Only solid to players"));
    public static final Component GLASS_COLLISION_MOBS_PASS = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.mobs_pass"), "Not solid to monsters"));
    public static final Component GLASS_COLLISION_MOBS_BLOCK = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.mobs_block"), "Only solid to monsters"));
    public static final Component GLASS_COLLISION_ANIMALS_PASS = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.animals_pass"), "Not solid to animals"));
    public static final Component GLASS_COLLISION_ANIMALS_BLOCK = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("collision.animals_block"), "Only solid to animals"));

    // endregion

    // region Items

    public static final Component DARK_STEEL_LADDER_FASTER = TooltipUtil.style(REGISTRATE.addLang("tooltip", EnderIO.loc("dark_steel_ladder.faster"), "Faster than regular ladders"));

    public static final Component SOUL_VIAL_ERROR_PLAYER = REGISTRATE.addLang("message", EnderIO.loc("soul_vial.error_player"), "You cannot put player in a bottle!");
    public static final Component SOUL_VIAL_ERROR_BOSS = REGISTRATE.addLang("message", EnderIO.loc("soul_vial.error_boss"), "Nice try. Bosses don't like bottles.");
    public static final Component SOUL_VIAL_ERROR_BLACKLISTED = REGISTRATE.addLang("message", EnderIO.loc("soul_vial.error_blacklisted"), "This entity has been blacklisted.");
    public static final Component SOUL_VIAL_ERROR_FAILED = REGISTRATE.addLang("message", EnderIO.loc("soul_vial.error_failed"), "This entity cannot be captured.");
    public static final Component SOUL_VIAL_ERROR_DEAD = REGISTRATE.addLang("message", EnderIO.loc("soul_vial.error_dead"), "Cannot capture a dead mob!");
    public static final MutableComponent SOUL_VIAL_TOOLTIP_HEALTH = REGISTRATE.addLang("tooltip", EnderIO.loc("soul_vial.health"), "Health: %s/%s");

    public static final Component COORDINATE_SELECTOR_NO_PAPER = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_paper"), "No paper in inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_block"), "No block in range");
    public static final Component TOO_MANY_LEVELS = REGISTRATE.addLang("info", EnderIO.loc("too_many_levels"), "You have more than 21862 levels, that's too much XP.");


    public static final Component CHANNEL = REGISTRATE.addLang("gui", EnderIO.loc("channel"), "Channel");
    public static final Component REDSTONE_MODE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.mode"), "Redstone Mode");
    public static final Component REDSTONE_ALWAYS_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.always_active"), "Always active");
    public static final Component REDSTONE_ACTIVE_WITH_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_with_signal"), "Active with signal");
    public static final Component REDSTONE_ACTIVE_WITHOUT_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_without_signal"), "Active without signal");
    public static final Component REDSTONE_NEVER_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.never_active"), "Never active");
    public static final Component ROUND_ROBIN_ENABLED = REGISTRATE.addLang("gui", EnderIO.loc("round_robin.enabled"), "Round Robin Enabled");
    public static final Component ROUND_ROBIN_DISABLED = REGISTRATE.addLang("gui", EnderIO.loc("round_robin.disabled"), "Round Robin Disabled");
    public static final Component SELF_FEED_ENABLED = REGISTRATE.addLang("gui", EnderIO.loc("self_feed.enabled"), "Self Feed Enabled");
    public static final Component SELF_FEED_DISABLED = REGISTRATE.addLang("gui", EnderIO.loc("self_feed.disabled"), "Self Feed Disabled");
    public static final Component FLUID_CONDUIT_CHANGE_FLUID1 = REGISTRATE.addLang("gui", EnderIO.loc("fluid_conduit.change_fluid1"), "Locked Fluid:");
    public static final Component FLUID_CONDUIT_CHANGE_FLUID2 = REGISTRATE.addLang("gui", EnderIO.loc("fluid_conduit.change_fluid2"), "Click to reset!");
    public static final MutableComponent FLUID_CONDUIT_CHANGE_FLUID3 = REGISTRATE.addLang("gui", EnderIO.loc("fluid_conduit.change_fluid3"), "Fluid: %s");

    public static final MutableComponent TANK_EMPTY_STRING = REGISTRATE.addLang("tooltip", EnderIO.loc("fluid_tank.tank_empty_tooltip"), "Empty tank");
    public static final MutableComponent FLUID_TANK_TOOLTIP = REGISTRATE.addLang("tooltip", EnderIO.loc("fluid_tank.tank_tooltip"), "%d/%d mb of %s");//[amount]/[capacity] mb of [FluidName]
    // endregion

    // region Dark Steel

    public static final MutableComponent HEAD_DROP_CHANCE = REGISTRATE.addLang("info", EnderIO.loc("headchance"), "%s%% chance to drop a mob head");
    public static final MutableComponent ENERGY_AMOUNT = REGISTRATE.addLang("info", EnderIO.loc("energy.amount"), "%s \u00B5I");
    public static final MutableComponent DURABILITY_AMOUNT = REGISTRATE.addLang("info", EnderIO.loc("durability.amount"), "Durability %s");

    public static final MutableComponent DS_UPGRADE_XP_COST = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.cost"), "Costs %s Levels");
    public static final Component DS_UPGRADE_ACTIVATE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.activate"), "Right Click to Activate");
    public static final Component DS_UPGRADE_ITEM_NO_XP = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.no_xp"), "Not enough XP");
    public static final Component DS_UPGRADE_AVAILABLE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.available"), "Available Upgrades").withStyle(
        ChatFormatting.YELLOW);

    public static final Component DS_UPGRADE_EMPOWERED_I = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered_l1"), "Empowered");
    public static final Component DS_UPGRADE_EMPOWERED_II = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered_l2"), "Empowered II");
    public static final Component DS_UPGRADE_EMPOWERED_III = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered_l3"), "Empowered III");
    public static final Component DS_UPGRADE_EMPOWERED_IV = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered_l4"), "Empowered IV");
    public static final Component DS_UPGRADE_EMPOWERED_DESCRIPTION = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.description"),
        "Infuse the steel with the power of Micro Infinity");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_STORAGE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.storage"),
        "Holds up to %s \u00B5I");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_DAMAGE_ABSORPTION = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.absorption"),
        "%s%% damage absorbed by \u00B5I");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_EFFICIENCY = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.efficiency"),
        "Efficiency +%s when powered");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_OBSIDIAM_EFFICIENCY = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.obsidian.efficiency"),
        "Efficiency +%s when breaking obsidian");

    public static final Component DS_UPGRADE_SPOON = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.spoon"), "Spoon");
    public static final Component DS_UPGRADE_SPOON_DESCRIPTION = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.spoon.description"),
        "Who needs a shovel when you have a spoon?");

    public static final Component DS_UPGRADE_FORK = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.fork"), "Fork");
    public static final Component DS_UPGRADE_FORK_DESCRIPTION = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.fork.description"),
        "Who needs a hoe when you have a fork?");

    public static final Component DS_UPGRADE_DIRECT = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.direct"), "Direct");
    public static final Component DS_UPGRADE_DIRECT_DESCRIPTION = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.direct.description"),
        "Teleports harvested items directly into your inventory");

    public static final Component DS_UPGRADE_EXPLOSIVE_I = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.explosive_l1"), "Explosive I");
    public static final Component DS_UPGRADE_EXPLOSIVE_II = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.explosive_l2"), "Explosive II");
    public static final Component DS_UPGRADE_EXPLOSIVE_DESCRIPTION = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.explosive.description"),
        "Makes surrounding dirt and rock go splodey");

    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_I = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.explosive_penetration_l1"),
        "Explosive Penetration I");
    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_II = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.explosive_penetration_l2"),
        "Explosive Penetration II");
    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_DESCRIPTION = REGISTRATE.addLang("info",
        EnderIO.loc("darksteel.upgrade.explosive_penetration.description"), "Makes dirt and rock behind the mined block go splodey");

    // endregion

    // region Capacitors

    public static final MutableComponent CAPACITOR_TOOLTIP_BASE = REGISTRATE.addLang("tooltip", EnderIO.loc("capacitor.base"), "Base Modifier: %s");

    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_CAPACITY = REGISTRATE.addLang("tooltip", CapacitorModifier.ENERGY_CAPACITY.id, "Energy Capacity Modifier: %s");
    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_USE = REGISTRATE.addLang("tooltip", CapacitorModifier.ENERGY_USE.id, "Energy Use Modifier: %s");

    public static final Component GLIDER_DISABLED = REGISTRATE.addLang("message", EnderIO.loc("glider.disable"), "Gliding is disabled: ");
    public static final Component GLIDER_DISABLED_FALL_FLYING = REGISTRATE.addLang("message", EnderIO.loc("glider.disable.fall_flying"), "Elytra Flight");


    // endregion

    // region Enchantments

    public static final Component AUTO_SMELT_DESC = enchantmentDescription("auto_smelt", "Automatically smelts whatever is mined");

    public static final Component REPELLENT_DESC1 = enchantmentDescription("repellent1",
        "Chance to teleport attackers away");
    public static final Component REPELLENT_DESC2 = enchantmentDescription("repellent2",
        "Higher levels teleport more often and farther");

    public static final Component SHIMMER_DESC1 = enchantmentDescription("shimmer1",
        "Makes the item shimmer as if it was enchanted.");
    public static final Component SHIMMER_DESC2 = enchantmentDescription("shimmer2",
        "That's all.");
    public static final Component SHIMMER_DESC3 = enchantmentDescription("shimmer3",
        "Really.");
    public static final Component SHIMMER_DESC4 = enchantmentDescription("shimmer4",
        "Nothing more.");
    public static final Component SHIMMER_DESC5 = enchantmentDescription("shimmer5",
        "Yes, it is useless.");
    public static final Component SHIMMER_DESC6 = enchantmentDescription("shimmer6",
        "I know.");

    public static final Component SOULBOUND_DESC1 = enchantmentDescription("soulbound1",
        "Prevents item from being lost on death.");
    public static final Component SOULBOUND_DESC2 = enchantmentDescription("soulbound2",
        "Note: Most gravestone mods are stupid and prevent this from working!");

    public static final Component WITHERING_BLADE_DESC1 = enchantmentDescription("withering_blade1",
        "Applies wither to the target");
    public static final Component WITHERING_BLADE_DESC2 = enchantmentDescription("withering_blade2",
        "Applies to bladed weapons");

    public static final Component WITHERING_ARROW_DESC1 = enchantmentDescription("withering_arrow1",
        "Applies wither to the target");
    public static final Component WITHERING_ARROW_DESC2 = enchantmentDescription("withering_arrow2",
        "Applies to bows.");

    public static final Component WITHERING_BOLT_DESC1 = enchantmentDescription("withering_bolt1",
        "Applies wither to the target");
    public static final Component WITHERING_BOLT_DESC2 = enchantmentDescription("withering_bolt2",
        "Applies to crossbows.");

    public static final Component XP_BOOST_DESC = enchantmentDescription("xp_boost", "Extra XP from mobs and blocks");

    private static Component enchantmentDescription(String enchantmentname, String description) {
        return TooltipUtil.style(REGISTRATE.addLang("description", EnderIO.loc("enchantment." + enchantmentname), description));
    }

    // endregion

    //region Filters

    public static final Component FILTER = REGISTRATE.addLang("gui", EnderIO.loc("filter"), "Filter");

    // endregion

    // region Graves

    public static final Component GRAVE_WRONG_OWNER = REGISTRATE.addLang("message", EnderIO.loc("grave.wrong_owner"), "This grave is not yours! You cannot open it.");

    // endregion

    // region Grinding balls

    public static final MutableComponent GRINDINGBALL_MAIN_OUTPUT = REGISTRATE.addLang("tooltip", EnderIO.loc("grinding_ball_main_output"), "Main Output %s%%");
    public static final MutableComponent GRINDINGBALL_BONUS_OUTPUT = REGISTRATE.addLang("tooltip", EnderIO.loc("grinding_ball_bonus_output"), "Bonus Output %s%%");
    public static final MutableComponent GRINDINGBALL_POWER_USE = REGISTRATE.addLang("tooltip", EnderIO.loc("grinding_ball_power_use"), "Power Use %s%%");

    // endregion

    // region GUI

    public static final Component RANGE = REGISTRATE.addLang("gui", EnderIO.loc("range"), "Range");
    public static final Component SHOW_RANGE = REGISTRATE.addLang("gui", EnderIO.loc("range.show"), "Show Range");
    public static final Component HIDE_RANGE = REGISTRATE.addLang("gui", EnderIO.loc("range.hide"), "Hide Range");

    public static final Component IOCONFIG = REGISTRATE.addLang("gui", EnderIO.loc("ioconfig"), "IO Configuration");
    public static final Component TOGGLE_NEIGHBOUR = REGISTRATE.addLang("gui", EnderIO.loc("ioconfig.neighbour"), "Show/Hide Neighbours");

    public static final Component PUSH = REGISTRATE.addLang("gui", EnderIO.loc("ioconfig.push"), "Push");
    public static final Component PULL = REGISTRATE.addLang("gui", EnderIO.loc("ioconfig.pull"), "Pull");
    public static final Component BOTH = REGISTRATE.addLang("gui", EnderIO.loc("ioconfig.both"), "Push / Pull");
    public static final Component DISABLED = REGISTRATE.addLang("gui", EnderIO.loc("ioconfig.disabled"), "Disabled");
    public static final Component NONE = REGISTRATE.addLang("gui", EnderIO.loc("ioconfig.none"), "None");

    public static final MutableComponent NOCAP_TITLE = REGISTRATE.addLang("gui", EnderIO.loc("nocap.title"), "Capacitor Missing");
    public static final MutableComponent NOCAP_DESC = REGISTRATE.addLang("gui", EnderIO.loc("nocap.desc"), "Insert any capacitor so \n this machine can work!");


    // endregion

    // region Glider

    public static final Component USE_GLIDER_ADVANCEMENT_TITLE = REGISTRATE.addLang("advancements", EnderIO.loc("use_glider.title"), "Majestic");
    public static final Component USE_GLIDER_ADVANCEMENT_DESCRIPTION = REGISTRATE.addLang("advancements", EnderIO.loc("use_glider.description"), "Do you really trust some leather?");

    public static final Component RICH_ADVANCEMENT_TITLE = REGISTRATE.addLang("advancements", EnderIO.loc("rich.title"), "Don't tell the others");
    public static final Component RICH_ADVANCEMENT_DESCRIPTION = REGISTRATE.addLang("advancements", EnderIO.loc("rich.description"), "Make others think you are rich");
    public static final Component RICHER_ADVANCEMENT_TITLE = REGISTRATE.addLang("advancements", EnderIO.loc("richer.title"), "Is this real?");
    public static final Component RICHER_ADVANCEMENT_DESCRIPTION = REGISTRATE.addLang("advancements", EnderIO.loc("richer.description"), "Make others think you are richer");

    // endregion

    // region Misc Tooltips

    public static final MutableComponent SHOW_DETAIL_TOOLTIP = REGISTRATE.addLang("tooltip", EnderIO.loc("gui.show_advanced_tooltip"), "<Hold Shift>");

    // endregion
    
    // region Guidebook
    
    public static final Component GUIDEBOOK_TITLE = REGISTRATE.addLang("guidebook", EnderIO.loc("book_title"), "Book Title");
    public static final Component GUIDEBOOK_LANDING_TEXT = REGISTRATE.addLang("guidebook", EnderIO.loc("landing_text"), "Landing Text");

    //endregion

    // region JEI

    public static final Component JEI_FIRE_CRAFTING_TITLE = REGISTRATE.addLang("jei", EnderIO.loc("fire_crafting"), "title", "Fire Crafting");
    public static final Component JEI_FIRE_CRAFTING_VALID_BLOCKS = REGISTRATE.addLang("jei", EnderIO.loc("fire_crafting"), "valid_blocks", "Valid Blocks:");
    public static final Component JEI_FIRE_CRAFTING_VALID_DIMENSIONS = REGISTRATE.addLang("jei", EnderIO.loc("fire_crafting"), "valid_dimensions", "Valid Dimensions:");

    public static final Component JEI_GRINDING_CRAFTING_TITLE = REGISTRATE.addLang("jei", EnderIO.loc("grinding"), "title", "Grinding");
    public static final MutableComponent JEI_GRINDING_CONSUME_CHANCE = REGISTRATE.addLang("jei", EnderIO.loc("grinding"), "consume_chance", "33% chance to be consumed");

    public static final MutableComponent JEI_GRAINS_HAND_GRIND = REGISTRATE.addLang("jei", EnderIO.loc("grinding"), "hand_grinding_infinity",
        "Hold some flint in your offhand and some deepslate or cobbled deepslate in your main hand, then shift right-click a block of obsidian, crying obsidian or a grindstone to produce early grains of infinity.");

    public static final MutableComponent JEI_COAL_HAND_GRIND = REGISTRATE.addLang("jei", EnderIO.loc("grinding"), "hand_grinding_coal",
        "Hold some flint in your offhand and 3 or more coal in your main hand, then shift right-click a block of obsidian, crying obsidian or a grindstone to produce early powdered coal.");

    // endregion

    // region Glass Names

    private static void registerGlassLang() {
        for (var lighting : GlassLighting.values()) {
            String lightingName = lighting != GlassLighting.NONE ? lighting.englishName() + " " : "";
            String lightingKeyName = lighting != GlassLighting.NONE ? "_" + lighting.shortName() : "";

            REGISTRATE.addLang("block", EnderIO.loc("clear_glass" + lightingKeyName),
                lightingName + "Clear Glass");
            REGISTRATE.addLang("block", EnderIO.loc("fused_quartz" + lightingKeyName),
                lightingName + "Fused Quartz");

            for (var color : DyeColor.values()) {
                String colorName = createEnglishPrefix(color);

                REGISTRATE.addLang("block", EnderIO.loc("clear_glass" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT)),
                    colorName + lightingName + "Clear Glass");
                REGISTRATE.addLang("block", EnderIO.loc("fused_quartz" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT)),
                    colorName + lightingName + "Fused Quartz");
            }
        }
    }

    private static String createEnglishPrefix(DyeColor color) {
        StringBuilder builder = new StringBuilder();
        boolean nextUpper = true;
        for (char c : color.getName().replace("_", " ").toCharArray()) {
            if (nextUpper) {
                builder.append(Character.toUpperCase(c));
                nextUpper = false;
                continue;
            }
            if (c == ' ') {
                nextUpper = true;
            }
            builder.append(c);
        }
        builder.append(" ");
        return builder.toString();
    }

    // endregion

    public static void register() {
        ApiLang.REDSTONE_ACTIVE_WITH_SIGNAL = REDSTONE_ACTIVE_WITH_SIGNAL;
        ApiLang.REDSTONE_NEVER_ACTIVE = REDSTONE_NEVER_ACTIVE;
        ApiLang.REDSTONE_ALWAYS_ACTIVE = REDSTONE_ALWAYS_ACTIVE;
        ApiLang.REDSTONE_ACTIVE_WITHOUT_SIGNAL = REDSTONE_ACTIVE_WITHOUT_SIGNAL;

        registerGlassLang();
    }
}
