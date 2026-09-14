package com.enderio.enderio.content.machines.drain;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.QuadraticIntScalable;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.attachment.RangedActor;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.io.IOConfig;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DrainBlockEntity extends PoweredMachineBlockEntity implements RangedActor {

    public static final ICapabilityProvider<DrainBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;
    public static final String CONSUMED = "Consumed";
    private static final QuadraticIntScalable ENERGY_CAPACITY = new QuadraticIntScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.DRAIN_CAPACITY);
    private static final QuadraticIntScalable ENERGY_USAGE = new QuadraticIntScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.DRAIN_USAGE);

    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    private static final ActionRange DEFAULT_RANGE = new ActionRange(5, false);

    // TODO: Config for both.
    public static final int CAPACITY = 3 * FluidType.BUCKET_VOLUME;
    public static final int ENERGY_PER_BUCKET = 1_500;

    public static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();

    private final FluidStorage fluidStorage;
    private List<BlockPos> positions;
    private int currentIndex = 0;
    private boolean fluidFound = false;
    private int consumed = 0;
    private Fluid type = Fluids.EMPTY;

    private ActionRange actionRange = DEFAULT_RANGE;

    public DrainBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.DRAIN.get(), worldPosition, blockState, false, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);

        var fluidStorageLayout = FluidStorageLayout.builder()
            .add(TANK_SLOT, SlotTemplates.output(CAPACITY), slot -> slot
                .filter((_, resource) -> type.isSame(resource.getFluid())))
            .build();;

        fluidStorage = new FluidStorage(fluidStorageLayout) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                updateMachineState(MachineState.FULL_TANK, fluidStorage.getAmountAsInt(TANK_SLOT) >= CAPACITY);
                setChanged();
            }
        };
    }

    @Override
    public ActionRange getActionRange() {
        return actionRange;
    }

    @Override
    @UseOnly(LogicalSide.SERVER)
    public void setActionRange(ActionRange actionRange) {
        this.actionRange = actionRange.clamp(0, getMaxRange());
        updateLocations();
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public int getMaxRange() {
        return 10;
    }

    @Override
    public @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK_SLOT);
    }

    @Override
    public void serverTick() {
        if (isActive()) {
            drainFluids();
        }

        super.serverTick();
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PUSH);
    }

    @Override
    public boolean isActive() {
        if (!canAct()) {
            return false;
        }

        FluidState fluidState = level.getFluidState(worldPosition.below());
        if (fluidState.isEmpty() || !fluidState.isSource()) {
            updateMachineState(MachineState.NO_SOURCE, true);
            return false;
        }
        updateMachineState(MachineState.NO_SOURCE, false);
        type = fluidState.getType();

        // Check if we can insert the fluid
        try (Transaction transaction = Transaction.openRoot()) {
            long inserted = fluidStorage.insert(TANK_SLOT, FluidResource.of(type), FluidType.BUCKET_VOLUME, transaction);
            // Don't commit, just simulate
            return inserted == FluidType.BUCKET_VOLUME;
        }
    }

    public void drainFluids() {
        int stop = Math.min(currentIndex + getRange(), positions.size());
        while (currentIndex < stop) {
            if (currentIndex >= positions.size()) {
                currentIndex--;
            }
            BlockPos pos = positions.get(currentIndex);

            // Skip, as this is the last checked block
            if (pos.equals(worldPosition.below()) && positions.size() != 1) {
                currentIndex++;
                continue;
            }

            // Last block, so reset
            if (currentIndex + 1 == positions.size()) {
                if (!fluidFound) {
                    pos = worldPosition.below(); // No fluids found, so consume the last block under the drain
                } else {
                    currentIndex = 0;
                    fluidFound = false;
                }
            }

            // Not a valid fluid
            FluidState fluidState = level.getFluidState(pos);
            if (fluidState.isEmpty() || !fluidState.isSource()) {
                currentIndex++;
                continue;
            }

            // Check if this fluid type is valid using the filter
            if (!fluidStorage.isValid(TANK_SLOT, FluidResource.of(fluidState.getType()))) {
                currentIndex++;
                continue;
            }

            // Fluid found, try to consume it
            fluidFound = true;

            // Check if we can insert the fluid
            if (consumed >= ENERGY_PER_BUCKET) {
                try (Transaction transaction = Transaction.openRoot()) {
                    long inserted = fluidStorage.insert(TANK_SLOT, FluidResource.of(fluidState.getType()), FluidType.BUCKET_VOLUME, transaction);

                    if (inserted == FluidType.BUCKET_VOLUME) {
                        if (consumed >= ENERGY_PER_BUCKET) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                            transaction.commit();
                            consumed -= ENERGY_PER_BUCKET;
                            currentIndex++;
                            return;
                        }
                    }
                }
            } else {
                consumed += getEnergyStorage().consume(ENERGY_PER_BUCKET - consumed, null);
                return;
            }
        }
    }

    public BlockPos getParticleLocation() {
        return worldPosition.below(getRange() + 1);
    }

    @Override
    public void clientTick() {
        if (level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getParticleLocation(),
                    MachinesConfig.CLIENT.BLOCKS.DRAIN_RANGE_COLOR.get());
        }

        super.clientTick();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    private void updateLocations() {
        positions = new ArrayList<>();
        currentIndex = 0;
        int range = getRange();
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-range, -range * 2 - 1, -range),
                worldPosition.offset(range, -1, range))) {
            positions.add(pos.immutable()); // Need to make it immutable
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DrainMenu(containerId, playerInventory, this);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(CONSUMED, consumed);

        output.putChild("Fluid", fluidStorage);
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);

        if (!actionRange.equals(DEFAULT_RANGE)) {
            output.store(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC, actionRange);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        consumed = input.getIntOr(CONSUMED, 0);

        actionRange = input.read(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC)
            .orElse(DEFAULT_RANGE);

        input.child("Fluid")
            .ifPresent(fluidStorage::deserialize);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        var actionRange = components.get(EIODataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.actionRange = actionRange;
        }

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            fluidStorage.setStack(TANK_SLOT, storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        // Only if unchanged.
        if (!actionRange.equals(DEFAULT_RANGE)) {
            components.set(EIODataComponents.ACTION_RANGE, actionRange);
        }

        var fluidStored = getStoredFluid();
        if (!fluidStored.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(fluidStored));
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.ACTION_RANGE);
        output.discard(CONSUMED);
    }
}
