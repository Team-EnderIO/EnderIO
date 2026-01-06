package com.enderio.enderio.content.machines;

import com.enderio.enderio.EnderIO;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MachinesLang {

    // region Common

    public static final MutableComponent TOOLTIP_PROGRESS = tooltip("progress");

    // Machine Status
    public static final MutableComponent STATUS_ACTIVE = tooltip("status/active");
    public static final MutableComponent STATUS_IDLE = tooltip("status/idle");
    public static final MutableComponent STATUS_NO_CAPACITOR = tooltip("status/no_capacitor");
    public static final MutableComponent STATUS_NO_ENERGY = tooltip("status/no_energy");
    public static final MutableComponent STATUS_ENERGY_FULL = tooltip("status/energy_full");
    public static final MutableComponent STATUS_DRAIN_NO_SOURCE = tooltip("status/drain/no_source");
    public static final MutableComponent STATUS_EMPTY_TANK = tooltip("status/empty_tank");
    public static final MutableComponent STATUS_FULL_TANK = tooltip("status/full_tank");
    public static final MutableComponent STATUS_BLOCKED_REDSTONE = tooltip("status/blocked_by_redstone");
    public static final MutableComponent STATUS_OUTPUT_FULL = tooltip("status/output_full");
    public static final MutableComponent STATUS_INPUT_EMPTY = tooltip("status/input_empty");

    // TODO: Might be better as a shared translation.
    public static final MutableComponent GUI_NO_FLUID = gui("no_fluid");

    public static final MutableComponent GENERATING = gui("generator/generating");
    public static final MutableComponent FUEL_EFFICIENCY = gui("generator/efficiency");

    // Range TODO: Maybe common
    public static final Component RANGE = gui("range");
    public static final Component MAX_RANGE = gui("range/max");
    public static final Component SHOW_RANGE = gui("range/show");
    public static final Component HIDE_RANGE = gui("range/hide");

    // Capacitors
    public static final MutableComponent NOCAP_TITLE = gui("nocap/title");
    public static final MutableComponent NOCAP_DESC = gui("nocap/desc");

    // endregion

    public static final MutableComponent ALLOY_SMELTER_MODE = gui("alloy_smelter/mode");
    public static final MutableComponent POWERED_SPAWNER_MODE = gui("powered_spawner/mode");

    // region SAG Mill

    public static final MutableComponent SAG_MILL_GRINDING_BALL_TITLE = tooltip("sag_mill/grinding_ball/title");
    public static final MutableComponent SAG_MILL_GRINDING_BALL_REMAINING = tooltip("sag_mill/grinding_ball/remaining");
    public static final MutableComponent SAG_MILL_CHANCE = tooltip("sag_mill/chance");
    public static final MutableComponent SAG_MILL_CHANCE_GRINDING_BALL = tooltip("sag_mill/chance/grinding_ball");

    // endregion

    // region Obelisks

    public static final MutableComponent OBELISK_UPKEEP = gui("obelisk/upkeep_cost");
    public static final MutableComponent OBELISK_NO_SOUL_FILTER = gui("obelisk/no_soul_filter");

    // region XP Obelisk

    public static final MutableComponent XP_RETRIEVE_1 = gui("xp_obelisk/button/retrieve/1_level");
    public static final MutableComponent XP_RETRIEVE_10 = gui("xp_obelisk/button/retrieve/10_levels");
    public static final MutableComponent XP_RETRIEVE_ALL = gui("xp_obelisk/button/retrieve/all_levels");
    public static final MutableComponent XP_STORE_1 = gui("xp_obelisk/button/rstore/1_level");
    public static final MutableComponent XP_STORE_10 = gui("xp_obelisk/button/store/10_levels");
    public static final MutableComponent XP_STORE_ALL = gui("xp_obelisk/button/store/all_levels");

    // endregion

    // endregion

    // region VAT

    public static final MutableComponent VAT_TRANSFER_TANK = gui("vat/transfer_tank");
    public static final MutableComponent VAT_DUMP_TANK = gui("vat/dump_tank");

    // endregion

    // region Powered Spawner

    public static final MutableComponent POWERED_SPAWNER_STATUS_OVERCROWDED_MOBS = gui("powered_spawner/status/overcrowded/mobs");
    public static final MutableComponent POWERED_SPAWNER_STATUS_OVERCROWDED_SPAWNERS = gui("powered_spawner/status/overcrowded/spawners");
    public static final MutableComponent POWERED_SPAWNER_STATUS_OTHER_MOD = gui("powered_spawner/status/other_mod");
    public static final MutableComponent POWERED_SPAWNER_STATUS_DISABLED = gui("powered_spawner/status/disabled");
    public static final MutableComponent POWERED_SPAWNER_STATUS_UNKNOWN_MOB = gui("powered_spawner/status/unknown_mob");
    public static final MutableComponent POWERED_SPAWNER_STATUS_NO_PLAYER = gui("powered_spawner/status/no_player");

    // endregion

    // region Solar Panel

    public static final MutableComponent PHOTOVOLTAIC_CELL = tooltip("photovoltaic_cell/main");
    public static final MutableComponent PHOTOVOLTAIC_CELL_ADVANCED = tooltip("photovoltaic_cell/advanced");
    public static final MutableComponent PHOTOVOLTAIC_CELL_ADVANCED2 = tooltip("photovoltaic_cell/advanced2");
    public static final MutableComponent PHOTOVOLTAIC_CELL_ADVANCED3 = tooltip("photovoltaic_cell/advanced3");

    // endregion

    // region Experiments

    public static final MutableComponent FARMING_STATION_EXPERIMENT = pack("experiment/farming_station");
    public static final MutableComponent ENDERFACE_EXPERIMENT = pack("experiment/ender_io");
    public static final MutableComponent NIARD_EXPERIMENT = pack("experiment/niard");

    // endregion

    private static MutableComponent gui(String path) {
        return create("gui", path);
    }

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent pack(String path) {
        return create("pack", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.rl("machine/" + path)));
    }
}
