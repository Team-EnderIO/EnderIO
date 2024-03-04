package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
import com.enderio.machines.client.gui.screen.CapacitorBankScreen;
import com.enderio.machines.client.gui.screen.CrafterScreen;
import com.enderio.machines.client.gui.screen.DrainScreen;
import com.enderio.machines.client.gui.screen.EnchanterScreen;
import com.enderio.machines.client.gui.screen.FarmScreen;
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
import com.enderio.machines.common.menu.CapacitorBankMenu;
import com.enderio.machines.common.menu.CrafterMenu;
import com.enderio.machines.common.menu.DrainMenu;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.menu.FarmMenu;
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
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class MachineMenus {
    private MachineMenus() {}

    private static final MenuRegistry MENU_REGISTRY = EnderIO.getRegilite().menuRegistry();

    public static final RegiliteMenu<FluidTankMenu> FLUID_TANK = MENU_REGISTRY
        .registerMenu("fluid_tank", FluidTankMenu::factory, () -> FluidTankScreen::new);
    public static final RegiliteMenu<EnchanterMenu> ENCHANTER = MENU_REGISTRY
        .registerMenu("enchanter", EnchanterMenu::factory, () -> EnchanterScreen::new);
    public static final RegiliteMenu<PrimitiveAlloySmelterMenu> PRIMITIVE_ALLOY_SMELTER = MENU_REGISTRY
        .registerMenu("primitive_alloy_smelter", PrimitiveAlloySmelterMenu::factory, () -> PrimitiveAlloySmelterScreen::new);
    public static final RegiliteMenu<AlloySmelterMenu> ALLOY_SMELTER = MENU_REGISTRY
        .registerMenu("alloy_smelter", AlloySmelterMenu::factory, () -> AlloySmelterScreen::new);
    public static final RegiliteMenu<SagMillMenu> SAG_MILL = MENU_REGISTRY
        .registerMenu("sag_mill", SagMillMenu::factory, () -> SagMillScreen::new);
    public static final RegiliteMenu<StirlingGeneratorMenu> STIRLING_GENERATOR = MENU_REGISTRY
        .registerMenu("stirling_generator", StirlingGeneratorMenu::factory, () -> StirlingGeneratorScreen::new);
    public static final RegiliteMenu<SlicerMenu> SLICE_N_SPLICE = MENU_REGISTRY
        .registerMenu("slice_n_splice", SlicerMenu::factory, () -> SlicerScreen::new);
    public static final RegiliteMenu<ImpulseHopperMenu> IMPULSE_HOPPER = MENU_REGISTRY
        .registerMenu("impulse_hopper", ImpulseHopperMenu::factory, () -> ImpulseHopperScreen::new);
    public static final RegiliteMenu<SoulBinderMenu> SOUL_BINDER = MENU_REGISTRY
        .registerMenu("soul_binder", SoulBinderMenu::factory, () -> SoulBinderScreen::new);
    public static final RegiliteMenu<PoweredSpawnerMenu> POWERED_SPAWNER = MENU_REGISTRY
        .registerMenu("powered_spawner", PoweredSpawnerMenu::factory, () -> PoweredSpawnerScreen::new);
    public static final RegiliteMenu<VacuumChestMenu> VACUUM_CHEST = MENU_REGISTRY
        .registerMenu("vacuum_chest", VacuumChestMenu::factory, () -> VacuumChestScreen::new);
    public static final RegiliteMenu<XPVacuumMenu> XP_VACUUM = MENU_REGISTRY
        .registerMenu("xp_vacuum", XPVacuumMenu::factory, () -> XPVacuumScreen::new);
    public static final RegiliteMenu<CrafterMenu> CRAFTER = MENU_REGISTRY
        .registerMenu("crafter", CrafterMenu::factory, () -> CrafterScreen::new);
    public static final RegiliteMenu<DrainMenu> DRAIN = MENU_REGISTRY
        .registerMenu("drain", DrainMenu::factory, () -> DrainScreen::new);
    public static final RegiliteMenu<WiredChargerMenu> WIRED_CHARGER = MENU_REGISTRY
        .registerMenu("wired_charger", WiredChargerMenu::factory, () -> WiredChargerScreen::new);
    public static final RegiliteMenu<PaintingMachineMenu> PAINTING_MACHINE = MENU_REGISTRY
        .registerMenu("painting_machine", PaintingMachineMenu::factory, () -> PaintingMachineScreen::new);
    public static final RegiliteMenu<CapacitorBankMenu> CAPACITOR_BANK = MENU_REGISTRY
        .registerMenu("capacitor_bank", CapacitorBankMenu::factory, () -> CapacitorBankScreen::new);
    public static final RegiliteMenu<SoulEngineMenu> SOUL_ENGINE = MENU_REGISTRY
        .registerMenu("soul_engine", SoulEngineMenu::factory, () -> SoulEngineScreen::new);
    public static final RegiliteMenu<TravelAnchorMenu> TRAVEL_ANCHOR = MENU_REGISTRY
        .registerMenu("travel_anchor", TravelAnchorMenu::factory, () -> TravelAnchorScreen::new);
    public static final RegiliteMenu<XPObeliskMenu> XP_OBELISK = MENU_REGISTRY
        .registerMenu("xp_obelisk", XPObeliskMenu::factory, () -> XPObeliskScreen::new);
    public static final RegiliteMenu<FarmMenu> FARMING_STATION = MENU_REGISTRY
        .registerMenu("farming_station", FarmMenu::factory, () -> FarmScreen::new);

    public static void register(IEventBus bus) {
        MENU_REGISTRY.register(bus);
    }
}
