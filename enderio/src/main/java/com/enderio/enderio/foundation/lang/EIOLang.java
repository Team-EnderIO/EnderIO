package com.enderio.enderio.foundation.lang;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.content.glass.GlassLighting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Locale;

public class EIOLang {
    public static final Component BLOCK_BLAST_RESISTANT = TooltipUtil
            .style(addTranslation("tooltip", EnderIO.rl("block.blast_resistant"), "Blast resistant"));

    // region GUI Interactions

    public static final Component OK = addTranslation("gui", EnderIO.rl("ok"), "Ok");
    public static final Component CANCEL = addTranslation("gui", EnderIO.rl("cancel"), "Cancel");

    // endregion

    // region Fused Quartz

    public static final Component FUSED_QUARTZ_EMITS_LIGHT = TooltipUtil
            .style(addTranslation("tooltip", EnderIO.rl("fused_quartz.emits_light"), "Emits light"));
    public static final Component FUSED_QUARTZ_BLOCKS_LIGHT = TooltipUtil
            .style(addTranslation("tooltip", EnderIO.rl("fused_quartz.blocks_light"), "Blocks light"));

    // endregion

    // region Items

    public static final Component DARK_STEEL_LADDER_FASTER = TooltipUtil
            .style(addTranslation("tooltip", EnderIO.rl("dark_steel_ladder.faster"), "Faster than regular ladders"));

    public static final Component SOUL_VIAL_ERROR_PLAYER = addTranslation("message",
            EnderIO.rl("soul_vial.error_player"), "You cannot put player in a bottle!");
    public static final Component SOUL_VIAL_ERROR_BOSS = addTranslation("message", EnderIO.rl("soul_vial.error_boss"),
            "Nice try. Bosses don't like bottles.");
    public static final Component SOUL_VIAL_ERROR_BLACKLISTED = addTranslation("message",
            EnderIO.rl("soul_vial.error_blacklisted"), "This entity has been blacklisted.");
    public static final Component SOUL_VIAL_ERROR_FAILED = addTranslation("message",
            EnderIO.rl("soul_vial.error_failed"), "This entity cannot be captured.");
    public static final Component SOUL_VIAL_ERROR_DEAD = addTranslation("message", EnderIO.rl("soul_vial.error_dead"),
            "Cannot capture a dead mob!");
    public static final MutableComponent SOUL_VIAL_TOOLTIP_HEALTH = addTranslation("tooltip",
            EnderIO.rl("soul_vial.health"), "Health: %s/%s");

    public static final Component COORDINATE_SELECTOR_NO_PAPER = addTranslation("info",
            EnderIO.rl("coordinate_selector.no_paper"), "No paper in inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = addTranslation("info",
            EnderIO.rl("coordinate_selector.no_block"), "No block in range");
    public static final Component TOO_MANY_LEVELS = addTranslation("info", EnderIO.rl("too_many_levels"),
            "You have more than 21862 levels, that's too much XP.");

    public static final Component REDSTONE_MODE = addTranslation("gui", EnderIO.rl("redstone.mode"), "Redstone Mode");

