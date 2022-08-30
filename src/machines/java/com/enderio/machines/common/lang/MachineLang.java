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
    public static final MutableComponent SAG_MILL_GRINDINGBALL_TOOLTIP = REGISTRATE.addLang("gui", EnderIO.loc("grinding_ball"), "Remaining: %s%%\\nSAG Mill Grinding Ball\\nMain Output %s%%\\nBonus Output %s%%\\nPower Use %s%%");

    public static final Component TOOLTIP_ENERGY_EQUIVALENCE = REGISTRATE.addLang("gui", EnderIO.loc("energy_equivalence"), "A unit of energy, equivalent to FE.");

    public static void register() {}
}
