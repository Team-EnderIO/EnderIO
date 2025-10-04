package com.enderio.enderio.common.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.gui.screens.machines.AlloySmelterScreen;
import com.enderio.enderio.client.gui.screens.machines.AttractorObeliskScreen;
import com.enderio.enderio.client.gui.screens.machines.AversionObeliskScreen;
import com.enderio.enderio.client.gui.screens.machines.CapacitorBankScreen;
import com.enderio.enderio.client.gui.screens.machines.CrafterScreen;
import com.enderio.enderio.client.gui.screens.machines.DrainScreen;
import com.enderio.enderio.client.gui.screens.machines.EnchanterScreen;
import com.enderio.enderio.client.gui.screens.machines.FarmingStationScreen;
import com.enderio.enderio.client.gui.screens.machines.FluidTankScreen;
import com.enderio.enderio.client.gui.screens.machines.ImpulseHopperScreen;
import com.enderio.enderio.client.gui.screens.machines.InhibitorObeliskScreen;
import com.enderio.enderio.client.gui.screens.machines.NiardScreen;
import com.enderio.enderio.client.gui.screens.machines.PaintingMachineScreen;
import com.enderio.enderio.client.gui.screens.machines.PoweredSpawnerScreen;
import com.enderio.enderio.client.gui.screens.machines.RelocatorObeliskScreen;
import com.enderio.enderio.client.gui.screens.machines.SagMillScreen;
import com.enderio.enderio.client.gui.screens.machines.SlicerScreen;
import com.enderio.enderio.client.gui.screens.machines.SoulBinderScreen;
import com.enderio.enderio.client.gui.screens.machines.SoulEngineScreen;
import com.enderio.enderio.client.gui.screens.machines.StirlingGeneratorScreen;
import com.enderio.enderio.client.gui.screens.machines.TravelAnchorScreen;
import com.enderio.enderio.client.gui.screens.machines.VacuumChestScreen;
import com.enderio.enderio.client.gui.screens.machines.VatScreen;
import com.enderio.enderio.client.gui.screens.machines.WeatherObeliskScreen;
import com.enderio.enderio.client.gui.screens.machines.WiredChargerScreen;
import com.enderio.enderio.client.gui.screens.machines.WirelessChargerScreen;
import com.enderio.enderio.client.gui.screens.machines.XPObeliskScreen;
import com.enderio.enderio.client.gui.screens.machines.XPVacuumScreen;
import com.enderio.enderio.common.content.machines.alloy.AlloySmelterMenu;
import com.enderio.enderio.common.content.machines.crafter.CrafterMenu;
import com.enderio.enderio.common.content.machines.drain.DrainMenu;
import com.enderio.enderio.common.content.enchanter.EnchanterMenu;
import com.enderio.enderio.common.content.machines.farming_station.FarmingStationMenu;
import com.enderio.enderio.common.content.machines.fluid_tank.FluidTankMenu;
import com.enderio.enderio.common.content.machines.impulse_hopper.ImpulseHopperMenu;
import com.enderio.enderio.common.content.machines.niard.NiardMenu;
import com.enderio.enderio.common.content.machines.obelisks.attractor.AttractorObeliskMenu;
import com.enderio.enderio.common.content.machines.obelisks.aversion.AversionObeliskMenu;
import com.enderio.enderio.common.content.machines.obelisks.inhibitor.InhibitorObeliskMenu;
import com.enderio.enderio.common.content.machines.obelisks.relocator.RelocatorObeliskMenu;
import com.enderio.enderio.common.content.machines.obelisks.weather.WeatherObeliskMenu;
import com.enderio.enderio.common.content.machines.obelisks.xp.XPObeliskMenu;
import com.enderio.enderio.common.content.machines.painting.PaintingMachineMenu;
import com.enderio.enderio.common.content.machines.powered_spawner.PoweredSpawnerMenu;
import com.enderio.enderio.common.content.machines.sag_mill.SagMillMenu;
import com.enderio.enderio.common.content.machines.slicer.SlicerMenu;
import com.enderio.enderio.common.content.machines.soul_binder.SoulBinderMenu;
import com.enderio.enderio.common.content.machines.soul_engine.SoulEngineMenu;
import com.enderio.enderio.common.content.machines.stirling_generator.StirlingGeneratorMenu;
import com.enderio.enderio.common.content.travel.travel_anchor.TravelAnchorMenu;
import com.enderio.enderio.common.content.machines.vacuum.chest.VacuumChestMenu;
import com.enderio.enderio.common.content.machines.vacuum.xp.XPVacuumMenu;
import com.enderio.enderio.common.content.machines.vat.VatMenu;
import com.enderio.enderio.common.content.machines.wired_charger.WiredChargerMenu;
import com.enderio.enderio.common.content.machines.wireless_charger.WirelessChargerMenu;
import com.enderio.enderio.common.content.machines.capacitor_bank.CapacitorBankMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class MachineMenus {
    private MachineMenus() {
    }

    private static final MenuRegistry MENU_REGISTRY = EnderIO.REGILITE.menuRegistry();

    public static final RegiliteMenu<FluidTankMenu> FLUID_TANK = MENU_REGISTRY.registerMenu("fluid_tank",
            FluidTankMenu::new, () -> FluidTankScreen::new);
    public static final RegiliteMenu<EnchanterMenu> ENCHANTER = MENU_REGISTRY.registerMenu("enchanter",
            EnchanterMenu::new, () -> EnchanterScreen::new);
    public static final RegiliteMenu<AlloySmelterMenu> ALLOY_SMELTER = MENU_REGISTRY.registerMenu("alloy_smelter",
            AlloySmelterMenu::new, () -> AlloySmelterScreen::new);
    public static final RegiliteMenu<SagMillMenu> SAG_MILL = MENU_REGISTRY.registerMenu("sag_mill", SagMillMenu::new,
            () -> SagMillScreen::new);
    public static final RegiliteMenu<StirlingGeneratorMenu> STIRLING_GENERATOR = MENU_REGISTRY
            .registerMenu("stirling_generator", StirlingGeneratorMenu::new, () -> StirlingGeneratorScreen::new);
    public static final RegiliteMenu<SlicerMenu> SLICE_N_SPLICE = MENU_REGISTRY.registerMenu("slice_n_splice",
            SlicerMenu::new, () -> SlicerScreen::new);
    public static final RegiliteMenu<ImpulseHopperMenu> IMPULSE_HOPPER = MENU_REGISTRY.registerMenu("impulse_hopper",
            ImpulseHopperMenu::new, () -> ImpulseHopperScreen::new);
    public static final RegiliteMenu<SoulBinderMenu> SOUL_BINDER = MENU_REGISTRY.registerMenu("soul_binder",
            SoulBinderMenu::new, () -> SoulBinderScreen::new);
    public static final RegiliteMenu<PoweredSpawnerMenu> POWERED_SPAWNER = MENU_REGISTRY.registerMenu("powered_spawner",
            PoweredSpawnerMenu::new, () -> PoweredSpawnerScreen::new);
    public static final RegiliteMenu<VacuumChestMenu> VACUUM_CHEST = MENU_REGISTRY.registerMenu("vacuum_chest",
            VacuumChestMenu::new, () -> VacuumChestScreen::new);
    public static final RegiliteMenu<XPVacuumMenu> XP_VACUUM = MENU_REGISTRY.registerMenu("xp_vacuum",
            XPVacuumMenu::new, () -> XPVacuumScreen::new);
    public static final RegiliteMenu<CrafterMenu> CRAFTER = MENU_REGISTRY.registerMenu("crafter", CrafterMenu::new,
            () -> CrafterScreen::new);
    public static final RegiliteMenu<DrainMenu> DRAIN = MENU_REGISTRY.registerMenu("drain", DrainMenu::new,
            () -> DrainScreen::new);
    public static final RegiliteMenu<NiardMenu> NIARD = MENU_REGISTRY.registerMenu("niard", NiardMenu::new,
        () -> NiardScreen::new);
    public static final RegiliteMenu<WiredChargerMenu> WIRED_CHARGER = MENU_REGISTRY.registerMenu("wired_charger",
            WiredChargerMenu::new, () -> WiredChargerScreen::new);
    public static final RegiliteMenu<WirelessChargerMenu> WIRELESS_CHARGER = MENU_REGISTRY
            .registerMenu("wireless_charger", WirelessChargerMenu::new, () -> WirelessChargerScreen::new);
    public static final RegiliteMenu<PaintingMachineMenu> PAINTING_MACHINE = MENU_REGISTRY
            .registerMenu("painting_machine", PaintingMachineMenu::new, () -> PaintingMachineScreen::new);
    public static final RegiliteMenu<CapacitorBankMenu> CAPACITOR_BANK = MENU_REGISTRY.registerMenu("capacitor_bank",
            CapacitorBankMenu::factory, () -> CapacitorBankScreen::new);
    public static final RegiliteMenu<SoulEngineMenu> SOUL_ENGINE = MENU_REGISTRY.registerMenu("soul_engine",
            SoulEngineMenu::new, () -> SoulEngineScreen::new);
    public static final RegiliteMenu<TravelAnchorMenu> TRAVEL_ANCHOR = MENU_REGISTRY.registerMenu("travel_anchor",
            TravelAnchorMenu::new, () -> TravelAnchorScreen::new);
    public static final RegiliteMenu<XPObeliskMenu> XP_OBELISK = MENU_REGISTRY.registerMenu("xp_obelisk",
            XPObeliskMenu::new, () -> XPObeliskScreen::new);
    public static final RegiliteMenu<FarmingStationMenu> FARMING_STATION = MENU_REGISTRY.registerMenu("farming_station",
            FarmingStationMenu::new, () -> FarmingStationScreen::new);
    public static final RegiliteMenu<InhibitorObeliskMenu> INHIBITOR_OBELISK = MENU_REGISTRY
            .registerMenu("inhibitor_obelisk", InhibitorObeliskMenu::new, () -> InhibitorObeliskScreen::new);
    public static final RegiliteMenu<AversionObeliskMenu> AVERSION_OBELISK = MENU_REGISTRY
            .registerMenu("aversion_obelisk", AversionObeliskMenu::new, () -> AversionObeliskScreen::new);
    public static final RegiliteMenu<RelocatorObeliskMenu> RELOCATOR_OBELISK = MENU_REGISTRY
            .registerMenu("relocator_obelisk", RelocatorObeliskMenu::new, () -> RelocatorObeliskScreen::new);
    public static final RegiliteMenu<AttractorObeliskMenu> ATTRACTOR_OBELISK = MENU_REGISTRY
            .registerMenu("attractor_obelisk", AttractorObeliskMenu::new, () -> AttractorObeliskScreen::new);
    public static final RegiliteMenu<VatMenu> VAT = MENU_REGISTRY.registerMenu("vat", VatMenu::new,
            () -> VatScreen::new);
    public static final RegiliteMenu<WeatherObeliskMenu> WEATHER_OBELISK = MENU_REGISTRY.registerMenu("weather_obelisk",
            WeatherObeliskMenu::new, () -> WeatherObeliskScreen::new);

    public static void register(IEventBus bus) {
        MENU_REGISTRY.register(bus);
    }
}
