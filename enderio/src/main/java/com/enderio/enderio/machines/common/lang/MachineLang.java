package com.enderio.enderio.machines.common.lang;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.machines.EnderIOMachines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class MachineLang {
    public static final MutableComponent PROGRESS_TOOLTIP = addTranslation("gui", EnderIOAPI.loc("progress"),
            "Progress %s%%");

    public static final Component ALLOY_SMELTER_MODE = addTranslation("gui", EnderIOAPI.loc("alloy_smelter.mode"),
            "Smelting Mode");

    public static final Component POWERED_SPAWNER_MODE = addTranslation("gui", EnderIOAPI.loc("powered_spawner.mode"),
            "Spawner Mode");

    public static final MutableComponent SAG_MILL_GRINDINGBALL_REMAINING = addTranslation("tooltip",
            EnderIOAPI.loc("grinding_ball_remaining"), "Remaining: %s%%");
    public static final MutableComponent SAG_MILL_GRINDINGBALL_TITLE = addTranslation("tooltip",
            EnderIOAPI.loc("grinding_ball_title"), "SAG Mill Grinding Ball");

    public static final Component TOOLTIP_ENERGY_EQUIVALENCE = addTranslation("gui", EnderIOAPI.loc("energy_equivalence"),
            "A unit of energy, equivalent to FE.");
    public static final MutableComponent TOOLTIP_SAG_MILL_CHANCE_BALL = addTranslation("gui",
            EnderIOAPI.loc("sag_mill_chance_ball"), "Chance: %s%% (modified by grinding ball)");
    public static final MutableComponent TOOLTIP_SAG_MILL_CHANCE = addTranslation("gui", EnderIOAPI.loc("sag_mill_chance"),
            "Chance: %s%%");

    public static final MutableComponent TOOLTIP_ACTIVE = addTranslation("gui", EnderIOAPI.loc("active"),
            "The machine is active");
    public static final MutableComponent TOOLTIP_IDLE = addTranslation("gui", EnderIOAPI.loc("idle"),
            "The machine is idle");

    public static final MutableComponent TOOLTIP_NO_CAPACITOR = addTranslation("gui", EnderIOAPI.loc("no_capacitor"),
            "Install a capacitor to be able to use the machine");
    public static final MutableComponent TOOLTIP_NO_POWER = addTranslation("gui", EnderIOAPI.loc("no_power"),
            "There is not enough power to use the machine");
    public static final MutableComponent TOOLTIP_FULL_POWER = addTranslation("gui", EnderIOAPI.loc("full_power"),
            "The energy storage is full");
    public static final MutableComponent TOOLTIP_NO_SOURCE = addTranslation("gui", EnderIOAPI.loc("no_source"),
            "The Drain needs a source block under it to work");
    public static final MutableComponent TOOLTIP_EMPTY_TANK = addTranslation("gui", EnderIOAPI.loc("empty_tank"),
            "The tank is empty");
    public static final MutableComponent TOOLTIP_FULL_TANK = addTranslation("gui", EnderIOAPI.loc("full_tank"),
            "The tank is full");
    public static final MutableComponent TOOLTIP_BLOCKED_RESTONE = addTranslation("gui",
            EnderIOAPI.loc("blocked_redstone"), "The machine is blocked by redstone");
    public static final MutableComponent TOOLTIP_OUTPUT_FULL = addTranslation("gui", EnderIOAPI.loc("output_full"),
            "There is not enough room for the output");
    public static final MutableComponent TOOLTIP_INPUT_EMPTY = addTranslation("gui", EnderIOAPI.loc("input_empty"),
            "There is no item in the input");

    // region JEI Categories

    public static final MutableComponent CATEGORY_ALLOY_SMELTING = addTranslation("gui",
            EnderIOAPI.loc("category.alloy_smelting"), "Alloy Smelting");
    public static final MutableComponent CATEGORY_ENCHANTER = addTranslation("gui", EnderIOAPI.loc("category.enchanter"),
            "Enchanting");
    public static final MutableComponent CATEGORY_PRIMITIVE_ALLOY_SMELTING = addTranslation("gui",
            EnderIOAPI.loc("category.primitive_alloy_smelting"), "Primitive Alloy Smelting");
    public static final MutableComponent CATEGORY_SAG_MILL = addTranslation("gui", EnderIOAPI.loc("category.sag_mill"),
            "SAG Mill");
    public static final MutableComponent CATEGORY_SLICING = addTranslation("gui", EnderIOAPI.loc("category.slicing"),
            "Slicing");
    public static final MutableComponent CATEGORY_SOUL_BINDING = addTranslation("gui",
            EnderIOAPI.loc("category.soul_binding"), "Soul Binding");
    public static final MutableComponent CATEGORY_TANK = addTranslation("gui", EnderIOAPI.loc("category.tank"),
            "Fluid Tank");
    public static final MutableComponent CATEGORY_SOUL_ENGINE = addTranslation("gui",
            EnderIOAPI.loc("category.soul_engine"), "Soul Engine");
    public static final MutableComponent CATEGORY_VAT = addTranslation("gui", EnderIOAPI.loc("category.vat"), "VAT");
    public static final MutableComponent CATEGORY_WEATHER_CHANGE = addTranslation("gui",
        EnderIOAPI.loc("category.weather_change"), "Weather Obelisk");
    // endregion

    public static final MutableComponent TOO_MANY_MOB = addTranslation("gui", EnderIOAPI.loc("spawner.too_many_mob"),
            "Too many mobs");
    public static final MutableComponent TOO_MANY_SPAWNER = addTranslation("gui",
            EnderIOAPI.loc("spawner.too_many_spawner"), "Too many spawners");
    public static final MutableComponent UNKNOWN = addTranslation("gui", EnderIOAPI.loc("spawner.unknown"), "Unknown mob");
    public static final MutableComponent OTHER_MOD = addTranslation("gui", EnderIOAPI.loc("spawner.other_mod"),
            "Blocked by another mod");
    public static final MutableComponent DISABLED = addTranslation("gui", EnderIOAPI.loc("spawner.disabled"),
            "Disabled by config");
    public static final Component PHOTOVOLTAIC_CELL = addTranslation("tooltip", EnderIOAPI.loc("photovoltaic_cell/main"),
            "Solar Power!");
    public static final Component PHOTOVOLTAIC_CELL_ADVANCED = addTranslation("tooltip",
            EnderIOAPI.loc("photovoltaic_cell/advanced"), "Produces Power during daylight hours");
    public static final Component PHOTOVOLTAIC_CELL_ADVANCED2 = addTranslation("tooltip",
            EnderIOAPI.loc("photovoltaic_cell/advanced2"), "Must have a clear line of sight to the sky");
    public static final MutableComponent PHOTOVOLTAIC_CELL_ADVANCED3 = addTranslation("tooltip",
            EnderIOAPI.loc("photovoltaic_cell/advanced3"), "Max Output: ");
    public static final Component PLACE_CAPACITOR_BANK_ADVANCEMENT_TITLE = addTranslation("advancements",
            EnderIOAPI.loc("place_capacitor_bank.title"), "Modular Power Storage");
    public static final Component PLACE_CAPACITOR_BANK_ADVANCEMENT_DESCRIPTION = addTranslation("advancements",
            EnderIOAPI.loc("place_capacitor_bank.description"), "Build a Capacitor Bank");
    public static final Component MULTIBLOCK_CONNECTED_TEXTURES = addTranslation("hint",
            EnderIOAPI.loc("connected_textures.text"),
            "If you are looking for connected textures on the capacitor bank, you might want to install Athena on your client");
    public static final MutableComponent OBELISK_UPKEEP = addTranslation("gui", EnderIOAPI.loc("obelisk.upkeep_cost"),
            "Upkeep %s \u00B5I/t ");
    public static final MutableComponent GENERATING = addTranslation("gui", EnderIOAPI.loc("generator.generating"),
            "Generating %s \u00B5I/t ");
    public static final MutableComponent FUEL_EFFICIENCY = addTranslation("gui", EnderIOAPI.loc("generator.efficiency"),
            "Efficiency %s%%");
    public static final MutableComponent NO_SOUL_FILTER = addTranslation("gui", EnderIOAPI.loc("obelisk.no_soul_filter"),
            "No Soul Filter Installed");
    public static final Component EMPTY_FLUID_TANK = addTranslation("gui", EnderIOAPI.loc("empty_fluid_tank"),
            "Empty Fluid Tank");

    // GUI BUTTONS
    public static final Component RETRIEVE_1 = addTranslation("gui", EnderIOAPI.loc("button.retrieve_1_level"),
            "Retrieve 1 level of XP");
    public static final Component RETRIEVE_10 = addTranslation("gui", EnderIOAPI.loc("button.retrieve_10_level"),
            "Retrieve 10 levels of XP");
    public static final Component RETRIEVE_ALL = addTranslation("gui", EnderIOAPI.loc("button.retrieve_all_level"),
            "Retrieve all levels of XP");
    public static final Component STORE_1 = addTranslation("gui", EnderIOAPI.loc("button.store_1_level"),
            "Store 1 level of XP");
    public static final Component STORE_10 = addTranslation("gui", EnderIOAPI.loc("button.store_10_level"),
            "Store 10 levels of XP");
    public static final Component STORE_ALL = addTranslation("gui", EnderIOAPI.loc("button.store_all_level"),
            "Store all levels of XP");
    public static final Component TRANSFER_TANK = addTranslation("gui", EnderIOAPI.loc("button.transfer_tank"),
            "Transfer tank contents");
    public static final Component DUMP_TANK = addTranslation("gui", EnderIOAPI.loc("button.dump_tank"),
            "Void tank contents");

    public static final Component FARMING_STATION_EXPERIMENT = addTranslation("pack",
            EnderIOAPI.loc("experiment.farming_station"), "EnderIO: Farming Station");
    public static final Component ENDERFACE_EXPERIMENT = addTranslation("pack", EnderIOAPI.loc("experiment.ender_io"),
            "EnderIO: The Ender IO");
    public static final Component NIARD_EXPERIMENT = addTranslation("pack", EnderIOAPI.loc("experiment.niard"),
            "EnderIO: Niard");

    // TODO: NEO-PORT: Common lang base class?
    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIOMachines.REGILITE.addTranslation(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name,
            String translation) {
        return EnderIOMachines.REGILITE.addTranslation(prefix,
                ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath() + "/" + name), translation);
    }

    public static void register() {
    }
}
