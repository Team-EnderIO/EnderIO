package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
import com.enderio.machines.client.gui.screen.AversionObeliskScreen;
import com.enderio.machines.client.gui.screen.CapacitorBankScreen;
import com.enderio.machines.client.gui.screen.CrafterScreen;
import com.enderio.machines.client.gui.screen.DrainScreen;
import com.enderio.machines.client.gui.screen.EnchanterScreen;
import com.enderio.machines.client.gui.screen.FluidTankScreen;
import com.enderio.machines.client.gui.screen.ImpulseHopperScreen;
import com.enderio.machines.client.gui.screen.PaintingMachineScreen;
import com.enderio.machines.client.gui.screen.PoweredSpawnerScreen;
import com.enderio.machines.client.gui.screen.PrimitiveAlloySmelterScreen;
import com.enderio.machines.client.gui.screen.SagMillScreen;
import com.enderio.machines.client.gui.screen.SlicerScreen;
import com.enderio.machines.client.gui.screen.SoulBinderScreen;
import com.enderio.machines.client.gui.screen.SoulEngineScreen;
import com.enderio.machines.client.gui.screen.StirlingGeneratorScreen;
import com.enderio.machines.client.gui.screen.TravelAnchorScreen;
import com.enderio.machines.client.gui.screen.VacuumChestScreen;
import com.enderio.machines.client.gui.screen.WiredChargerScreen;
import com.enderio.machines.client.gui.screen.XPObeliskScreen;
import com.enderio.machines.client.gui.screen.XPVacuumScreen;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.menu.AversionObeliskMenu;
import com.enderio.machines.common.menu.CapacitorBankMenu;
import com.enderio.machines.common.menu.CrafterMenu;
import com.enderio.machines.common.menu.DrainMenu;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.menu.FluidTankMenu;
import com.enderio.machines.common.menu.ImpulseHopperMenu;
import com.enderio.machines.common.menu.PaintingMachineMenu;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import com.enderio.machines.common.menu.PrimitiveAlloySmelterMenu;
import com.enderio.machines.common.menu.SagMillMenu;
import com.enderio.machines.common.menu.SlicerMenu;
import com.enderio.machines.common.menu.SoulBinderMenu;
import com.enderio.machines.common.menu.SoulEngineMenu;
import com.enderio.machines.common.menu.StirlingGeneratorMenu;
import com.enderio.machines.common.menu.TravelAnchorMenu;
import com.enderio.machines.common.menu.VacuumChestMenu;
import com.enderio.machines.common.menu.WiredChargerMenu;
import com.enderio.machines.common.menu.XPObeliskMenu;
import com.enderio.machines.common.menu.XPVacuumMenu;
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
    public static final MenuEntry<SoulBinderMenu> SOUL_BINDER = REGISTRATE.menu("soul_binder", SoulBinderMenu::factory, () -> SoulBinderScreen::new).register();
    public static final MenuEntry<PoweredSpawnerMenu> POWERED_SPAWNER = REGISTRATE.menu("powered_spawner", PoweredSpawnerMenu::factory, () -> PoweredSpawnerScreen::new).register();
    public static final MenuEntry<VacuumChestMenu> VACUUM_CHEST = REGISTRATE.menu("vacuum_chest", VacuumChestMenu::factory, () -> VacuumChestScreen::new).register();
    public static final MenuEntry<XPVacuumMenu> XP_VACUUM = REGISTRATE.menu("xp_vacuum", XPVacuumMenu::factory, () -> XPVacuumScreen::new).register();
    public static final MenuEntry<CrafterMenu> CRAFTER = REGISTRATE.menu("crafter", CrafterMenu::factory, () -> CrafterScreen::new).register();
    public static final MenuEntry<DrainMenu> DRAIN = REGISTRATE.menu("drain", DrainMenu::factory, () -> DrainScreen::new).register();
    public static final MenuEntry<WiredChargerMenu> WIRED_CHARGER = REGISTRATE.menu("wired_charger", WiredChargerMenu::factory, () -> WiredChargerScreen::new).register();
    public static final MenuEntry<PaintingMachineMenu> PAINTING_MACHINE = REGISTRATE.menu("painting_machine", PaintingMachineMenu::factory, () -> PaintingMachineScreen::new).register();
    public static final MenuEntry<CapacitorBankMenu> CAPACITOR_BANK = REGISTRATE.menu("capacitor_bank", CapacitorBankMenu::factory, () -> CapacitorBankScreen::new).register();
    public static final MenuEntry<SoulEngineMenu> SOUL_ENGINE = REGISTRATE.menu("soul_engine", SoulEngineMenu::factory, () -> SoulEngineScreen::new).register();
    public static final MenuEntry<TravelAnchorMenu> TRAVEL_ANCHOR = REGISTRATE.menu("travel_anchor", TravelAnchorMenu::factory, () -> TravelAnchorScreen::new).register();
    public static final MenuEntry<XPObeliskMenu> XP_OBELISK = REGISTRATE.menu("xp_obelisk", XPObeliskMenu::factory, () -> XPObeliskScreen::new).register();
    public static final MenuEntry<AversionObeliskMenu> AVERSION_OBELISK = REGISTRATE.menu("aversion_obelisk", AversionObeliskMenu::factory, () -> AversionObeliskScreen::new).register();

    public static void register() {}
}
