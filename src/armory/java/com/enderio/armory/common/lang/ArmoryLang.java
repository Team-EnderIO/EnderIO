package com.enderio.armory.common.lang;

import com.enderio.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ArmoryLang {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region Dark Steel

    public static final MutableComponent HEAD_DROP_CHANCE = REGISTRATE.addLang("info", EnderIO.loc("headchance"), "%s%% chance to drop a mob head");
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

    public static void register() {
    }
}
