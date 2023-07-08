package com.enderio.machines.common.lang;

import com.enderio.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MachineLang {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final MutableComponent PROGRESS_TOOLTIP = REGISTRATE.addLang("gui", EnderIO.loc("progress"), "Progress %s%%");

    public static final Component ALLOY_SMELTER_MODE = REGISTRATE.addLang("gui", EnderIO.loc("alloy_smelter.mode"), "Smelting Mode");
    public static final Component ALLOY_SMELTER_MODE_ALL = REGISTRATE.addLang("gui", EnderIO.loc("alloy_smelter.mode_all"), "Alloying and Smelting");
    public static final Component ALLOY_SMELTER_MODE_ALLOY = REGISTRATE.addLang("gui", EnderIO.loc("alloy_smelter.mode_alloy"), "Alloys Only");
    public static final Component ALLOY_SMELTER_MODE_FURNACE = REGISTRATE.addLang("gui", EnderIO.loc("alloy_smelter.mode_furnace"), "Furnace Only");
    public static final MutableComponent SAG_MILL_GRINDINGBALL_REMAINING = REGISTRATE.addLang("tooltip", EnderIO.loc("grinding_ball_remaining"), "Remaining: %s%%");
    public static final MutableComponent SAG_MILL_GRINDINGBALL_TITLE = REGISTRATE.addLang("tooltip", EnderIO.loc("grinding_ball_title"), "SAG Mill Grinding Ball");

    public static final Component TOOLTIP_ENERGY_EQUIVALENCE = REGISTRATE.addLang("gui", EnderIO.loc("energy_equivalence"), "A unit of energy, equivalent to FE.");
    public static final MutableComponent TOOLTIP_SAG_MILL_CHANCE_BALL = REGISTRATE.addLang("gui", EnderIO.loc("sag_mill_chance_ball"), "Chance: %s%% (modified by grinding ball)");
    public static final MutableComponent TOOLTIP_SAG_MILL_CHANCE = REGISTRATE.addLang("gui", EnderIO.loc("sag_mill_chance"), "Chance: %s%%");

    // region JEI Categories

    public static final MutableComponent CATEGORY_ALLOY_SMELTING = REGISTRATE.addLang("gui", EnderIO.loc("category.alloy_smelting"), "Alloy Smelting");
    public static final MutableComponent CATEGORY_ENCHANTER = REGISTRATE.addLang("gui", EnderIO.loc("category.enchanter"), "Enchanting");
    public static final MutableComponent CATEGORY_PRIMITIVE_ALLOY_SMELTING = REGISTRATE.addLang("gui", EnderIO.loc("category.primitive_alloy_smelting"), "Primitive Alloy Smelting");
    public static final MutableComponent CATEGORY_SAG_MILL = REGISTRATE.addLang("gui", EnderIO.loc("category.sag_mill"), "SAG Mill");
    public static final MutableComponent CATEGORY_SLICING = REGISTRATE.addLang("gui", EnderIO.loc("category.slicing"), "Slicing");
    public static final MutableComponent CATEGORY_SOUL_BINDING = REGISTRATE.addLang("gui", EnderIO.loc("category.soul_binding"), "Soul Binding");
    public static final MutableComponent CATEGORY_TANK = REGISTRATE.addLang("gui", EnderIO.loc("category.tank"), "Fluid Tank");

    // endregion

    public static final Component TOO_MANY_MOB = REGISTRATE.addLang("gui", EnderIO.loc("spawner.too_many_mob"), "Too many mobs");
    public static final Component TOO_MANY_SPAWNER = REGISTRATE.addLang("gui", EnderIO.loc("spawner.too_many_spawner"), "Too many spawners");
    public static final Component UNKNOWN = REGISTRATE.addLang("gui", EnderIO.loc("spawner.unknown"), "Unknown mob");
    public static final Component OTHER_MOD = REGISTRATE.addLang("gui", EnderIO.loc("spawner.other_mod"), "Other mod");
    public static final Component DISABLED = REGISTRATE.addLang("gui", EnderIO.loc("spawner.disabled"), "Disabled by config");
    public static final Component PHOTOVOLTAIC_CELL = REGISTRATE.addLang("tooltip", EnderIO.loc("photovoltaic_cell/main"), "Solar Power!");
    public static final Component PHOTOVOLTAIC_CELL_ADVANCED = REGISTRATE.addLang("tooltip", EnderIO.loc("photovoltaic_cell/advanced"), "Produces Power during daylight hours");
    public static final Component PHOTOVOLTAIC_CELL_ADVANCED2 = REGISTRATE.addLang("tooltip", EnderIO.loc("photovoltaic_cell/advanced2"), "Must have a clear line of sight to the sky");
    public static final MutableComponent PHOTOVOLTAIC_CELL_ADVANCED3 = REGISTRATE.addLang("tooltip", EnderIO.loc("photovoltaic_cell/advanced3"), "Max Output: ");
    public static final Component PLACE_CAPACITOR_BANK_ADVANCEMENT_TITLE = REGISTRATE.addLang("advancements", EnderIO.loc("place_capacitor_bank.title"), "Modular Power Storage");
    public static final Component PLACE_CAPACITOR_BANK_ADVANCEMENT_DESCRIPTION = REGISTRATE.addLang("advancements", EnderIO.loc("place_capacitor_bank.description"), "Build a Capacitor Bank");
    public static final Component MULTIBLOCK_CONNECTED_TEXTURES = REGISTRATE.addLang("hint", EnderIO.loc("connected_textures.text"), "If you are looking for connected textures on the capacitor bank, you might want to install Athena on your client");

    public static void register() {}
}
