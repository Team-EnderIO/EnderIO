package com.enderio.base.common.lang;

import com.enderio.EnderIO;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.misc.ApiLang;
import com.enderio.base.common.block.glass.GlassLighting;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Locale;

public class EIOLang {
    public static final Component BLOCK_BLAST_RESISTANT = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("block.blast_resistant"), "Blast resistant"));

    // region Fused Quartz

    public static final Component FUSED_QUARTZ_EMITS_LIGHT = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("fused_quartz.emits_light"), "Emits light"));
    public static final Component FUSED_QUARTZ_BLOCKS_LIGHT = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("fused_quartz.blocks_light"), "Blocks light"));

    public static final Component GLASS_COLLISION_PLAYERS_PASS = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("collision.players_pass"), "Not solid to players"));
    public static final Component GLASS_COLLISION_PLAYERS_BLOCK = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("collision.players_block"), "Only solid to players"));
    public static final Component GLASS_COLLISION_MOBS_PASS = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("collision.mobs_pass"), "Not solid to monsters"));
    public static final Component GLASS_COLLISION_MOBS_BLOCK = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("collision.mobs_block"), "Only solid to monsters"));
    public static final Component GLASS_COLLISION_ANIMALS_PASS = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("collision.animals_pass"), "Not solid to animals"));
    public static final Component GLASS_COLLISION_ANIMALS_BLOCK = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("collision.animals_block"), "Only solid to animals"));

    // endregion

    // region Items

    public static final Component DARK_STEEL_LADDER_FASTER = TooltipUtil.style(addTranslation("tooltip", EnderIO.loc("dark_steel_ladder.faster"), "Faster than regular ladders"));

    public static final Component SOUL_VIAL_ERROR_PLAYER = addTranslation("message", EnderIO.loc("soul_vial.error_player"), "You cannot put player in a bottle!");
    public static final Component SOUL_VIAL_ERROR_BOSS = addTranslation("message", EnderIO.loc("soul_vial.error_boss"), "Nice try. Bosses don't like bottles.");
    public static final Component SOUL_VIAL_ERROR_BLACKLISTED = addTranslation("message", EnderIO.loc("soul_vial.error_blacklisted"), "This entity has been blacklisted.");
    public static final Component SOUL_VIAL_ERROR_FAILED = addTranslation("message", EnderIO.loc("soul_vial.error_failed"), "This entity cannot be captured.");
    public static final Component SOUL_VIAL_ERROR_DEAD = addTranslation("message", EnderIO.loc("soul_vial.error_dead"), "Cannot capture a dead mob!");
    public static final MutableComponent SOUL_VIAL_TOOLTIP_HEALTH = addTranslation("tooltip", EnderIO.loc("soul_vial.health"), "Health: %s/%s");

    public static final Component COORDINATE_SELECTOR_NO_PAPER = addTranslation("info", EnderIO.loc("coordinate_selector.no_paper"), "No paper in inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = addTranslation("info", EnderIO.loc("coordinate_selector.no_block"), "No block in range");
    public static final Component TOO_MANY_LEVELS = addTranslation("info", EnderIO.loc("too_many_levels"), "You have more than 21862 levels, that's too much XP.");


    public static final Component CONDUIT_CHANNEL = addTranslation("gui", EnderIO.loc("conduit_channel"), "Conduit-Channel");
    public static final Component REDSTONE_CHANNEL = addTranslation("gui", EnderIO.loc("redstone_channel"), "Redstone-Channel");
    public static final Component REDSTONE_MODE = addTranslation("gui", EnderIO.loc("redstone.mode"), "Redstone Mode");
    public static final Component REDSTONE_ALWAYS_ACTIVE = addTranslation("gui", EnderIO.loc("redstone.always_active"), "Always active");
    public static final Component REDSTONE_ACTIVE_WITH_SIGNAL = addTranslation("gui", EnderIO.loc("redstone.active_with_signal"), "Active with signal");
    public static final Component REDSTONE_ACTIVE_WITHOUT_SIGNAL = addTranslation("gui", EnderIO.loc("redstone.active_without_signal"), "Active without signal");
    public static final Component REDSTONE_NEVER_ACTIVE = addTranslation("gui", EnderIO.loc("redstone.never_active"), "Never active");
    public static final Component ROUND_ROBIN_ENABLED = addTranslation("gui", EnderIO.loc("round_robin.enabled"), "Round Robin Enabled");
    public static final Component ROUND_ROBIN_DISABLED = addTranslation("gui", EnderIO.loc("round_robin.disabled"), "Round Robin Disabled");
    public static final Component SELF_FEED_ENABLED = addTranslation("gui", EnderIO.loc("self_feed.enabled"), "Self Feed Enabled");
    public static final Component SELF_FEED_DISABLED = addTranslation("gui", EnderIO.loc("self_feed.disabled"), "Self Feed Disabled");
    public static final Component FLUID_CONDUIT_CHANGE_FLUID1 = addTranslation("gui", EnderIO.loc("fluid_conduit.change_fluid1"), "Locked Fluid:");
    public static final Component FLUID_CONDUIT_CHANGE_FLUID2 = addTranslation("gui", EnderIO.loc("fluid_conduit.change_fluid2"), "Click to reset!");
    public static final MutableComponent FLUID_CONDUIT_CHANGE_FLUID3 = addTranslation("gui", EnderIO.loc("fluid_conduit.change_fluid3"), "Fluid: %s");

    public static final MutableComponent TANK_EMPTY_STRING = addTranslation("tooltip", EnderIO.loc("fluid_tank.tank_empty_tooltip"), "Empty tank");
    public static final MutableComponent FLUID_TANK_TOOLTIP = addTranslation("tooltip", EnderIO.loc("fluid_tank.tank_tooltip"), "%d/%d mb of %s");//[amount]/[capacity] mb of [FluidName]
    // endregion

    // region Capacitors

    public static final MutableComponent CAPACITOR_TOOLTIP_BASE = addTranslation("tooltip", EnderIO.loc("capacitor.base"), "Base Modifier: %s");

    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_CAPACITY = addTranslation("tooltip", CapacitorModifier.ENERGY_CAPACITY.modifierId, "Energy Capacity Modifier: %s");
    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_USE = addTranslation("tooltip", CapacitorModifier.ENERGY_USE.modifierId, "Energy Use Modifier: %s");

    public static final Component GLIDER_DISABLED = addTranslation("message", EnderIO.loc("glider.disable"), "Gliding is disabled: ");
    public static final Component GLIDER_DISABLED_FALL_FLYING = addTranslation("message", EnderIO.loc("glider.disable.fall_flying"), "Elytra Flight");


    // endregion

    // region Enchantments

    public static final Component AUTO_SMELT_DESC = enchantmentDescription("auto_smelt", "desc",
        "Automatically smelts whatever is mined.");

    public static final Component REPELLENT_DESC = enchantmentDescription("repellent", "desc",
        "Chance to teleport attackers away. Higher levels teleport more often and farther.");

    public static final Component SHIMMER_DESC = enchantmentDescription("shimmer", "desc",
        "Makes the item shimmer as if it was enchanted... That's all... Really...");

    public static final Component SOULBOUND_DESC = enchantmentDescription("soulbound", "desc",
        "Prevents item from being lost on death. Note: Most gravestone mods are stupid and prevent this from working!");

    public static final Component WITHERING_DESC = enchantmentDescription("withering", "desc",
        "Applies wither to the target.");

    public static final Component WITHERING_TYPES = enchantmentDescription("withering", "type",
        "Applies to bladed weapons, bows and crossbows.");

    public static final Component XP_BOOST_DESC = enchantmentDescription("xp_boost", "desc",
        "Extra XP from mobs and blocks");

    private static Component enchantmentDescription(String enchantmentName, String suffix, String description) {
        return TooltipUtil.style(addTranslation("enchantment", EnderIO.loc(String.format("%s.%s", enchantmentName, suffix)), description));
    }

    // endregion

    //region Filters

    public static final Component FILTER = addTranslation("gui", EnderIO.loc("filter"), "Filter");

    // endregion

    // region Graves

    public static final Component GRAVE_WRONG_OWNER = addTranslation("message", EnderIO.loc("grave.wrong_owner"), "This grave is not yours! You cannot open it.");

    // endregion

    // region Grinding balls

    public static final MutableComponent GRINDINGBALL_MAIN_OUTPUT = addTranslation("tooltip", EnderIO.loc("grinding_ball_main_output"), "Main Output %s%%");
    public static final MutableComponent GRINDINGBALL_BONUS_OUTPUT = addTranslation("tooltip", EnderIO.loc("grinding_ball_bonus_output"), "Bonus Output %s%%");
    public static final MutableComponent GRINDINGBALL_POWER_USE = addTranslation("tooltip", EnderIO.loc("grinding_ball_power_use"), "Power Use %s%%");

    // endregion

    // region GUI

    public static final MutableComponent ENERGY_AMOUNT = addTranslation("info", EnderIO.loc("energy.amount"), "%s \u00B5I");
    public static final Component RANGE = addTranslation("gui", EnderIO.loc("range"), "Range");
    public static final Component SHOW_RANGE = addTranslation("gui", EnderIO.loc("range.show"), "Show Range");
    public static final Component HIDE_RANGE = addTranslation("gui", EnderIO.loc("range.hide"), "Hide Range");

    public static final Component IOCONFIG = addTranslation("gui", EnderIO.loc("ioconfig"), "IO Configuration");
    public static final Component TOGGLE_NEIGHBOUR = addTranslation("gui", EnderIO.loc("ioconfig.neighbour"), "Show/Hide Neighbours");

    public static final Component PUSH = addTranslation("gui", EnderIO.loc("ioconfig.push"), "Push");
    public static final Component PULL = addTranslation("gui", EnderIO.loc("ioconfig.pull"), "Pull");
    public static final Component BOTH = addTranslation("gui", EnderIO.loc("ioconfig.both"), "Push / Pull");
    public static final Component DISABLED = addTranslation("gui", EnderIO.loc("ioconfig.disabled"), "Disabled");
    public static final Component NONE = addTranslation("gui", EnderIO.loc("ioconfig.none"), "None");

    public static final MutableComponent NOCAP_TITLE = addTranslation("gui", EnderIO.loc("nocap.title"), "Capacitor Missing");
    public static final MutableComponent NOCAP_DESC = addTranslation("gui", EnderIO.loc("nocap.desc"), "Insert any capacitor so \n this machine can work!");

    public static Component VISIBLE = addTranslation("gui", EnderIO.loc("visible.true"), "Visible");
    public static Component NOT_VISIBLE = addTranslation("gui", EnderIO.loc("visible.false"), "Hidden");

    // endregion

    // region Entity Storage

    public static final MutableComponent TOOLTIP_NO_SOULBOUND = addTranslation("gui", EnderIO.loc("no_soulbound"), "Bind a soul to the item to be able to use it");

    // endregion

    // region Glider

    public static final Component USE_GLIDER_ADVANCEMENT_TITLE = addTranslation("advancements", EnderIO.loc("use_glider.title"), "Majestic");
    public static final Component USE_GLIDER_ADVANCEMENT_DESCRIPTION = addTranslation("advancements", EnderIO.loc("use_glider.description"),
        "Do you really trust some leather?");

    public static final Component RICH_ADVANCEMENT_TITLE = addTranslation("advancements", EnderIO.loc("rich.title"), "Don't tell the others");
    public static final Component RICH_ADVANCEMENT_DESCRIPTION = addTranslation("advancements", EnderIO.loc("rich.description"),
        "Make others think you are rich");
    public static final Component RICHER_ADVANCEMENT_TITLE = addTranslation("advancements", EnderIO.loc("richer.title"), "Is this real?");
    public static final Component RICHER_ADVANCEMENT_DESCRIPTION = addTranslation("advancements", EnderIO.loc("richer.description"),
        "Make others think you are richer");

    // endregion

    // region Misc Tooltips

    public static final MutableComponent SHOW_DETAIL_TOOLTIP = addTranslation("tooltip", EnderIO.loc("gui.show_advanced_tooltip"), "<Hold Shift>");

    // endregion
    
    // region Guidebook
    
    public static final Component GUIDEBOOK_TITLE = addTranslation("guidebook", EnderIO.loc("book_title"), "Book Title");
    public static final Component GUIDEBOOK_LANDING_TEXT = addTranslation("guidebook", EnderIO.loc("landing_text"), "Landing Text");

    //endregion

    // region JEI

    public static final Component JEI_FIRE_CRAFTING_TITLE = addTranslation("jei", EnderIO.loc("fire_crafting"), "title", "Fire Crafting");
    public static final Component JEI_FIRE_CRAFTING_VALID_BLOCKS = addTranslation("jei", EnderIO.loc("fire_crafting"), "valid_blocks", "Valid Blocks:");
    public static final Component JEI_FIRE_CRAFTING_VALID_DIMENSIONS = addTranslation("jei", EnderIO.loc("fire_crafting"), "valid_dimensions", "Valid Dimensions:");
    public static final Component JEI_FIRE_CRAFTING_LOOT_TABLE = addTranslation("jei", EnderIO.loc("fire_crafting"), "loot_table", "Loot Table:");
    public static final Component JEI_FIRE_CRAFTING_MAX_DROPS = addTranslation("jei", EnderIO.loc("fire_crafting"), "max_drops", "Max Item Drops:");

    public static final Component JEI_GRINDING_CRAFTING_TITLE = addTranslation("jei", EnderIO.loc("grinding"), "title", "Grinding");
    public static final MutableComponent JEI_GRINDING_CONSUME_CHANCE = addTranslation("jei", EnderIO.loc("grinding"), "consume_chance", "33% chance to be consumed");

    public static final MutableComponent JEI_GRAINS_HAND_GRIND = addTranslation("jei", EnderIO.loc("grinding"), "hand_grinding_infinity",
        "Hold some flint in your offhand and some deepslate or cobbled deepslate in your main hand, then shift right-click a block of obsidian, crying obsidian or a grindstone to produce early grains of infinity.");

    public static final MutableComponent JEI_COAL_HAND_GRIND = addTranslation("jei", EnderIO.loc("grinding"), "hand_grinding_coal",
        "Hold some flint in your offhand and 3 or more coal in your main hand, then shift right-click a block of obsidian, crying obsidian or a grindstone to produce early powdered coal.");

    // endregion

    // region Glass Names

    private static void registerGlassLang() {
        for (var lighting : GlassLighting.values()) {
            String lightingName = lighting != GlassLighting.NONE ? lighting.englishName() + " " : "";
            String lightingKeyName = lighting != GlassLighting.NONE ? "_" + lighting.shortName() : "";

            addTranslation("block", EnderIO.loc("clear_glass" + lightingKeyName),
                lightingName + "Clear Glass");
            addTranslation("block", EnderIO.loc("fused_quartz" + lightingKeyName),
                lightingName + "Fused Quartz");

            for (var color : DyeColor.values()) {
                String colorName = createEnglishPrefix(color);

                addTranslation("block", EnderIO.loc("clear_glass" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT)),
                    colorName + lightingName + "Clear Glass");
                addTranslation("block", EnderIO.loc("fused_quartz" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT)),
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

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIO.getRegilite().addTranslation(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name, String translation) {
        return EnderIO.getRegilite().addTranslation(prefix, new ResourceLocation(path.getNamespace(), path.getPath() + "/" + name), translation);
    }

    public static void register() {
        // TODO: NEO-PORT: This is gross. We should probably just hand-write a lang json for core/api?
        ApiLang.REDSTONE_ACTIVE_WITH_SIGNAL = REDSTONE_ACTIVE_WITH_SIGNAL;
        ApiLang.REDSTONE_NEVER_ACTIVE = REDSTONE_NEVER_ACTIVE;
        ApiLang.REDSTONE_ALWAYS_ACTIVE = REDSTONE_ALWAYS_ACTIVE;
        ApiLang.REDSTONE_ACTIVE_WITHOUT_SIGNAL = REDSTONE_ACTIVE_WITHOUT_SIGNAL;

        registerGlassLang();
    }
}
