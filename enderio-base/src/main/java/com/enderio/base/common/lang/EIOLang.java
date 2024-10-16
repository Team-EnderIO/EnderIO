package com.enderio.base.common.lang;

import com.enderio.EnderIOBase;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.common.block.glass.GlassLighting;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Locale;

public class EIOLang {
    public static final Component BLOCK_BLAST_RESISTANT = TooltipUtil.style(addTranslation("tooltip", EnderIOBase.loc("block.blast_resistant"), "Blast resistant"));

    // region GUI Interactions

    public static final Component OK = addTranslation("gui", EnderIOBase.loc("ok"), "Ok");
    public static final Component CANCEL = addTranslation("gui", EnderIOBase.loc("cancel"), "Cancel");

    // endregion

    // region Fused Quartz

    public static final Component FUSED_QUARTZ_EMITS_LIGHT = TooltipUtil.style(addTranslation("tooltip", EnderIOBase.loc("fused_quartz.emits_light"), "Emits light"));
    public static final Component FUSED_QUARTZ_BLOCKS_LIGHT = TooltipUtil.style(addTranslation("tooltip", EnderIOBase.loc("fused_quartz.blocks_light"), "Blocks light"));

    // endregion

    // region Items

    public static final Component DARK_STEEL_LADDER_FASTER = TooltipUtil.style(addTranslation("tooltip", EnderIOBase.loc("dark_steel_ladder.faster"), "Faster than regular ladders"));

    public static final Component SOUL_VIAL_ERROR_PLAYER = addTranslation("message", EnderIOBase.loc("soul_vial.error_player"), "You cannot put player in a bottle!");
    public static final Component SOUL_VIAL_ERROR_BOSS = addTranslation("message", EnderIOBase.loc("soul_vial.error_boss"), "Nice try. Bosses don't like bottles.");
    public static final Component SOUL_VIAL_ERROR_BLACKLISTED = addTranslation("message", EnderIOBase.loc("soul_vial.error_blacklisted"), "This entity has been blacklisted.");
    public static final Component SOUL_VIAL_ERROR_FAILED = addTranslation("message", EnderIOBase.loc("soul_vial.error_failed"), "This entity cannot be captured.");
    public static final Component SOUL_VIAL_ERROR_DEAD = addTranslation("message", EnderIOBase.loc("soul_vial.error_dead"), "Cannot capture a dead mob!");
    public static final MutableComponent SOUL_VIAL_TOOLTIP_HEALTH = addTranslation("tooltip", EnderIOBase.loc("soul_vial.health"), "Health: %s/%s");

