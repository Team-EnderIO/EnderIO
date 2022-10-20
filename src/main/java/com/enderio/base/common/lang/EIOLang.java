package com.enderio.base.common.lang;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.base.common.util.RegLangUtil;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EIOLang extends RegLangUtil {
    public static final Component BLOCK_BLAST_RESISTANT = TooltipUtil.style(lang("tooltip", "block.blast_resistant", "Blast resistant"));

    // region Fused Quartz

    public static final Component FUSED_QUARTZ_EMITS_LIGHT = TooltipUtil.style(lang("tooltip", "fused_quartz.emits_light", "Emits light"));
    public static final Component FUSED_QUARTZ_BLOCKS_LIGHT = TooltipUtil.style(lang("tooltip", "fused_quartz.blocks_light", "Blocks light"));

    public static final Component GLASS_COLLISION_PLAYERS_PASS = TooltipUtil.style(lang("tooltip", "collision.players_pass", "Not solid to players"));
    public static final Component GLASS_COLLISION_PLAYERS_BLOCK = TooltipUtil.style(lang("tooltip", "collision.players_block", "Only solid to players"));
    public static final Component GLASS_COLLISION_MOBS_PASS = TooltipUtil.style(lang("tooltip", "collision.mobs_pass", "Not solid to monsters"));
    public static final Component GLASS_COLLISION_MOBS_BLOCK = TooltipUtil.style(lang("tooltip", "collision.mobs_block", "Only solid to monsters"));
    public static final Component GLASS_COLLISION_ANIMALS_PASS = TooltipUtil.style(lang("tooltip", "collision.animals_pass", "Not solid to animals"));
    public static final Component GLASS_COLLISION_ANIMALS_BLOCK = TooltipUtil.style(lang("tooltip", "collision.animals_block", "Only solid to animals"));

    // endregion

    // region Items

    public static final Component DARK_STEEL_LADDER_FASTER = TooltipUtil.style(lang("tooltip", "dark_steel_ladder.faster", "Faster than regular ladders"));

    public static final Component SOUL_VIAL_ERROR_PLAYER = lang("message", "soul_vial.error_player", "You cannot put player in a bottle!");
    public static final Component SOUL_VIAL_ERROR_BOSS = lang("message", "soul_vial.error_boss", "Nice try. Bosses don't like bottles.");
    public static final Component SOUL_VIAL_ERROR_BLACKLISTED = lang("message", "soul_vial.error_blacklisted", "This entity has been blacklisted.");
    public static final Component SOUL_VIAL_ERROR_FAILED = lang("message", "soul_vial.error_failed", "This entity cannot be captured.");
    public static final Component SOUL_VIAL_ERROR_DEAD = lang("message", "soul_vial.error_dead", "Cannot capture a dead mob!");
    public static final MutableComponent SOUL_VIAL_TOOLTIP_HEALTH = lang("tooltip", "soul_vial.health", "Health: %s/%s");

    public static final Component COORDINATE_SELECTOR_NO_PAPER = lang("info", "coordinate_selector.no_paper", "No Paper in Inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = lang("info", "coordinate_selector.no_block", "No Block in Range");

    public static final Component REDSTONE_MODE = lang("gui", "redstone.mode", "Redstone Mode");
    public static final Component REDSTONE_ALWAYS_ACTIVE = lang("gui", "redstone.always_active", "Always active");
    public static final Component REDSTONE_ACTIVE_WITH_SIGNAL = lang("gui", "redstone.active_with_signal", "Active with signal");
    public static final Component REDSTONE_ACTIVE_WITHOUT_SIGNAL = lang("gui", "redstone.active_without_signal", "Active without signal");
    public static final Component REDSTONE_NEVER_ACTIVE = lang("gui", "redstone.never_active", "Never active");

    // endregion

    // region Dark Steel

    public static final MutableComponent ENERGY_AMOUNT = lang("info", "energy.amount", "%s \u00B5I");
    public static final MutableComponent DURABILITY_AMOUNT = lang("info", "durability.amount", "Durability %s");

    public static final MutableComponent DS_UPGRADE_XP_COST = lang("info", "darksteel.upgrade.cost", "Costs %s Levels");
    public static final Component DS_UPGRADE_ACTIVATE = lang("info", "darksteel.upgrade.activate", "Right Click to Activate");
    public static final Component DS_UPGRADE_ITEM_NO_XP = lang("info", "darksteel.upgrade.no_xp", "Not enough XP");
    public static final Component DS_UPGRADE_AVAILABLE = lang("info", "darksteel.upgrade.available", "Available Upgrades").withStyle(ChatFormatting.YELLOW);

    public static final Component DS_UPGRADE_EMPOWERED_I = lang("info", "darksteel.upgrade.empowered_l1", "Empowered");
    public static final Component DS_UPGRADE_EMPOWERED_II = lang("info", "darksteel.upgrade.empowered_l2", "Empowered II");
    public static final Component DS_UPGRADE_EMPOWERED_III = lang("info", "darksteel.upgrade.empowered_l3", "Empowered III");
    public static final Component DS_UPGRADE_EMPOWERED_IV = lang("info", "darksteel.upgrade.empowered_l4", "Empowered IV");
    public static final Component DS_UPGRADE_EMPOWERED_DESCRIPTION = lang("info", "darksteel.upgrade.empowered.description",
        "Infuse the steel with the power of Micro Infinity");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_STORAGE = lang("info", "darksteel.upgrade.empowered.storage", "Holds up to %s \u00B5I");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_DAMAGE_ABSORPTION = lang("info", "darksteel.upgrade.empowered.absorption",
        "%s%% damage absorbed by \u00B5I");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_EFFICIENCY = lang("info", "darksteel.upgrade.empowered.efficiency",
        "Efficiency +%s when powered");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_OBSIDIAM_EFFICIENCY = lang("info", "darksteel.upgrade.empowered.obsidian.efficiency",
        "Efficiency +%s when breaking obsidian");

    public static final Component DS_UPGRADE_SPOON = lang("info", "darksteel.upgrade.spoon", "Spoon");
    public static final Component DS_UPGRADE_SPOON_DESCRIPTION = lang("info", "darksteel.upgrade.spoon.description",
        "Who needs a shovel when you have a spoon?");

    public static final Component DS_UPGRADE_FORK = lang("info", "darksteel.upgrade.fork", "Fork");
    public static final Component DS_UPGRADE_FORK_DESCRIPTION = lang("info", "darksteel.upgrade.fork.description", "Who needs a hoe when you have a fork?");

    public static final Component DS_UPGRADE_DIRECT = lang("info", "darksteel.upgrade.direct", "Direct");
    public static final Component DS_UPGRADE_DIRECT_DESCRIPTION = lang("info", "darksteel.upgrade.direct.description",
        "Teleports harvested items directly into your inventory");

    public static final Component DS_UPGRADE_EXPLOSIVE_I = lang("info", "darksteel.upgrade.explosive_l1", "Explosive I");
    public static final Component DS_UPGRADE_EXPLOSIVE_II = lang("info", "darksteel.upgrade.explosive_l2", "Explosive II");
    public static final Component DS_UPGRADE_EXPLOSIVE_DESCRIPTION = lang("info", "darksteel.upgrade.explosive.description",
        "Makes surrounding dirt and rock go splodey");

    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_I = lang("info", "darksteel.upgrade.explosive_penetration_l1", "Explosive Penetration I");
    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_II = lang("info", "darksteel.upgrade.explosive_penetration_l2", "Explosive Penetration II");
    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_DESCRIPTION = lang("info", "darksteel.upgrade.explosive_penetration.description",
        "Makes dirt and rock behind the mined block go splodey");

    // endregion

    // region Capacitors

    public static final MutableComponent CAPACITOR_TOOLTIP_BASE = lang("tooltip", "capacitor.base", "Base Modifier: %s");

    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_CAPACITY = REGISTRATE.addLang("tooltip", CapacitorModifier.ENERGY_CAPACITY.id,
        "Energy Capacity Modifier: %s");
    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_USE = REGISTRATE.addLang("tooltip", CapacitorModifier.ENERGY_USE.id,
        "Energy Use Modifier: %s");
    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_TRANSFER = REGISTRATE.addLang("tooltip", CapacitorModifier.ENERGY_TRANSFER.id,
        "Energy Transfer Modifier: %s");

    // endregion

    // region Enchantments

    public static final Component AUTO_SMELT_DESC = enchantmentDescription("auto_smelt", "Automatically smeltes whatever is mined");
    public static final Component REPELLENT_DESC = enchantmentDescription("repellent",
        "Chance to teleport attackers away\nHigher levels teleport more often and farther");
    public static final Component SHIMMER_DESC = enchantmentDescription("shimmer",
        "Makes the item shimmer as if it was enchanted.\nThat's all.\nReally.\nNothing more.\nYes, it is useless.\nI know.");
    public static final Component SOULBOUND_DESC = enchantmentDescription("soulbound",
        "Prevents item from being lost on death.\nNote: Most gravestone mods are stupid and prevent this from working!");
    public static final Component WITHERING_BLADE_DESC = enchantmentDescription("withering_blade", "Applies wither to the target\nApplies to bladed weapons");
    public static final Component WITHERING_ARROW_DESC = enchantmentDescription("withering_arrow", "Applies wither to the target\nApplies to bows.");
    public static final Component WITHERING_BOLT_DESC = enchantmentDescription("withering_bolt", "Applies wither to the target\nApplies to crossbows.");
    public static final Component XP_BOOST_DESC = enchantmentDescription("xp_boost", "Extra XP from mobs and blocks");

    private static Component enchantmentDescription(String enchantmentName, String description) {
        return TooltipUtil.style(lang("description", "enchantment", enchantmentName, description));
    }

    // endregion

    // region Graves

    public static final Component GRAVE_WRONG_OWNER = lang("message", "grave.wrong_owner", "This grave is not yours! You cannot open it.");

    // endregion

    // region Misc Tooltips

    public static final MutableComponent SHOW_DETAIL_TOOLTIP = lang("tooltip", "gui.show_advanced_tooltip", "<Hold Shift>");

    // endregion

    // region Guidebook

    static {
        guideBook("book_title", "EnderIO Manual");
        guideBook("landing_text", "Welcome $(6)$(playername)$() to the world of $(3)EnderIO$(), the full-fat tech mod!");

        // region Gear Category

        guideBook("gear", "title", "Gear");
        guideBook("gear", "desc", "TODO: DESCRIPTION");

        guideBook("gear.cold_fire", "title", "Cold Fire");
        guideBook("gear.cold_fire", "desc", "TODO: DESCRIPTION");

        guideBook("gear.coordinate_selector", "title", "Coordinate Selector");
        guideBook("gear.coordinate_selector", "desc", "TODO: DESCRIPTION");

        guideBook("gear.dark_steel", "title", "Dark Steel Gear");
        guideBook("gear.dark_steel", "desc", "TODO: DESCRIPTION");

        guideBook("gear.electromagnet", "title", "Electromagnet");
        guideBook("gear.electromagnet", "desc", "TODO: DESCRIPTION");

        guideBook("gear.staff_of_levity", "title", "Staff of Levity");
        guideBook("gear.staff_of_levity", "desc", "TODO: DESCRIPTION");

        guideBook("gear.yeta_wrench", "title", "Yeta Wrench");
        guideBook("gear.yeta_wrench", "landing", """
            The $(3)Yeta Wrench$() is a tool used for seeing through $(l:enderio:conduit_facade)Conduit Facades$(/l) when being helf
            and can be used to disassemble machines in one click""");
        guideBook("gear.yeta_wrench", "crafting", "TODO: DESCRIPTION");

        // endregion
        // region Energy Category

        guideBook("energy", "title", "Energy");
        guideBook("energy", "desc", "TODO: DESCRIPTION");

        guideBook("energy.creative_power", "title", "Creative Power");
        guideBook("energy.creative_power", "landing", "TODO: LANDING");
        guideBook("energy.creative_power", "crafting", "TODO: CRAFTING");

        guideBook("energy.stirling_generator", "title", "Stirling Generator");
        guideBook("energy.stirling_generator", "landing", "TODO: LANDING");
        guideBook("energy.stirling_generator", "crafting", "TODO: CRAFTING");

        // endregion
        // region Conduits Category

        guideBook("conduits", "title", "Conduits");
        guideBook("conduits", "desc", "TODO: DESCRIPTION");

        guideBook("conduits.facade", "title", "Conduit Facades");
        guideBook("conduits.facade", "landing", "TODO: LANDING");
        guideBook("conduits.facade", "crafting", "TODO: CRAFTING");

        guideBook("conduits.redstone", "title", "Redstone Conduits");
        guideBook("conduits.redstone", "landing", "TODO: LANDING");
        guideBook("conduits.redstone", "crafting", "TODO: CRAFTING");

        guideBook("conduits.refined_storage", "title", "Refined Storage Conduits");
        guideBook("conduits.refined_storage", "landing", "TODO: LANDING");
        guideBook("conduits.refined_storage", "crafting", "TODO: CRAFTING");

        guideBook("conduits.item", "title", "Item Conduits");
        guideBook("conduits.item", "landing", "TODO: LANDING");
        guideBook("conduits.item", "crafting", "TODO: CRAFTING");

        guideBook("conduits.liquid", "title", "Liquid Conduits");
        guideBook("conduits.liquid", "landing", "TODO: LANDING");
        guideBook("conduits.liquid", "crafting", "TODO: CRAFTING");

        guideBook("conduits.gas", "title", "Gas Conduits");
        guideBook("conduits.gas", "landing", "TODO: LANDING");
        guideBook("conduits.gas", "crafting", "TODO: CRAFTING");

        guideBook("conduits.energy", "title", "Gas Conduits");
        guideBook("conduits.energy", "landing", "TODO: LANDING");
        guideBook("conduits.energy", "crafting", "TODO: CRAFTING");

        guideBook("conduits.applied_energistics", "title", "AE Conduits");
        guideBook("conduits.applied_energistics", "landing", "TODO: LANDING");
        guideBook("conduits.applied_energistics", "crafting", "TODO: CRAFTING");

        // endregion
        // region Misc Category

        guideBook("misc", "title", "Misc");
        guideBook("misc", "desc", "TODO: DESCRIPTION");

        guideBook("misc.equipment", "title", "Equipment");
        guideBook("misc.equipment", "landing", "TODO: DESCRIPTION");

        guideBook("misc.infinity_dust", "title", "Grains of Infinity");
        guideBook("misc.infinity_dust", "landing", "The gains of infinity is essential component!");
        guideBook("misc.infinity_dust", "heading", "TODO: HEADING");
        guideBook("misc.infinity_dust", "crafting", "TODO: CRAFTING");

        guideBook("misc.capacitors", "title", "Capacitors");
        guideBook("misc.capacitors", "landing", "TODO: DESCRIPTION");
        guideBook("misc.capacitors", "crafting", "TODO: CRAFTING");

        // endregion
    }

    //endregion

    public static void register() {}
}
