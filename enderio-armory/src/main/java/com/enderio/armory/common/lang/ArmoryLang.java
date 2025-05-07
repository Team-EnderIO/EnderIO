package com.enderio.armory.common.lang;

import com.enderio.armory.EnderIOArmory;
import com.enderio.base.api.EnderIO;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ArmoryLang {

    // region Dark Steel

    public static final MutableComponent ENDER_HEAD_DROP_CHANCE = addTranslation(EnderIO.loc("ender.headchance"),
            "%s%% chance to drop a mob head");
    public static final MutableComponent ENDER_HEAD_DROP_INFO = addTranslation(EnderIO.loc("ender.headinfo"),
            "Cuts off mob heads once Empowered");
    public static final MutableComponent ENDER_BLOCK_TELEPORT = addTranslation(EnderIO.loc("ender.blockteleport"),
            "Stops Enderman teleporting");
    public static final MutableComponent DURABILITY_AMOUNT = addTranslation(EnderIO.loc("durability.amount"),
            "Durability %s");

    public static final MutableComponent DS_UPGRADE_XP_COST = addTranslation(EnderIO.loc("darksteel.upgrade.cost"),
            "Costs %s Levels");

    public static final Component DS_UPGRADE_AVAILABLE = addTranslation(EnderIO.loc("darksteel.upgrade.available"),
            "Available Upgrades").withStyle(ChatFormatting.YELLOW);

    public static final Component DS_UPGRADE_EMPOWERED_I = addTranslation(EnderIO.loc("darksteel.upgrade.empowered_l1"),
            "Empowered");
    public static final Component DS_UPGRADE_EMPOWERED_II = addTranslation(
            EnderIO.loc("darksteel.upgrade.empowered_l2"), "Empowered II");
    public static final Component DS_UPGRADE_EMPOWERED_III = addTranslation(
            EnderIO.loc("darksteel.upgrade.empowered_l3"), "Empowered III");
    public static final Component DS_UPGRADE_EMPOWERED_IV = addTranslation(
            EnderIO.loc("darksteel.upgrade.empowered_l4"), "Empowered IV");
    public static final Component DS_UPGRADE_EMPOWERED_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.empowered.description"),
            "Infuse the steel with the power of Micro Infinity");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_STORAGE = addTranslation(
            EnderIO.loc("darksteel.upgrade.empowered.storage"), "Holds up to %s µI");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_DAMAGE_ABSORPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.empowered.absorption"), "%s%% damage absorbed by µI");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_EFFICIENCY = addTranslation(
            EnderIO.loc("darksteel.upgrade.empowered.efficiency"), "Efficiency +%s when powered");
    public static final MutableComponent DS_UPGRADE_EMPOWERED_OBSIDIAM_EFFICIENCY = addTranslation(
            EnderIO.loc("darksteel.upgrade.empowered.obsidian.efficiency"), "Efficiency +%s when breaking obsidian");

    public static final Component DS_UPGRADE_SPOON = addTranslation(EnderIO.loc("darksteel.upgrade.spoon"), "Spoon");
    public static final Component DS_UPGRADE_SPOON_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.spoon.description"), "Who needs a shovel when you have a spoon?");

    public static final Component DS_UPGRADE_NIGHT_VISION = addTranslation(EnderIO.loc("darksteel.upgrade.nightvision"),
            "Night Vision");
    public static final Component DS_UPGRADE_NIGHT_VISION_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.nightvision.description"), "You can stop eating carrots now");
    public static final String DS_UPGRADE_NIGHT_VISION_KEYBIND = "darksteel.upgrade.nightvision.keybind";

    public static final Component DS_UPGRADE_SOLAR_I = addTranslation(EnderIO.loc("darksteel.upgrade.solar_l1"),
            "Solar I");
    public static final Component DS_UPGRADE_SOLAR_II = addTranslation(EnderIO.loc("darksteel.upgrade.solar_l2"),
            "Solar II");
    public static final Component DS_UPGRADE_SOLAR_III = addTranslation(EnderIO.loc("darksteel.upgrade.solar_l3"),
            "Solar III");
    public static final Component DS_UPGRADE_SOLAR_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.solar.description"), "Charges gear with a panel on your head");

    public static final Component DS_UPGRADE_FORK = addTranslation(EnderIO.loc("darksteel.upgrade.fork"), "Fork");
    public static final Component DS_UPGRADE_FORK_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.fork.description"), "Who needs a hoe when you have a fork?");

    public static final Component DS_UPGRADE_TRAVEL = addTranslation(EnderIO.loc("darksteel.upgrade.travel"), "Travel");
    public static final Component DS_UPGRADE_TRAVEL_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.travel.description"), "Integrated Travel Staff");

    public static final Component DS_UPGRADE_DIRECT = addTranslation(EnderIO.loc("darksteel.upgrade.direct"), "Direct");
    public static final Component DS_UPGRADE_DIRECT_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.direct.description"),
            "Teleports harvested items directly into your inventory");

    public static final Component DS_UPGRADE_EXPLOSIVE_I = addTranslation(EnderIO.loc("darksteel.upgrade.explosive_l1"),
            "Explosive I");
    public static final Component DS_UPGRADE_EXPLOSIVE_II = addTranslation(
            EnderIO.loc("darksteel.upgrade.explosive_l2"), "Explosive II");
    public static final Component DS_UPGRADE_EXPLOSIVE_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.explosive.description"), "Makes surrounding dirt and rock go splodey");

    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_I = addTranslation(
            EnderIO.loc("darksteel.upgrade.explosive_penetration_l1"), "Explosive Penetration I");
    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_II = addTranslation(
            EnderIO.loc("darksteel.upgrade.explosive_penetration_l2"), "Explosive Penetration II");
    public static final Component DS_UPGRADE_EXPLOSIVE_PENETRATION_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.explosive_penetration.description"),
            "Makes dirt and rock behind the mined block go splodey");

    public static final Component DS_UPGRADE_SPEED_I = addTranslation(EnderIO.loc("darksteel.upgrade.speed_l1"),
            "Speed I");
    public static final Component DS_UPGRADE_SPEED_II = addTranslation(EnderIO.loc("darksteel.upgrade.speed_l2"),
            "Speed II");
    public static final Component DS_UPGRADE_SPEED_III = addTranslation(EnderIO.loc("darksteel.upgrade.speed_l3"),
            "Speed III");
    public static final Component DS_UPGRADE_SPEED_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.speed.description"), "Increases movement speed");

    public static final MutableComponent DS_UPGRADE_BOOTS_SNOW_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.boots_snow"), "Can walk on powdered snow");

    public static final Component DS_UPGRADE_JUMP_I = addTranslation(EnderIO.loc("darksteel.upgrade.jump_l1"),
            "Jump I");
    public static final MutableComponent DS_UPGRADE_JUMP_I_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.jump.description_l1"), "Enables double jump");
    public static final Component DS_UPGRADE_JUMP_II = addTranslation(EnderIO.loc("darksteel.upgrade.jump_l2"),
            "Jump II");
    public static final MutableComponent DS_UPGRADE_JUMP_II_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.jump.description_l2"), "Enables triple jump");

    public static final Component DS_UPGRADE_STEP_ASSIST = addTranslation(EnderIO.loc("darksteel.upgrade.step_assist"),
            "Step Assist");
    public static final Component DS_UPGRADE_STEP_ASSIST_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.step_assist.description"), "Step just a little higher");

    public static final Component DS_UPGRADE_GLIDER = addTranslation(EnderIO.loc("darksteel.upgrade.glider"), "Glider");
    public static final Component DS_UPGRADE_GLIDER_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.glider.description"), "Wings anyone?");

    public static final String DS_UPGRADE_KEYBIND_CATEGORY = "darksteel.keybind.category";
    public static final String DS_UPGRADE_FLIGHT_KEYBIND = "darksteel.upgrade.flight.keybind";

    public static final Component FLIGHT_ENABLED = addTranslation(EnderIO.loc("darksteel.upgrade.flight.enabled"),
            "Flight Enabled");
    public static final Component FLIGHT_DISABLED = addTranslation(EnderIO.loc("darksteel.upgrade.flight.disabled"),
            "Flight Disabled");

    public static final Component DS_UPGRADE_ELYTRA = addTranslation(EnderIO.loc("darksteel.upgrade.elytra"), "Elytra");
    public static final Component DS_UPGRADE_ELYTRA_DESCRIPTION = addTranslation(
            EnderIO.loc("darksteel.upgrade.elytra.description"), "Wings anyone?");

    static {
        EnderIOArmory.REGILITE.addTranslation(() -> DS_UPGRADE_KEYBIND_CATEGORY, "Ender IO Dark Steel");
        EnderIOArmory.REGILITE.addTranslation(() -> DS_UPGRADE_FLIGHT_KEYBIND, "Toggles Glider/Elytra Upgrade");
    }

    public static final Component ARMORY_REWRITE_EXPERIMENT = addTranslation("pack",
            EnderIO.loc("experiment.armory_rewrite"), "EnderIO: Armory Rewrite");

    // endregion

    private static MutableComponent addTranslation(ResourceLocation id, String translation) {
        return EnderIOArmory.REGILITE.addTranslation("info", id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIOArmory.REGILITE.addTranslation(prefix, id, translation);
    }

    public static void register() {
    }
}
