package com.enderio.machines.common.lang;

import com.enderio.base.EnderIO;
import com.enderio.machines.EIOMachines;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class MachineLang {
    private static Registrate REGISTRATE = EIOMachines.registrate();

    public static final TranslatableComponent PROGRESS_TOOLTIP = REGISTRATE.addLang("gui", EnderIO.loc("progress"), "Progress %s%%");

    public static final Component ALLOY_SMELTER_MODE = REGISTRATE.addLang("gui", EnderIO.loc("alloy_smelter.mode"), "Smelting Mode");
    public static final Component ALLOY_SMELTER_MODE_ALL = REGISTRATE.addLang("gui", EnderIO.loc("alloy_smelter.mode_all"), "All Smelting");
    public static final Component ALLOY_SMELTER_MODE_ALLOY = REGISTRATE.addLang("gui", EnderIO.loc("alloy_smelter.mode_alloy"), "Alloys Only");
    public static final Component ALLOY_SMELTER_MODE_FURNACE = REGISTRATE.addLang("gui", EnderIO.loc("alloy_smelter.mode_furnace"), "Furnace Only");

    public static void register() {}
}
