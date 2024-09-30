package com.enderio.machines.common.init;

import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.client.rendering.blockentity.CapacitorBankBER;
import com.enderio.machines.client.rendering.blockentity.FluidTankBER;
import com.enderio.machines.client.rendering.blockentity.ObeliskBER;
import com.enderio.machines.client.rendering.blockentity.XPObeliskBER;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.blockentity.AversionObeliskBlockEntity;
import com.enderio.machines.common.blockentity.CrafterBlockEntity;
import com.enderio.machines.common.blockentity.CreativePowerBlockEntity;
import com.enderio.machines.common.blockentity.DrainBlockEntity;
import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.blockentity.ImpulseHopperBlockEntity;
import com.enderio.machines.common.blockentity.InhibitorObeliskBlockEntity;
import com.enderio.machines.common.blockentity.PaintedTravelAnchorBlockEntity;
import com.enderio.machines.common.blockentity.PaintingMachineBlockEntity;
import com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity;
import com.enderio.machines.common.blockentity.PrimitiveAlloySmelterBlockEntity;
import com.enderio.machines.common.blockentity.RelocatorObeliskBlockEntity;
import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.blockentity.SlicerBlockEntity;
import com.enderio.machines.common.blockentity.SoulBinderBlockEntity;
import com.enderio.machines.common.blockentity.SoulEngineBlockEntity;
import com.enderio.machines.common.blockentity.StirlingGeneratorBlockEntity;
import com.enderio.machines.common.blockentity.TravelAnchorBlockEntity;
import com.enderio.machines.common.blockentity.VacuumChestBlockEntity;
import com.enderio.machines.common.blockentity.VatBlockEntity;
import com.enderio.machines.common.blockentity.WiredChargerBlockEntity;
import com.enderio.machines.common.blockentity.XPObeliskBlockEntity;
import com.enderio.machines.common.blockentity.XPVacuumBlockEntity;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelBlockEntity;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.regilite.blockentities.BlockEntityTypeBuilder;
import com.enderio.regilite.blockentities.DeferredBlockEntityType;
import com.enderio.regilite.blockentities.RegiliteBlockEntityTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class MachineBlockEntities {
    private static final RegiliteBlockEntityTypes BLOCK_ENTITY_TYPES = EnderIOMachines.REGILITE.blockEntityTypes();

    public static final DeferredBlockEntityType<FluidTankBlockEntity.Standard> FLUID_TANK =
        register("fluid_tank", FluidTankBlockEntity.Standard::new, MachineBlocks.FLUID_TANK)
            .renderer(() -> FluidTankBER::new)
            .with(MachineBlockEntities::machineBlockEntityCapabilities)
            .with(MachineBlockEntities::fluidHandlerCapability)
            .finish();

    public static final DeferredBlockEntityType<FluidTankBlockEntity.Enhanced> PRESSURIZED_FLUID_TANK =
        register("pressurized_fluid_tank", FluidTankBlockEntity.Enhanced::new, MachineBlocks.PRESSURIZED_FLUID_TANK)
            .renderer(() -> FluidTankBER::new)
            .with(MachineBlockEntities::machineBlockEntityCapabilities)
            .with(MachineBlockEntities::fluidHandlerCapability)
            .finish();

    public static final DeferredBlockEntityType<EnchanterBlockEntity> ENCHANTER =
        register("enchanter", EnchanterBlockEntity::new, MachineBlocks.ENCHANTER)
            .finish();

    public static final DeferredBlockEntityType<PrimitiveAlloySmelterBlockEntity> PRIMITIVE_ALLOY_SMELTER =
        register("primitive_alloy_smelter", PrimitiveAlloySmelterBlockEntity::new, MachineBlocks.PRIMITIVE_ALLOY_SMELTER)
            .with(MachineBlockEntities::machineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<AlloySmelterBlockEntity> ALLOY_SMELTER =
        register("alloy_smelter", AlloySmelterBlockEntity::factory, MachineBlocks.ALLOY_SMELTER)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<CreativePowerBlockEntity> CREATIVE_POWER =
        register("creative_power", CreativePowerBlockEntity::new, MachineBlocks.CREATIVE_POWER)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<StirlingGeneratorBlockEntity> STIRLING_GENERATOR =
        register("stirling_generator", StirlingGeneratorBlockEntity::new, MachineBlocks.STIRLING_GENERATOR)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<SagMillBlockEntity> SAG_MILL =
        register("sag_mill", SagMillBlockEntity::new, MachineBlocks.SAG_MILL)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<SlicerBlockEntity> SLICE_AND_SPLICE =
        register("slice_and_splice", SlicerBlockEntity::new, MachineBlocks.SLICE_AND_SPLICE)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<ImpulseHopperBlockEntity> IMPULSE_HOPPER =
        register("impulse_hopper", ImpulseHopperBlockEntity::new, MachineBlocks.IMPULSE_HOPPER)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<VacuumChestBlockEntity> VACUUM_CHEST =
        register("vacuum_chest", VacuumChestBlockEntity::new, MachineBlocks.VACUUM_CHEST)
            .with(MachineBlockEntities::machineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<XPVacuumBlockEntity> XP_VACUUM =
        register("xp_vacuum", XPVacuumBlockEntity::new, MachineBlocks.XP_VACUUM)
            .with(MachineBlockEntities::machineBlockEntityCapabilities)
            .with(MachineBlockEntities::fluidHandlerCapability)
            .finish();

    public static final DeferredBlockEntityType<TravelAnchorBlockEntity> TRAVEL_ANCHOR =
        register("travel_anchor", TravelAnchorBlockEntity::new, MachineBlocks.TRAVEL_ANCHOR)
            .with(MachineBlockEntities::machineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<PaintedTravelAnchorBlockEntity> PAINTED_TRAVEL_ANCHOR = register("painted_travel_anchor",
        PaintedTravelAnchorBlockEntity::new, MachineBlocks.PAINTED_TRAVEL_ANCHOR)
        .with(MachineBlockEntities::machineBlockEntityCapabilities)
        .finish();

    public static final DeferredBlockEntityType<CrafterBlockEntity> CRAFTER =
        register("crafter", CrafterBlockEntity::new, MachineBlocks.CRAFTER)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<DrainBlockEntity> DRAIN =
        register("drain", DrainBlockEntity::new, MachineBlocks.DRAIN)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .with(MachineBlockEntities::fluidHandlerCapability)
            .finish();

    public static final DeferredBlockEntityType<SoulBinderBlockEntity> SOUL_BINDER =
        register("soul_binder", SoulBinderBlockEntity::new, MachineBlocks.SOUL_BINDER)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .with(MachineBlockEntities::fluidHandlerCapability)
            .finish();

    public static final DeferredBlockEntityType<WiredChargerBlockEntity> WIRED_CHARGER =
        register("wired_charger", WiredChargerBlockEntity::new, MachineBlocks.WIRED_CHARGER)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<PaintingMachineBlockEntity> PAINTING_MACHINE =
        register("painting_machine", PaintingMachineBlockEntity::new, MachineBlocks.PAINTING_MACHINE)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<PoweredSpawnerBlockEntity> POWERED_SPAWNER =
        register("powered_spawner", PoweredSpawnerBlockEntity::new, MachineBlocks.POWERED_SPAWNER)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final Map<SolarPanelTier, DeferredBlockEntityType<SolarPanelBlockEntity>> SOLAR_PANELS = Util.make(() -> {
       Map<SolarPanelTier, DeferredBlockEntityType<SolarPanelBlockEntity>> map = new HashMap<>();
       for (SolarPanelTier tier : SolarPanelTier.values()) {
           map.put(
               tier,
               register(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_cell",
                   (worldPosition, blockState) -> new SolarPanelBlockEntity(worldPosition, blockState, tier),
                   () -> MachineBlocks.SOLAR_PANELS.get(tier).get())
                   .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
                   .finish());
       }
       return ImmutableMap.copyOf(map);
    });

    public static final Map<CapacitorTier, DeferredBlockEntityType<CapacitorBankBlockEntity>> CAPACITOR_BANKS = Util.make(() -> {
       Map<CapacitorTier, DeferredBlockEntityType<CapacitorBankBlockEntity>> map = new HashMap<>();
       for (CapacitorTier tier : CapacitorTier.values()) {
           map.put(
               tier,
               register(
                   tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank",
                   (worldPosition, blockState) -> new CapacitorBankBlockEntity(worldPosition, blockState, tier),
                   () -> MachineBlocks.CAPACITOR_BANKS.get(tier).get())
                   .renderer(() -> CapacitorBankBER::new)
                   .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
                   .finish());
       }
       return ImmutableMap.copyOf(map);
    });

    public static final DeferredBlockEntityType<SoulEngineBlockEntity> SOUL_ENGINE =
        register("soul_engine", SoulEngineBlockEntity::new, MachineBlocks.SOUL_ENGINE)
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .with(MachineBlockEntities::fluidHandlerCapability)
            .finish();

    public static final DeferredBlockEntityType<XPObeliskBlockEntity> XP_OBELISK =
        register("xp_obelisk", XPObeliskBlockEntity::new, MachineBlocks.XP_OBELISK)
            .renderer(() -> XPObeliskBER::new)
            // TODO: Make XP Obelisk use the common base class :)
            //.setRenderer(() -> ObeliskBER.factory(EIOItems.EXPERIENCE_ROD::get))
            .with(MachineBlockEntities::machineBlockEntityCapabilities)
            .with(MachineBlockEntities::fluidHandlerCapability)
            .finish();

    public static final DeferredBlockEntityType<VatBlockEntity> VAT = register("vat", VatBlockEntity::new, MachineBlocks.VAT)
        .with(MachineBlockEntities::machineBlockEntityCapabilities)
        .with(MachineBlockEntities::fluidHandlerCapability)
        .finish();

    public static final DeferredBlockEntityType<InhibitorObeliskBlockEntity> INHIBITOR_OBELISK =
        register("inhibitor_obelisk", InhibitorObeliskBlockEntity::new, MachineBlocks.INHIBITOR_OBELISK)
            .renderer(() -> ObeliskBER.factory(() -> Items.ENDER_PEARL))
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<AversionObeliskBlockEntity> AVERSION_OBELISK =
        register("aversion_obelisk", AversionObeliskBlockEntity::new, MachineBlocks.AVERSION_OBELISK)
            .renderer(() -> ObeliskBER.factory(EIOBlocks.ENDERMAN_HEAD::asItem))
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    public static final DeferredBlockEntityType<RelocatorObeliskBlockEntity> RELOCATOR_OBELISK =
        register("relocator_obelisk", RelocatorObeliskBlockEntity::new, MachineBlocks.RELOCATOR_OBELISK)
            .renderer(() -> ObeliskBER.factory(() -> Items.PRISMARINE))
            .with(MachineBlockEntities::poweredMachineBlockEntityCapabilities)
            .finish();

    @SafeVarargs
    private static <B extends BlockEntity> BlockEntityTypeBuilder<B> register(String name, BlockEntityType.BlockEntitySupplier<B> beFactory,
        Supplier<? extends Block>... blocks) {
        return BLOCK_ENTITY_TYPES
            .create(name, beFactory, blocks);
    }

    private static <T extends MachineBlockEntity> BlockEntityTypeBuilder<T> machineBlockEntityCapabilities(BlockEntityTypeBuilder<T> blockEntity) {
        return blockEntity
            .capability(EIOCapabilities.SideConfig.BLOCK, MachineBlockEntity.SIDE_CONFIG_PROVIDER)
            .capability(Capabilities.ItemHandler.BLOCK, MachineBlockEntity.ITEM_HANDLER_PROVIDER);
    }

    private static <T extends PoweredMachineBlockEntity> BlockEntityTypeBuilder<T> poweredMachineBlockEntityCapabilities(BlockEntityTypeBuilder<T> blockEntity) {
        return blockEntity
            .with(MachineBlockEntities::machineBlockEntityCapabilities)
            .capability(Capabilities.EnergyStorage.BLOCK, PoweredMachineBlockEntity.ENERGY_STORAGE_PROVIDER);
    }

    private static <T extends MachineBlockEntity> BlockEntityTypeBuilder<T> fluidHandlerCapability(BlockEntityTypeBuilder<T> blockEntity) {
        return blockEntity.capability(Capabilities.FluidHandler.BLOCK, FluidTankUser.FLUID_HANDLER_PROVIDER);
    }

    public static void register() {
    }
}
