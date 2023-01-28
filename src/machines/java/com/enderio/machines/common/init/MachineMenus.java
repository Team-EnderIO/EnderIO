package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.machines.client.gui.screen.*;
import com.enderio.machines.common.menu.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.MenuEntry;

public class MachineMenus {
    private MachineMenus() {}

    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final MenuEntry<FluidTankMenu> FLUID_TANK = REGISTRATE.menu("fluid_tank", FluidTankMenu::factory, () -> FluidTankScreen::new).register();
    public static final MenuEntry<EnchanterMenu> ENCHANTER = REGISTRATE.menu("enchanter", EnchanterMenu::factory, () -> EnchanterScreen::new).register();
    public static final MenuEntry<PrimitiveAlloySmelterMenu> PRIMITIVE_ALLOY_SMELTER = REGISTRATE.menu("primitive_alloy_smelter", PrimitiveAlloySmelterMenu::factory, () -> PrimitiveAlloySmelterScreen::new).register();
    public static final MenuEntry<AlloySmelterMenu> ALLOY_SMELTER = REGISTRATE.menu("alloy_smelter", AlloySmelterMenu::factory, () -> AlloySmelterScreen::new).register();
    public static final MenuEntry<SagMillMenu> SAG_MILL = REGISTRATE.menu("sag_mill", SagMillMenu::factory, () -> SagMillScreen::new).register();
    public static final MenuEntry<StirlingGeneratorMenu> STIRLING_GENERATOR = REGISTRATE.menu("stirling_generator", StirlingGeneratorMenu::factory, () -> StirlingGeneratorScreen::new).register();
    public static final MenuEntry<SlicerMenu> SLICE_N_SPLICE = REGISTRATE.menu("slice_n_splice", SlicerMenu::factory, () -> SlicerScreen::new).register();
    public static final MenuEntry<ImpulseHopperMenu> IMPULSE_HOPPER = REGISTRATE.menu("impulse_hopper", ImpulseHopperMenu::factory, () -> ImpulseHopperScreen::new).register();
    public static final MenuEntry<VacuumChestMenu> VACUUM_CHEST = REGISTRATE.menu("vacuum_chest", VacuumChestMenu::factory, () -> VacuumChestScreen::new).register();
    public static final MenuEntry<XPVacuumMenu> XP_VACUUM = REGISTRATE.menu("xp_vacuum", XPVacuumMenu::factory, () -> XPVacuumScreen::new).register();

    public static void register() {}
}
