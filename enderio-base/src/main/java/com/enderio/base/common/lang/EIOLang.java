package com.enderio.base.common.lang;

import com.enderio.base.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class EIOLang {

    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region machines

    public static final Component REDSTONE_ALWAYS_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.always_active"), "Always active");
    public static final Component REDSTONE_ACTIVE_WITH_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_with_signal"), "Active with signal");
    public static final Component REDSTONE_ACTIVE_WITHOUT_SIGNAL = REGISTRATE.addLang("gui", EnderIO.loc("redstone.active_without_signal"), "Active without signal");
    public static final Component REDSTONE_NEVER_ACTIVE = REGISTRATE.addLang("gui", EnderIO.loc("redstone.never_active"), "Never active");

    // endregion

    // region items

    public static final Component COORDINATE_SELECTOR_NO_PAPER = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_paper"), "No Paper in Inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_block"), "No Block in Range");

    // endregion

    // region dark steel

    public static final TranslatableComponent ENERGY_AMOUNT = REGISTRATE.addLang("info", EnderIO.loc("energy.amount"), "%s \u00B5I");
    public static final TranslatableComponent DURABILITY_AMOUNT = REGISTRATE.addLang("info", EnderIO.loc("durability.amount"), "Durability %s");

    public static final TranslatableComponent DS_UPGRADE_XP_COST = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.cost"), "Costs %s Levels");
    public static final Component DS_UPGRADE_ACTIVATE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.activate"), "Right Click to Activate");
    public static final Component DS_UPGRADE_ITEM_NO_XP = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.no_xp"), "Not enough XP");
    public static final Component DS_UPGRADE_AVAILABLE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.available"), "Available Upgrades");

    public static final Component DS_UPGRADE_EMPOWERED_I = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered_l1"), "Empowered");
    public static final Component DS_UPGRADE_EMPOWERED_II = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered_l2"), "Empowered II");
    public static final Component DS_UPGRADE_EMPOWERED_III = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered_l3"), "Empowered III");
    public static final Component DS_UPGRADE_EMPOWERED_IV = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered_l4"), "Empowered IV");
    public static final Component DS_UPGRADE_EMPOWERED_DESCRIPTION = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.description"),
        "Infuse the steel with the power of Micro Infinity");
    public static final TranslatableComponent DS_UPGRADE_EMPOWERED_STORAGE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.storage"),
        "Holds up to %s \u00B5I");
    public static final TranslatableComponent DS_UPGRADE_EMPOWERED_DAMAGE_ABSORPTION = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.absorption"),
        "%s%% damage absorbed by \u00B5I");
    public static final TranslatableComponent DS_UPGRADE_EMPOWERED_EFFICIENCY = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.efficiency"),
        "Efficiency +%s when powered");
    public static final TranslatableComponent DS_UPGRADE_EMPOWERED_OBSIDIAM_EFFICIENCY = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered.obsidian.efficiency"),
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

    // endregion

    public static void register() {}
}