    public static final Component COORDINATE_SELECTOR_NO_PAPER = addTranslation("info", EnderIOBase.loc("coordinate_selector.no_paper"), "No paper in inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = addTranslation("info", EnderIOBase.loc("coordinate_selector.no_block"), "No block in range");
    public static final Component TOO_MANY_LEVELS = addTranslation("info", EnderIOBase.loc("too_many_levels"), "You have more than 21862 levels, that's too much XP.");


    public static final Component CONDUIT_CHANNEL = addTranslation("gui", EnderIOBase.loc("conduit_channel"), "Conduit-Channel");
    public static final Component REDSTONE_CHANNEL = addTranslation("gui", EnderIOBase.loc("redstone_channel"), "Redstone-Channel");
    public static final Component REDSTONE_MODE = addTranslation("gui", EnderIOBase.loc("redstone.mode"), "Redstone Mode");
    public static final Component ROUND_ROBIN_ENABLED = addTranslation("gui", EnderIOBase.loc("round_robin.enabled"), "Round Robin Enabled");
    public static final Component ROUND_ROBIN_DISABLED = addTranslation("gui", EnderIOBase.loc("round_robin.disabled"), "Round Robin Disabled");
    public static final Component SELF_FEED_ENABLED = addTranslation("gui", EnderIOBase.loc("self_feed.enabled"), "Self Feed Enabled");
    public static final Component SELF_FEED_DISABLED = addTranslation("gui", EnderIOBase.loc("self_feed.disabled"), "Self Feed Disabled");
    public static final Component FLUID_CONDUIT_CHANGE_FLUID1 = addTranslation("gui", EnderIOBase.loc("fluid_conduit.change_fluid1"), "Locked Fluid:");
    public static final Component FLUID_CONDUIT_CHANGE_FLUID2 = addTranslation("gui", EnderIOBase.loc("fluid_conduit.change_fluid2"), "Click to reset!");
    public static final MutableComponent FLUID_CONDUIT_CHANGE_FLUID3 = addTranslation("gui", EnderIOBase.loc("fluid_conduit.change_fluid3"), "Fluid: %s");

    public static final MutableComponent TANK_EMPTY_STRING = addTranslation("tooltip", EnderIOBase.loc("fluid_tank.tank_empty_tooltip"), "Empty tank");
    public static final MutableComponent FLUID_TANK_TOOLTIP = addTranslation("tooltip", EnderIOBase.loc("fluid_tank.tank_tooltip"), "%d/%d mb of %s");//[amount]/[capacity] mb of [FluidName]
    // endregion

    // region Capacitors

    public static final MutableComponent CAPACITOR_TOOLTIP_BASE = addTranslation("tooltip", EnderIOBase.loc("capacitor.base"), "Base Modifier: %s");

    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_CAPACITY = addTranslation("tooltip", CapacitorModifier.ENERGY_CAPACITY.modifierId, "Energy Capacity Modifier: %s");
    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_USE = addTranslation("tooltip", CapacitorModifier.ENERGY_USE.modifierId, "Energy Use Modifier: %s");
    public static final MutableComponent CAPACITOR_TOOLTIP_FUEL_EFFICIENCY = addTranslation("tooltip", CapacitorModifier.FUEL_EFFICIENCY.modifierId, "Fuel Efficiency Modifier: %s");
    public static final MutableComponent CAPACITOR_TOOLTIP_BURNING_ENERGY_GENERATION = addTranslation("tooltip", CapacitorModifier.BURNING_ENERGY_GENERATION.modifierId, "Burning Energy Generation Modifier: %s");

    // region Loot Capacitor

    // Declares order. In this case: Modifier Quality, Modifier Type, Base Quality. Takes first modifier.
    public static final MutableComponent LOOT_CAPACITOR_NAME = addTranslation("item", EnderIOBase.loc("loot_capacitor_template"), "%s %s %s");

    public static final MutableComponent LOOT_CAPACITOR_BASE_DUD = addTranslation("item", EnderIOBase.loc("loot_capacitor_dud"), "Capacitor Dud");
    public static final MutableComponent LOOT_CAPACITOR_BASE_NORMAL = addTranslation("item", EnderIOBase.loc("loot_capacitor_normal"), "Capacitor");
    public static final MutableComponent LOOT_CAPACITOR_BASE_ENHANCED = addTranslation("item", EnderIOBase.loc("loot_capacitor_enhanced"), "Enhanced Capacitor");
    public static final MutableComponent LOOT_CAPACITOR_BASE_WONDER = addTranslation("item", EnderIOBase.loc("loot_capacitor_wonder"), "Wonder Capacitor");
    public static final MutableComponent LOOT_CAPACITOR_BASE_IMPOSSIBLE = addTranslation("item", EnderIOBase.loc("loot_capacitor_impossible"), "Impossible Capacitor");

    public static final MutableComponent LOOT_CAPACITOR_TYPE_ENERGY_CAPACITY = addTranslation("item", EnderIOBase.loc("loot_capacitor_energy_capacity"), "Insatiable");
    public static final MutableComponent LOOT_CAPACITOR_TYPE_ENERGY_USE = addTranslation("item", EnderIOBase.loc("loot_capacitor_energy_use"), "Hungry");
    public static final MutableComponent LOOT_CAPACITOR_TYPE_FUEL_EFFICIENCY = addTranslation("item", EnderIOBase.loc("loot_capacitor_fuel_efficiency"), "Efficient");
    public static final MutableComponent LOOT_CAPACITOR_TYPE_BURNING_ENERGY_GENERATION = addTranslation("item", EnderIOBase.loc("loot_capacitor_burning_energy_generation"), "Hot");
    public static final MutableComponent LOOT_CAPACITOR_TYPE_UNKNOWN = addTranslation("item", EnderIOBase.loc("loot_capacitor_unknown"), "Mystery");

    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_FAILED = addTranslation("item", EnderIOBase.loc("loot_capacitor_modifier_failed"), "Failed");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_SIMPLE = addTranslation("item", EnderIOBase.loc("loot_capacitor_modifier_simple"), "Simple");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_NICE = addTranslation("item", EnderIOBase.loc("loot_capacitor_modifier_nice"), "Nice");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_GOOD = addTranslation("item", EnderIOBase.loc("loot_capacitor_modifier_good"), "Good");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_ENHANCED = addTranslation("item", EnderIOBase.loc("loot_capacitor_modifier_enhanced"), "Enhanced");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_PREMIUM = addTranslation("item", EnderIOBase.loc("loot_capacitor_modifier_premium"), "Premium");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_INCREDIBLY = addTranslation("item", EnderIOBase.loc("loot_capacitor_modifier_incredibly"), "Incredibly");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_UNSTABLE = addTranslation("item", EnderIOBase.loc("loot_capacitor_modifier_unstable"), "Unstable");

    // endregion

    // endregion

    public static final Component GLIDER_DISABLED = addTranslation("message", EnderIOBase.loc("glider.disable"), "Gliding is disabled: ");
    public static final Component GLIDER_DISABLED_FALL_FLYING = addTranslation("message", EnderIOBase.loc("glider.disable.fall_flying"), "Elytra Flight");

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
        return TooltipUtil.style(addTranslation("enchantment", EnderIOBase.loc(String.format("%s.%s", enchantmentName, suffix)), description));
    }

