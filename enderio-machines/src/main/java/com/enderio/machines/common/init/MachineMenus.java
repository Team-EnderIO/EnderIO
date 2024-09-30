package com.enderio.machines.common.init;

import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.client.gui.screen.AlloySmelterScreen;
import com.enderio.machines.client.gui.screen.AversionObeliskScreen;
import com.enderio.machines.client.gui.screen.CapacitorBankScreen;
import com.enderio.machines.client.gui.screen.CrafterScreen;
import com.enderio.machines.client.gui.screen.DrainScreen;
import com.enderio.machines.client.gui.screen.EnchanterScreen;
import com.enderio.machines.client.gui.screen.FluidTankScreen;
import com.enderio.machines.client.gui.screen.ImpulseHopperScreen;
import com.enderio.machines.client.gui.screen.InhibitorObeliskScreen;
import com.enderio.machines.client.gui.screen.PaintingMachineScreen;
import com.enderio.machines.client.gui.screen.PoweredSpawnerScreen;
import com.enderio.machines.client.gui.screen.PrimitiveAlloySmelterScreen;
import com.enderio.machines.client.gui.screen.RelocatorObeliskScreen;
import com.enderio.machines.client.gui.screen.SagMillScreen;
import com.enderio.machines.client.gui.screen.SlicerScreen;
import com.enderio.machines.client.gui.screen.SoulBinderScreen;
import com.enderio.machines.client.gui.screen.SoulEngineScreen;
import com.enderio.machines.client.gui.screen.StirlingGeneratorScreen;
import com.enderio.machines.client.gui.screen.TravelAnchorScreen;
import com.enderio.machines.client.gui.screen.VacuumChestScreen;
import com.enderio.machines.client.gui.screen.VatScreen;
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
import com.enderio.machines.common.menu.InhibitorObeliskMenu;
import com.enderio.machines.common.menu.PaintingMachineMenu;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import com.enderio.machines.common.menu.PrimitiveAlloySmelterMenu;
import com.enderio.machines.common.menu.RelocatorObeliskMenu;
import com.enderio.machines.common.menu.SagMillMenu;
import com.enderio.machines.common.menu.SlicerMenu;
import com.enderio.machines.common.menu.SoulBinderMenu;
import com.enderio.machines.common.menu.SoulEngineMenu;
import com.enderio.machines.common.menu.StirlingGeneratorMenu;
import com.enderio.machines.common.menu.TravelAnchorMenu;
import com.enderio.machines.common.menu.VacuumChestMenu;
import com.enderio.machines.common.menu.VatMenu;
import com.enderio.machines.common.menu.WiredChargerMenu;
import com.enderio.machines.common.menu.XPObeliskMenu;
import com.enderio.machines.common.menu.XPVacuumMenu;
import com.enderio.regilite.menus.RegiliteMenuTypes;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class MachineMenus {
    private MachineMenus() {}

    private static final RegiliteMenuTypes MENU_TYPES = EnderIOMachines.REGILITE.menuTypes();

    public static final java.util.function.Supplier<MenuType<FluidTankMenu>> FLUID_TANK = MENU_TYPES
        .createOnly("fluid_tank", FluidTankMenu::factory, () -> FluidTankScreen::new);
    
    public static final Supplier<MenuType<EnchanterMenu>> ENCHANTER = MENU_TYPES
        .createOnly("enchanter", EnchanterMenu::factory, () -> EnchanterScreen::new);
    
    public static final Supplier<MenuType<PrimitiveAlloySmelterMenu>> PRIMITIVE_ALLOY_SMELTER = MENU_TYPES
        .createOnly("primitive_alloy_smelter", PrimitiveAlloySmelterMenu::factory, () -> PrimitiveAlloySmelterScreen::new);
    
    public static final Supplier<MenuType<AlloySmelterMenu>> ALLOY_SMELTER = MENU_TYPES
        .createOnly("alloy_smelter", AlloySmelterMenu::factory, () -> AlloySmelterScreen::new);
    
    public static final Supplier<MenuType<SagMillMenu>> SAG_MILL = MENU_TYPES
        .createOnly("sag_mill", SagMillMenu::factory, () -> SagMillScreen::new);
    
    public static final Supplier<MenuType<StirlingGeneratorMenu>> STIRLING_GENERATOR = MENU_TYPES
        .createOnly("stirling_generator", StirlingGeneratorMenu::factory, () -> StirlingGeneratorScreen::new);
    
    public static final Supplier<MenuType<SlicerMenu>> SLICE_N_SPLICE = MENU_TYPES
        .createOnly("slice_n_splice", SlicerMenu::factory, () -> SlicerScreen::new);
    
    public static final Supplier<MenuType<ImpulseHopperMenu>> IMPULSE_HOPPER = MENU_TYPES
        .createOnly("impulse_hopper", ImpulseHopperMenu::factory, () -> ImpulseHopperScreen::new);
    
    public static final Supplier<MenuType<SoulBinderMenu>> SOUL_BINDER = MENU_TYPES
        .createOnly("soul_binder", SoulBinderMenu::factory, () -> SoulBinderScreen::new);
    
    public static final Supplier<MenuType<PoweredSpawnerMenu>> POWERED_SPAWNER = MENU_TYPES
        .createOnly("powered_spawner", PoweredSpawnerMenu::factory, () -> PoweredSpawnerScreen::new);
    
    public static final Supplier<MenuType<VacuumChestMenu>> VACUUM_CHEST = MENU_TYPES
        .createOnly("vacuum_chest", VacuumChestMenu::factory, () -> VacuumChestScreen::new);
    
    public static final Supplier<MenuType<XPVacuumMenu>> XP_VACUUM = MENU_TYPES
        .createOnly("xp_vacuum", XPVacuumMenu::factory, () -> XPVacuumScreen::new);
    
    public static final Supplier<MenuType<CrafterMenu>> CRAFTER = MENU_TYPES
        .createOnly("crafter", CrafterMenu::factory, () -> CrafterScreen::new);
    
    public static final Supplier<MenuType<DrainMenu>> DRAIN = MENU_TYPES
        .createOnly("drain", DrainMenu::factory, () -> DrainScreen::new);
    
    public static final Supplier<MenuType<WiredChargerMenu>> WIRED_CHARGER = MENU_TYPES
        .createOnly("wired_charger", WiredChargerMenu::factory, () -> WiredChargerScreen::new);
    
    public static final Supplier<MenuType<PaintingMachineMenu>> PAINTING_MACHINE = MENU_TYPES
        .createOnly("painting_machine", PaintingMachineMenu::factory, () -> PaintingMachineScreen::new);
    
    public static final Supplier<MenuType<CapacitorBankMenu>> CAPACITOR_BANK = MENU_TYPES
        .createOnly("capacitor_bank", CapacitorBankMenu::factory, () -> CapacitorBankScreen::new);
    
    public static final Supplier<MenuType<SoulEngineMenu>> SOUL_ENGINE = MENU_TYPES
        .createOnly("soul_engine", SoulEngineMenu::factory, () -> SoulEngineScreen::new);
    
    public static final Supplier<MenuType<TravelAnchorMenu>> TRAVEL_ANCHOR = MENU_TYPES
        .createOnly("travel_anchor", TravelAnchorMenu::factory, () -> TravelAnchorScreen::new);
    
    public static final Supplier<MenuType<XPObeliskMenu>> XP_OBELISK = MENU_TYPES
        .createOnly("xp_obelisk", XPObeliskMenu::factory, () -> XPObeliskScreen::new);
    
    public static final Supplier<MenuType<InhibitorObeliskMenu>> INHIBITOR_OBELISK = MENU_TYPES
        .createOnly("inhibitor_obelisk", InhibitorObeliskMenu::factory, () -> InhibitorObeliskScreen::new);
    
    public static final Supplier<MenuType<AversionObeliskMenu>> AVERSION_OBELISK = MENU_TYPES
        .createOnly("aversion_obelisk", AversionObeliskMenu::factory, () -> AversionObeliskScreen::new);
    
    public static final Supplier<MenuType<RelocatorObeliskMenu>> RELOCATOR_OBELISK = MENU_TYPES
        .createOnly("relocator_obelisk", RelocatorObeliskMenu::factory, () -> RelocatorObeliskScreen::new);
    
    public static final Supplier<MenuType<VatMenu>> VAT = MENU_TYPES.createOnly("vat", VatMenu::factory, () -> VatScreen::new);

    public static void register() {
    }
}
