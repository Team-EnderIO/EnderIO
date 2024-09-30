package com.enderio.armory.common.lang;

import com.enderio.EnderIOBase;
import com.enderio.armory.EnderIOArmory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ArmoryLang {

    // region Dark Steel

    public static final MutableComponent HEAD_DROP_CHANCE = addTranslation("info", EnderIOBase.loc("headchance"), "%s%% chance to drop a mob head");
    public static final MutableComponent DURABILITY_AMOUNT = addTranslation("info", EnderIOBase.loc("durability.amount"), "Durability %s");

    public static final MutableComponent DS_UPGRADE_XP_COST = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.cost"), "Costs %s Levels");
    public static final Component DS_UPGRADE_ACTIVATE = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.activate"), "Right Click to Activate");
    public static final Component DS_UPGRADE_ITEM_NO_XP = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.no_xp"), "Not enough XP");
    public static final Component DS_UPGRADE_AVAILABLE = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.available"), "Available Upgrades").withStyle(
        ChatFormatting.YELLOW);

    public static final Component DS_UPGRADE_EMPOWERED_I = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered_l1"), "Empowered");
    public static final Component DS_UPGRADE_EMPOWERED_II = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered_l2"), "Empowered II");
    public static final Component DS_UPGRADE_EMPOWERED_III = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered_l3"), "Empowered III");
    public static final Component DS_UPGRADE_EMPOWERED_IV = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered_l4"), "Empowered IV");
    public static final Component DS_UPGRADE_EMPOWERED_DESCRIPTION = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered.description"),
        "Infuse the steel with the power of Micro Infinity");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_STORAGE = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered.storage"),
        "Holds up to %s \u00B5I");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_DAMAGE_ABSORPTION = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered.absorption"),
        "%s%% damage absorbed by \u00B5I");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_EFFICIENCY = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered.efficiency"),
        "Efficiency +%s when powered");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_OBSIDIAM_EFFICIENCY = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.empowered.obsidian.efficiency"),
        "Efficiency +%s when breaking obsidian");

    public static final Component DS_UPGRADE_SPOON = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.spoon"), "Spoon");
    public static final Component DS_UPGRADE_SPOON_DESCRIPTION = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.spoon.description"),
        "Who needs a shovel when you have a spoon?");

    public static final Component DS_UPGRADE_FORK = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.fork"), "Fork");
    public static final Component DS_UPGRADE_FORK_DESCRIPTION = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.fork.description"),
        "Who needs a hoe when you have a fork?");

    public static final Component DS_UPGRADE_DIRECT = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.direct"), "Direct");
    public static final Component DS_UPGRADE_DIRECT_DESCRIPTION = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.direct.description"),
        "Teleports harvested items directly into your inventory");

    public static final Component DS_UPGRADE_EXPLOSIVE_I = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.explosive_l1"), "Explosive I");
    public static final Component DS_UPGRADE_EXPLOSIVE_II = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.explosive_l2"), "Explosive II");
    public static final Component DS_UPGRADE_EXPLOSIVE_DESCRIPTION = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.explosive.description"),
        "Makes surrounding dirt and rock go splodey");

    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_I = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.explosive_penetration_l1"),
        "Explosive Penetration I");
    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_II = addTranslation("info", EnderIOBase.loc("darksteel.upgrade.explosive_penetration_l2"),
        "Explosive Penetration II");
    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_DESCRIPTION = addTranslation("info",
        EnderIOBase.loc("darksteel.upgrade.explosive_penetration.description"), "Makes dirt and rock behind the mined block go splodey");

    // endregion

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIOArmory.REGILITE.lang().add(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name, String translation) {
        return EnderIOArmory.REGILITE.lang().add(prefix, ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath() + "/" + name), translation);
    }

    public static void register() {}
}
