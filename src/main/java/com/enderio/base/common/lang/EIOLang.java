package com.enderio.base.common.lang;

import com.enderio.EnderIO;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.core.common.util.TooltipUtil;
import com.tterrag.registrate.Registrate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

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

    public static final Component COORDINATE_SELECTOR_NO_PAPER = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_paper"), "No Paper in Inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_block"), "No Block in Range");


    public static final Component REDSTONE_MODE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.mode"), "Redstone Mode");
    public static final Component REDSTONE_ALWAYS_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.always_active"), "Always active");
    public static final Component REDSTONE_ACTIVE_WITH_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_with_signal"), "Active with signal");
    public static final Component REDSTONE_ACTIVE_WITHOUT_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_without_signal"), "Active without signal");
    public static final Component REDSTONE_NEVER_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.never_active"), "Never active");

    // endregion

    // region Dark Steel

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
    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_TRANSFER = REGISTRATE.addLang("tooltip", CapacitorModifier.ENERGY_TRANSFER.id, "Energy Transfer Modifier: %s");
    
    // endregion

    // region Enchantments

    public static final Component AUTO_SMELT_DESC = enchantmentDescription("auto_smelt", "Automatically smeltes whatever is mined");
    public static final Component REPELLENT_DESC = enchantmentDescription("repellent",
        "Chance to teleport attackers away\nHigher levels teleport more often and farther");
    public static final Component SHIMMER_DESC = enchantmentDescription("shimmer",
        "Makes the item shimmer as if it was enchanted.\nThat's all.\nReally.\nNothing more.\nYes, it is useless.\nI know.");
    public static final Component SOULBOUND_DESC = enchantmentDescription("soulbound",
        "Prevents item from being lost on death.\nNote: Most gravestone mods are stupid and prevent this from working!");
    public static final Component WITHERING_BLADE_DESC = enchantmentDescription("withering_blade",
        "Applies wither to the target\nApplies to bladed weapons");
    public static final Component WITHERING_ARROW_DESC = enchantmentDescription("withering_arrow",
        "Applies wither to the target\nApplies to bows.");
    public static final Component WITHERING_BOLT_DESC = enchantmentDescription("withering_bolt",
        "Applies wither to the target\nApplies to crossbows.");
    public static final Component XP_BOOST_DESC = enchantmentDescription("xp_boost", "Extra XP from mobs and blocks");

    private static Component enchantmentDescription(String enchantmentname, String description) {
        return TooltipUtil.style(REGISTRATE.addLang("description", EnderIO.loc("enchantment." + enchantmentname), description));
    }

    // endregion

    //region Filters

    public static final Component FILTER = REGISTRATE.addLang("gui", EnderIO.loc("filter"), "Filter");

    // endregion

    // region Graves

    public static final Component GRAVE_WRONG_OWNER = REGISTRATE.addLang("message", EnderIO.loc("grave.wrong_owner"),
        "This grave is not yours! You cannot open it.");

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
    // endregion

    // region Misc Tooltips

    public static final MutableComponent SHOW_DETAIL_TOOLTIP = REGISTRATE.addLang("tooltip", EnderIO.loc("gui.show_advanced_tooltip"), "<Hold Shift>");

    // endregion

    // region Guidebook

    public static final Component GUIDEBOOK_TITLE = REGISTRATE.addLang("guidebook", EnderIO.loc("book_title"), "Book Title");
    public static final Component GUIDEBOOK_LANDING_TEXT = REGISTRATE.addLang("guidebook", EnderIO.loc("landing_text"), "Landing Text");

    // endregion

    public static void register() {}
}