    // endregion

    //region Filters

    public static final Component FILTER = addTranslation("gui", EnderIOBase.loc("filter"), "Filter");
    public static final Component CONFIRM = addTranslation("gui", EnderIOBase.loc("confirm"), "Confirm");

    // endregion

    // region Graves

    public static final Component GRAVE_WRONG_OWNER = addTranslation("message", EnderIOBase.loc("grave.wrong_owner"), "This grave is not yours! You cannot open it.");

    // endregion

    // region Grinding balls

    public static final MutableComponent GRINDINGBALL_MAIN_OUTPUT = addTranslation("tooltip", EnderIOBase.loc("grinding_ball_main_output"), "Main Output %s%%");
    public static final MutableComponent GRINDINGBALL_BONUS_OUTPUT = addTranslation("tooltip", EnderIOBase.loc("grinding_ball_bonus_output"), "Bonus Output %s%%");
    public static final MutableComponent GRINDINGBALL_POWER_USE = addTranslation("tooltip", EnderIOBase.loc("grinding_ball_power_use"), "Power Use %s%%");

    // endregion

    // region GUI

    public static final MutableComponent ENERGY_AMOUNT = addTranslation("info", EnderIOBase.loc("energy.amount"), "%s \u00B5I");
    public static final Component RANGE = addTranslation("gui", EnderIOBase.loc("range"), "Range");
    public static final Component SHOW_RANGE = addTranslation("gui", EnderIOBase.loc("range.show"), "Show Range");
    public static final Component HIDE_RANGE = addTranslation("gui", EnderIOBase.loc("range.hide"), "Hide Range");
    public static final Component WHITELIST_FILTER = addTranslation("gui", EnderIOBase.loc("filter.whitelist"), "Whitelist");
    public static final Component BLACKLIST_FILTER = addTranslation("gui", EnderIOBase.loc("filter.blacklist"), "BlackList");
    public static final Component NBT_FILTER = addTranslation("gui", EnderIOBase.loc("filter.nbt"), "Match NBT");
    public static final Component NO_NBT_FILTER = addTranslation("gui", EnderIOBase.loc("filter.nonbt"), "Ignore NBT");

    public static final Component IOCONFIG = addTranslation("gui", EnderIOBase.loc("ioconfig"), "IO Configuration");
    public static final Component TOGGLE_NEIGHBOUR = addTranslation("gui", EnderIOBase.loc("ioconfig.neighbour"), "Show/Hide Neighbours");

    public static final Component PUSH = addTranslation("gui", EnderIOBase.loc("ioconfig.push"), "Push");
    public static final Component PULL = addTranslation("gui", EnderIOBase.loc("ioconfig.pull"), "Pull");
    public static final Component BOTH = addTranslation("gui", EnderIOBase.loc("ioconfig.both"), "Push / Pull");
    public static final Component DISABLED = addTranslation("gui", EnderIOBase.loc("ioconfig.disabled"), "Disabled");
    public static final Component NONE = addTranslation("gui", EnderIOBase.loc("ioconfig.none"), "None");

    public static final MutableComponent NOCAP_TITLE = addTranslation("gui", EnderIOBase.loc("nocap.title"), "Capacitor Missing");
    public static final MutableComponent NOCAP_DESC = addTranslation("gui", EnderIOBase.loc("nocap.desc"), "Insert any capacitor so \n this machine can work!");

