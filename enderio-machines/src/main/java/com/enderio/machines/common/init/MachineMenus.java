package com.enderio.machines.common.init;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.screen.*;
import com.enderio.machines.common.menu.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.MenuEntry;

public class MachineMenus {
    private MachineMenus() {}

    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final MenuEntry<FluidTankMenu> FLUID_TANK = REGISTRATE.menu("fluid_tank", FluidTankMenu::factory, () -> FluidTankScreen::new).register();
    public static final MenuEntry<EnchanterMenu> ENCHANTER = REGISTRATE.menu("enchanter", EnchanterMenu::factory, () -> EnchanterScreen::new).register();
    public static final MenuEntry<AlloySmelterMenu> ALLOY_SMELTER = REGISTRATE.menu("alloy_smelter", AlloySmelterMenu::factory, () -> AlloySmelterScreen::new).register();
    public static final MenuEntry<SagMillMenu> SAG_MILL = REGISTRATE.menu("sag_mill", SagMillMenu::factory, () -> SagMillScreen::new).register();
    public static final MenuEntry<StirlingGeneratorMenu> STIRLING_GENERATOR = REGISTRATE.menu("stirling_generator", StirlingGeneratorMenu::factory, () -> StirlingGeneratorScreen::new).register();
    public static final MenuEntry<SlicerMenu> SLICE_N_SPLICE = REGISTRATE.menu("slice_n_splice", SlicerMenu::factory, () -> SlicerScreen::new).register();

    public static void classload() {}
}
