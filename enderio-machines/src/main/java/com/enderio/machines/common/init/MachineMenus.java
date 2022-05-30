package com.enderio.machines.common.init;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
import com.enderio.machines.client.gui.screen.EnchanterScreen;
import com.enderio.machines.client.gui.screen.FluidTankScreen;
import com.enderio.machines.client.gui.screen.StirlingGeneratorScreen;
import com.enderio.machines.client.gui.screen.VacuumChestScreen;
import com.enderio.machines.client.gui.screen.XPVacuumScreen;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.menu.FluidTankMenu;
import com.enderio.machines.common.menu.StirlingGeneratorMenu;
import com.enderio.machines.common.menu.VacuumChestMenu;
import com.enderio.machines.common.menu.XPVacuumMenu;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.MenuEntry;

public class MachineMenus {
    private MachineMenus() {}

    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final MenuEntry<FluidTankMenu> FLUID_TANK = REGISTRATE.menu("fluid_tank", FluidTankMenu::factory, () -> FluidTankScreen::new).register();
    public static final MenuEntry<EnchanterMenu> ENCHANTER = REGISTRATE.menu("enchanter", EnchanterMenu::factory, () -> EnchanterScreen::new).register();
    public static final MenuEntry<AlloySmelterMenu> ALLOY_SMELTER = REGISTRATE.menu("alloy_smelter", AlloySmelterMenu::factory, () -> AlloySmelterScreen::new).register();
    
    public static final MenuEntry<VacuumChestMenu> VACUUM_CHEST = REGISTRATE.menu("vacuum_chest", VacuumChestMenu::factory, () -> VacuumChestScreen::new).register();
    public static final MenuEntry<XPVacuumMenu> XP_VACUUM = REGISTRATE.menu("xp_vacuum", XPVacuumMenu::factory, () -> XPVacuumScreen::new).register();

    public static final MenuEntry<StirlingGeneratorMenu> STIRLING_GENERATOR = REGISTRATE.menu("stirling_generator", StirlingGeneratorMenu::factory, () -> StirlingGeneratorScreen::new).register();

    public static void classload() {}
}