    public static final MutableComponent TANK_EMPTY_STRING = addTranslation("tooltip",
            EnderIO.rl("fluid_tank.tank_empty_tooltip"), "Empty tank");
    public static final MutableComponent FLUID_TANK_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("fluid_tank.tank_tooltip"), "%d/%d mb of %s");// [amount]/[capacity] mb of [FluidName]

    public static final MutableComponent CONFIGURED = TooltipUtil
            .style(addTranslation("tooltip", EnderIO.rl("configured"), "Configured"));
    public static final MutableComponent FILTER_CONFIG_NOT_ALLOWED_COMPONENT_MATCH = addTranslation("tooltip",
            EnderIO.rl("filter.not_allowed_component_match"),
            "This filter uses component matching which is no longer available to this item. Clear this filter using the crafting grid to remove this warning.")
                    .withStyle(ChatFormatting.RED);

    // endregion

    public static final Component GLIDER_DISABLED = addTranslation("message", EnderIO.rl("glider.disable"),
            "Gliding is disabled: ");
    public static final Component GLIDER_DISABLED_FALL_FLYING = addTranslation("message",
            EnderIO.rl("glider.disable.fall_flying"), "Elytra Flight");

    // region Filters

    public static final Component FILTER = addTranslation("gui", EnderIO.rl("filter"), "Filter");
    public static final Component CONFIRM = addTranslation("gui", EnderIO.rl("confirm"), "Confirm");

    // endregion

    // region Graves

    public static final Component GRAVE_WRONG_OWNER = addTranslation("message", EnderIO.rl("grave.wrong_owner"),
            "This grave is not yours! You cannot open it.");

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

    public static final MutableComponent ENERGY_AMOUNT = addTranslation("info", EnderIO.rl("energy.amount"),
            "%s \u00B5I");
    public static final Component RANGE = addTranslation("gui", EnderIO.rl("range"), "Range");
    public static final Component MAX_RANGE = addTranslation("gui", EnderIO.rl("max_range"), "Maximum Range");
    public static final Component SHOW_RANGE = addTranslation("gui", EnderIO.rl("range.show"), "Show Range");
    public static final Component HIDE_RANGE = addTranslation("gui", EnderIO.rl("range.hide"), "Hide Range");
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

    public static final Component IOCONFIG = addTranslation("gui", EnderIO.rl("ioconfig"), "IO Configuration");
    public static final Component TOGGLE_NEIGHBOUR = addTranslation("gui", EnderIO.rl("ioconfig.neighbour"),
            "Show/Hide Neighbours");

    public static final Component PUSH = addTranslation("gui", EnderIO.rl("ioconfig.push"), "Push");
    public static final Component PULL = addTranslation("gui", EnderIO.rl("ioconfig.pull"), "Pull");
    public static final Component BOTH = addTranslation("gui", EnderIO.rl("ioconfig.both"), "Push / Pull");
    public static final Component DISABLED = addTranslation("gui", EnderIO.rl("ioconfig.disabled"), "Disabled");
    public static final Component NONE = addTranslation("gui", EnderIO.rl("ioconfig.none"), "None");

    public static final MutableComponent NOCAP_TITLE = addTranslation("gui", EnderIO.rl("nocap.title"),
            "Capacitor Missing");
    public static final MutableComponent NOCAP_DESC = addTranslation("gui", EnderIO.rl("nocap.desc"),
            "Insert any capacitor so \n this machine can work!");

    public static final Component VISIBLE = addTranslation("gui", EnderIO.rl("visible.true"), "Visible");
    public static final Component NOT_VISIBLE = addTranslation("gui", EnderIO.rl("visible.false"), "Hidden");

    // endregion

    // region Entity Storage

    public static final MutableComponent TOOLTIP_SOULBOUND = addTranslation("gui", EnderIO.rl("soulbound"),
            "Bound Soul: ");

    public static final MutableComponent TOOLTIP_NO_SOULBOUND = addTranslation("gui", EnderIO.rl("no_soulbound"),
            "This item can have a soul bound to it.");

    // endregion

    // region Glider

    public static final Component USE_GLIDER_ADVANCEMENT_TITLE = addTranslation("advancements",
            EnderIO.rl("use_glider.title"), "Majestic");
    public static final Component USE_GLIDER_ADVANCEMENT_DESCRIPTION = addTranslation("advancements",
            EnderIO.rl("use_glider.description"), "Do you really trust some leather?");

    public static final Component RICH_ADVANCEMENT_TITLE = addTranslation("advancements", EnderIO.rl("rich.title"),
            "Don't tell the others");
    public static final Component RICH_ADVANCEMENT_DESCRIPTION = addTranslation("advancements",
            EnderIO.rl("rich.description"), "Make others think you are rich");
    public static final Component RICHER_ADVANCEMENT_TITLE = addTranslation("advancements", EnderIO.rl("richer.title"),
            "Is this real?");
    public static final Component RICHER_ADVANCEMENT_DESCRIPTION = addTranslation("advancements",
            EnderIO.rl("richer.description"), "Make others think you are richer");

    // endregion

    // region Lore

    public static final Component SUSPICIOUS_SEED_LORE = addTranslation("item", EnderIO.rl("suspicious_seed.lore"),
            "The seed appears to interact with nearby experience orbs...").withStyle(ChatFormatting.DARK_GRAY,
                    ChatFormatting.ITALIC);

    // endregion

    // region Misc Tooltips

    public static final Component SHOW_DETAIL_TOOLTIP = addTranslation("tooltip",
            EnderIO.rl("gui.show_advanced_tooltip"), "<Hold Shift>").withStyle(ChatFormatting.GRAY,
                    ChatFormatting.ITALIC);

    // endregion

    // region Guidebook

    public static final Component GUIDEBOOK_TITLE = addTranslation("guidebook", EnderIO.rl("book_title"),
            "Book Title");
    public static final Component GUIDEBOOK_LANDING_TEXT = addTranslation("guidebook", EnderIO.rl("landing_text"),
            "Landing Text");

    // endregion

    // region JEI

    public static final Component JEI_FIRE_CRAFTING_TITLE = addTranslation("jei", EnderIO.rl("fire_crafting"), "title",
            "Fire Crafting");
    public static final Component JEI_FIRE_CRAFTING_VALID_BLOCKS = addTranslation("jei", EnderIO.rl("fire_crafting"),
            "valid_blocks", "Valid Blocks:");
    public static final Component JEI_FIRE_CRAFTING_VALID_DIMENSIONS = addTranslation("jei",
            EnderIO.rl("fire_crafting"), "valid_dimensions", "Valid Dimensions:");
    public static final MutableComponent JEI_FIRE_CRAFTING_CHANCE = addTranslation("jei", EnderIO.rl("fire_crafting"),
            "chance", "%s%% Chance");
    public static final MutableComponent JEI_FIRE_CRAFTING_DROPS = addTranslation("jei", EnderIO.rl("fire_crafting"),
            "drops", "Drops %s");

    // endregion

    // region Glass Names

    private static void registerGlassLang() {
        for (var lighting : GlassLighting.values()) {
            String lightingName = lighting != GlassLighting.NONE ? lighting.englishName() + " " : "";
            String lightingKeyName = lighting != GlassLighting.NONE ? "_" + lighting.shortName() : "";

            addTranslation("block", EnderIO.rl("clear_glass" + lightingKeyName), lightingName + "Clear Glass");
            addTranslation("block", EnderIO.rl("fused_quartz" + lightingKeyName), lightingName + "Fused Quartz");

            for (var color : DyeColor.values()) {
                String colorName = createEnglishPrefix(color);

                addTranslation("block",
                        EnderIO.rl("clear_glass" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT)),
                        colorName + lightingName + "Clear Glass");
                addTranslation("block",
                        EnderIO.rl("fused_quartz" + lightingKeyName + "_" + color.getName().toLowerCase(Locale.ROOT)),
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
        return EnderIO.REGILITE.addTranslation(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name,
            String translation) {
        return EnderIO.REGILITE.addTranslation(prefix,
                ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath() + "." + name), translation);
    }

    public static void register() {
        registerGlassLang();
    }
}
