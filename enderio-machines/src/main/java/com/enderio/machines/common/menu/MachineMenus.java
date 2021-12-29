package com.enderio.machines.common.menu;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.EnchanterScreen;
import com.enderio.machines.client.FluidTankScreen;
import com.enderio.registrate.Registrate;
import com.enderio.registrate.util.entry.MenuEntry;

public class MachineMenus {
    private MachineMenus() {}

    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final MenuEntry<FluidTankMenu> FLUID_TANK = REGISTRATE.menu("fluid_tank",
        FluidTankMenu::factory, () -> FluidTankScreen::new).register();
    
    public static final MenuEntry<EnchanterMenu> ENCHANTER = REGISTRATE.menu("enchanter",
            EnchanterMenu::factory, () -> EnchanterScreen::new).register();

    public static void register() {}
}
