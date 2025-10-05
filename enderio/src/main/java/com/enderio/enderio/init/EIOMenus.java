package com.enderio.enderio.init;

import com.enderio.core.common.registries.MenuDeferredRegister;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.conduits.menu.ConduitMenu;
import com.enderio.enderio.content.enchanter.EnchanterMenu;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilterItem;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilterMenu;
import com.enderio.enderio.content.filters.item.general.EnderItemFilterItem;
import com.enderio.enderio.content.filters.item.general.EnderItemFilterMenu;
import com.enderio.enderio.content.filters.redstone.RedstoneCountFilterMenu;
import com.enderio.enderio.content.filters.redstone.RedstoneDoubleChannelFilterMenu;
import com.enderio.enderio.content.filters.redstone.RedstoneTimerFilterMenu;
import com.enderio.enderio.content.filters.soul.EnderSoulFilterItem;
import com.enderio.enderio.content.filters.soul.EnderSoulFilterMenu;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankMenu;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMenu;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankMenu;
import com.enderio.enderio.content.storage.crafter.CrafterMenu;
import com.enderio.enderio.content.machines.drain.DrainMenu;
import com.enderio.enderio.content.machines.farming_station.FarmingStationMenu;
import com.enderio.enderio.content.machines.impulse_hopper.ImpulseHopperMenu;
import com.enderio.enderio.content.machines.niard.NiardMenu;
import com.enderio.enderio.content.machines.obelisks.attractor.AttractorObeliskMenu;
import com.enderio.enderio.content.machines.obelisks.aversion.AversionObeliskMenu;
import com.enderio.enderio.content.machines.obelisks.inhibitor.InhibitorObeliskMenu;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskMenu;
import com.enderio.enderio.content.machines.obelisks.weather.WeatherObeliskMenu;
import com.enderio.enderio.content.machines.obelisks.xp.XPObeliskMenu;
import com.enderio.enderio.content.machines.painting.PaintingMachineMenu;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerMenu;
import com.enderio.enderio.content.machines.sag_mill.SagMillMenu;
import com.enderio.enderio.content.machines.slicer.SlicerMenu;
import com.enderio.enderio.content.machines.soul_binder.SoulBinderMenu;
import com.enderio.enderio.content.machines.soul_engine.SoulEngineMenu;
import com.enderio.enderio.content.machines.stirling_generator.StirlingGeneratorMenu;
import com.enderio.enderio.content.machines.vacuum.chest.VacuumChestMenu;
import com.enderio.enderio.content.machines.vacuum.xp.XPVacuumMenu;
import com.enderio.enderio.content.machines.vat.VatMenu;
import com.enderio.enderio.content.machines.wired_charger.WiredChargerMenu;
import com.enderio.enderio.content.machines.wireless_charger.WirelessChargerMenu;
import com.enderio.enderio.content.tools.coordinate_selector.CoordinateMenu;
import com.enderio.enderio.content.travel.travel_anchor.TravelAnchorMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EIOMenus {
    private static final MenuDeferredRegister MENUS = MenuDeferredRegister.create(EnderIO.MOD_ID);

    // region Filters

    public static final DeferredHolder<MenuType<?>, MenuType<EnderItemFilterMenu>> BASIC_ITEM_FILTER = MENUS.register("basic_item_filter",
        EnderItemFilterItem.Type.BASIC::openMenu);

    public static final DeferredHolder<MenuType<?>, MenuType<EnderItemFilterMenu>> ADVANCED_ITEM_FILTER = MENUS.register("advanced_item_filter",
        EnderItemFilterItem.Type.ADVANCED::openMenu);

    public static final DeferredHolder<MenuType<?>, MenuType<EnderItemFilterMenu>> BIG_ITEM_FILTER = MENUS.register("big_item_filter",
        EnderItemFilterItem.Type.BIG::openMenu);

    public static final DeferredHolder<MenuType<?>, MenuType<EnderItemFilterMenu>> BIG_ADVANCED_ITEM_FILTER = MENUS.register("big_advanced_item_filter",
        EnderItemFilterItem.Type.BIG_ADVANCED::openMenu);

    public static final DeferredHolder<MenuType<?>, MenuType<EnderFluidFilterMenu>> BASIC_FLUID_FILTER = MENUS.register("basic_fluid_filter",
        EnderFluidFilterItem.Type.BASIC::openMenu);

    public static final DeferredHolder<MenuType<?>, MenuType<EnderSoulFilterMenu>> BASIC_SOUL_FILTER = MENUS.register("basic_soul_filter",
        EnderSoulFilterItem.Type.BASIC::openMenu);

    public static final DeferredHolder<MenuType<?>, MenuType<RedstoneDoubleChannelFilterMenu>> REDSTONE_DOUBLE_CHANNEL_FILTER = MENUS.register(
        "redstone_and_filter", RedstoneDoubleChannelFilterMenu::factory);

    public static final DeferredHolder<MenuType<?>, MenuType<RedstoneTimerFilterMenu>> REDSTONE_TIMER_FILTER = MENUS.register("redstone_timer_filter",
        RedstoneTimerFilterMenu::factory);

    public static final DeferredHolder<MenuType<?>, MenuType<RedstoneCountFilterMenu>> REDSTONE_COUNT_FILTER = MENUS.register("redstone_count_filter",
        RedstoneCountFilterMenu::factory);

    // endregion

    // region Machines

    public static final DeferredHolder<MenuType<?>, MenuType<AlloySmelterMenu>> ALLOY_SMELTER = MENUS.register("alloy_smelter", AlloySmelterMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SagMillMenu>> SAG_MILL = MENUS.register("sag_mill", SagMillMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<StirlingGeneratorMenu>> STIRLING_GENERATOR = MENUS.register("stirling_generator",
        StirlingGeneratorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SlicerMenu>> SLICE_N_SPLICE = MENUS.register("slice_n_splice", SlicerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ImpulseHopperMenu>> IMPULSE_HOPPER = MENUS.register("impulse_hopper", ImpulseHopperMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SoulBinderMenu>> SOUL_BINDER = MENUS.register("soul_binder", SoulBinderMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<PoweredSpawnerMenu>> POWERED_SPAWNER = MENUS.register("powered_spawner", PoweredSpawnerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<VacuumChestMenu>> VACUUM_CHEST = MENUS.register("vacuum_chest", VacuumChestMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<XPVacuumMenu>> XP_VACUUM = MENUS.register("xp_vacuum", XPVacuumMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<CrafterMenu>> CRAFTER = MENUS.register("crafter", CrafterMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<DrainMenu>> DRAIN = MENUS.register("drain", DrainMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<NiardMenu>> NIARD = MENUS.register("niard", NiardMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<WiredChargerMenu>> WIRED_CHARGER = MENUS.register("wired_charger", WiredChargerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<WirelessChargerMenu>> WIRELESS_CHARGER = MENUS.register("wireless_charger",
        WirelessChargerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<PaintingMachineMenu>> PAINTING_MACHINE = MENUS.register("painting_machine",
        PaintingMachineMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SoulEngineMenu>> SOUL_ENGINE = MENUS.register("soul_engine", SoulEngineMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<TravelAnchorMenu>> TRAVEL_ANCHOR = MENUS.register("travel_anchor", TravelAnchorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<XPObeliskMenu>> XP_OBELISK = MENUS.register("xp_obelisk", XPObeliskMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FarmingStationMenu>> FARMING_STATION = MENUS.register("farming_station", FarmingStationMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<InhibitorObeliskMenu>> INHIBITOR_OBELISK = MENUS.register("inhibitor_obelisk",
        InhibitorObeliskMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<AversionObeliskMenu>> AVERSION_OBELISK = MENUS.register("aversion_obelisk",
        AversionObeliskMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<RelocatorObeliskMenu>> RELOCATOR_OBELISK = MENUS.register("relocator_obelisk",
        RelocatorObeliskMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<AttractorObeliskMenu>> ATTRACTOR_OBELISK = MENUS.register("attractor_obelisk",
        AttractorObeliskMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<WeatherObeliskMenu>> WEATHER_OBELISK = MENUS.register("weather_obelisk", WeatherObeliskMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<VatMenu>> VAT = MENUS.register("vat", VatMenu::new);

    // endregion

    // region Storage

    public static final DeferredHolder<MenuType<?>, MenuType<CapacitorBankMenu>> CAPACITOR_BANK = MENUS.register("capacitor_bank", CapacitorBankMenu::factory);

    public static final DeferredHolder<MenuType<?>, MenuType<FluidTankMenu>> FLUID_TANK = MENUS.register("fluid_tank", FluidTankMenu::new);

    // endregion

    public static final DeferredHolder<MenuType<?>, MenuType<CoordinateMenu>> COORDINATE = MENUS.register("coordinate", CoordinateMenu::factory);

    public static final DeferredHolder<MenuType<?>, MenuType<ConduitMenu>> CONDUIT_MENU = MENUS.register("conduit", ConduitMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<EnchanterMenu>> ENCHANTER = MENUS.register("enchanter", EnchanterMenu::new);

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
