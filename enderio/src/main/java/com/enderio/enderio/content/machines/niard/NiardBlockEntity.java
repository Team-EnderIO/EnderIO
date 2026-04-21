package com.enderio.enderio.content.machines.niard;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.storage.fluid_tank.InternalTankTasks;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.attachment.RangedActor;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.io.fluid.FluidItemInteractive;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class NiardBlockEntity extends PoweredMachineBlockEntity implements RangedActor, FluidItemInteractive {

    public static final ICapabilityProvider<NiardBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
        MachinesConfig.COMMON.ENERGY.NIARD_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
        MachinesConfig.COMMON.ENERGY.NIARD_USAGE);

    private static final ActionRange DEFAULT_RANGE = new ActionRange(5, false);
    private ActionRange actionRange = DEFAULT_RANGE;

    private NiardRangeIterator iterator;

    public static final SingleResourceSlotKey<FluidResource> TANK = new SingleResourceSlotKey<>();
    public static final int CAPACITY = 4 * FluidType.BUCKET_VOLUME;

    public static final FluidStorageLayout FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.builder()
            .add(TANK, SlotTemplates.storage(), slot -> slot.capacity(CAPACITY))
            .build();

    private final FluidStorage fluidStorage;

    private static final int ENERGY_PER_BUCKET = 1_500;
    private static final int BASE_IDLE_TICKS = 40;

    public static final SingleResourceSlotKey<ItemResource> FLUID_FILL_INPUT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> FLUID_FILL_OUTPUT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();


    public NiardBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.NIARD.get(), worldPosition, blockState, false, CapacitorSupport.REQUIRED, CAPACITOR,
            EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);

        fluidStorage = new FluidStorage(FLUID_STORAGE_LAYOUT) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                setChanged();
                updateMachineState(MachineState.EMPTY_TANK, fluidStorage.getAmountAsInt(TANK) <= 0);
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };

        iterator = new NiardRangeIterator(worldPosition, actionRange);
    }

    public FluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public void serverTick() {
        if (isActive()) {
            if (canAct()) {
                fillTank();
            }

            if (canAct((int) (BASE_IDLE_TICKS / getCapacitorData().base()))) {
                tryPlaceFluid();
            }
        }

        super.serverTick();
    }

    private void fillTank() {
        InternalTankTasks.fillInternal(fluidStorage, TANK, getInventory(), FLUID_FILL_INPUT, FLUID_FILL_OUTPUT);
    }

    private void tryPlaceFluid() {
        int startIndex = iterator.getIndex();

        //check each position in the range and place fluid if possible
        do {
            BlockPos pos = iterator.current();
            if ((isSameLiquid(pos) ? isFlowingBlock(pos) : canPlace(pos))) {
                if (fluidStorage.getAmountAsInt(TANK) >= FluidType.BUCKET_VOLUME) {
                    placeFluid(pos);
                    consumeResources();
                }
                return;
            }
            iterator.moveToNextPosition();
        } while (iterator.getIndex() != startIndex);
    }

    private void placeFluid(BlockPos pos) {
        Fluid fluid = fluidStorage.getStack(TANK).getFluid();
        if (fluid == Fluids.EMPTY) return;

        BlockState fluidBlockState = fluid.defaultFluidState().createLegacyBlock();
        level.setBlock(pos, fluidBlockState, Block.UPDATE_ALL);
    }

    private void consumeResources() {
        try (Transaction transaction = Transaction.openRoot()) {
            int tankIndex = fluidStorage.layout().indexOf(TANK);
            FluidStack currentFluid = fluidStorage.getStack(TANK);
            fluidStorage.extract(tankIndex, FluidResource.of(currentFluid), FluidType.BUCKET_VOLUME, transaction);
            getEnergyStorage().consume(ENERGY_PER_BUCKET, transaction);
            transaction.commit();
        }
    }

    @Override
    public void clientTick() {
        if (level.isClientSide() && level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getParticleLocation(),
                MachinesConfig.CLIENT.BLOCKS.NIARD_RANGE_COLOR.get());
        }

        super.clientTick();
    }

    @Override
    public boolean isActive() {
        return hasEnergy();
    }

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .add(FLUID_FILL_INPUT, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> acceptItemDrain(itemResource.toStack())))
            .add(FLUID_FILL_OUTPUT, SlotTemplates.output())
            .build();
    }

    @Override
    public int getMaxRange() {
        return 7;
    }

    @Override
    public ActionRange getActionRange() {
        return actionRange;
    }

    @Override
    public void setActionRange(ActionRange actionRange) {
        this.actionRange = actionRange.clamp(0, getMaxRange());
        iterator = new NiardRangeIterator(getBlockPos(), getActionRange());

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new NiardMenu(containerId, playerInventory, this);
    }

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK);
    }

    private BlockPos getParticleLocation() {
        return worldPosition.below(getRange() + 1);
    }

    private boolean canPlace(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        boolean isAir = state.isAir();
        boolean isLiquid = !state.getFluidState().isEmpty();
        boolean replaceable = state.canBeReplaced();

        return isAir || (!isLiquid && replaceable);
    }

    private boolean isSameLiquid(@Nonnull BlockPos pos) {
        Fluid fluidAtPos = level.getBlockState(pos).getFluidState().getType();
        Fluid fluidInTank = fluidStorage.getStack(TANK).getFluid();

        return (fluidInTank == Fluids.WATER && (fluidAtPos == Fluids.WATER || fluidAtPos == Fluids.FLOWING_WATER))
            || (fluidInTank == Fluids.LAVA && (fluidAtPos == Fluids.LAVA  || fluidAtPos == Fluids.FLOWING_LAVA))
            || fluidInTank.isSame(fluidAtPos);
    }

    private boolean isFlowingBlock(@Nonnull BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        return !fluidState.isSource();
    }

    public boolean acceptItemDrain(ItemStack item) {
        var fluidHandlerCap = item.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(item));
        return fluidHandlerCap != null;
    }

    // region Serialization

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            fluidStorage.setStack(TANK, storedFluid.copy());
        }

        var actionRange = components.get(EIODataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.actionRange = actionRange;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        FluidStack tank = fluidStorage.getStack(TANK);
        if (!tank.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(tank));
        }

        components.set(EIODataComponents.ACTION_RANGE, actionRange);
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);
        output.putChild("Fluid", fluidStorage);

        if (!actionRange.equals(DEFAULT_RANGE)) {
            output.store(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC, actionRange);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Fluid").ifPresent(fluidStorage::deserialize);

        actionRange = input.read(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC)
            .orElse(DEFAULT_RANGE);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.ACTION_RANGE);
    }

    // endregion
}