    public static Component VISIBLE = addTranslation("gui", EnderIOBase.loc("visible.true"), "Visible");
    public static Component NOT_VISIBLE = addTranslation("gui", EnderIOBase.loc("visible.false"), "Hidden");

    // endregion

    // region Entity Storage

    public static final MutableComponent TOOLTIP_NO_SOULBOUND = addTranslation("gui", EnderIOBase.loc("no_soulbound"), "Bind a soul to the item to be able to use it");

    // endregion

    // region Glider

    public static final Component USE_GLIDER_ADVANCEMENT_TITLE = addTranslation("advancements", EnderIOBase.loc("use_glider.title"), "Majestic");
    public static final Component USE_GLIDER_ADVANCEMENT_DESCRIPTION = addTranslation("advancements", EnderIOBase.loc("use_glider.description"),
        "Do you really trust some leather?");

    public static final Component RICH_ADVANCEMENT_TITLE = addTranslation("advancements", EnderIOBase.loc("rich.title"), "Don't tell the others");
    public static final Component RICH_ADVANCEMENT_DESCRIPTION = addTranslation("advancements", EnderIOBase.loc("rich.description"),
        "Make others think you are rich");
    public static final Component RICHER_ADVANCEMENT_TITLE = addTranslation("advancements", EnderIOBase.loc("richer.title"), "Is this real?");
    public static final Component RICHER_ADVANCEMENT_DESCRIPTION = addTranslation("advancements", EnderIOBase.loc("richer.description"),
        "Make others think you are richer");

    // endregion

    // region Misc Tooltips

    public static final MutableComponent SHOW_DETAIL_TOOLTIP = addTranslation("tooltip", EnderIOBase.loc("gui.show_advanced_tooltip"), "<Hold Shift>");

    // endregion
    
    // region Guidebook
    
    public static final Component GUIDEBOOK_TITLE = addTranslation("guidebook", EnderIOBase.loc("book_title"), "Book Title");
    public static final Component GUIDEBOOK_LANDING_TEXT = addTranslation("guidebook", EnderIOBase.loc("landing_text"), "Landing Text");

    //endregion

    // region JEI

    public static final Component JEI_FIRE_CRAFTING_TITLE = addTranslation("jei", EnderIOBase.loc("fire_crafting"), "title", "Fire Crafting");
    public static final Component JEI_FIRE_CRAFTING_VALID_BLOCKS = addTranslation("jei", EnderIOBase.loc("fire_crafting"), "valid_blocks", "Valid Blocks:");
    public static final Component JEI_FIRE_CRAFTING_VALID_DIMENSIONS = addTranslation("jei", EnderIOBase.loc("fire_crafting"), "valid_dimensions", "Valid Dimensions:");
    public static final Component JEI_FIRE_CRAFTING_LOOT_TABLE = addTranslation("jei", EnderIOBase.loc("fire_crafting"), "loot_table", "Loot Table:");
    public static final Component JEI_FIRE_CRAFTING_MAX_DROPS = addTranslation("jei", EnderIOBase.loc("fire_crafting"), "max_drops", "Max Item Drops:");

    // endregion

    // region Glass Names

    private static void registerGlassLang() {
        for (var lighting : GlassLighting.values()) {
            String lightingName = lighting != GlassLighting.NONE ? lighting.englishName() + " " : "";
            String lightingKeyName = lighting != GlassLighting.NONE ? "_" + lighting.shortName() : "";

            addTranslation("block", EnderIOBase.loc("clear_glass" + lightingKeyName),
                lightingName + "Clear Glass");
            addTranslation("block", EnderIOBase.loc("fused_quartz" + lightingKeyName),
                lightingName + "Fused Quartz");

            for (var color : DyeColor.values()) {
                String colorName = createEnglishPrefix(color);

                addTranslation("block", EnderIOBase.loc("clear_glass" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT)),
                    colorName + lightingName + "Clear Glass");
                addTranslation("block", EnderIOBase.loc("fused_quartz" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT)),
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
        return EnderIOBase.REGILITE.lang().add(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name, String translation) {
        return EnderIOBase.REGILITE.lang().add(prefix, ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath() + "/" + name), translation);
    }

    public static void register() {
        registerGlassLang();
    }
}
