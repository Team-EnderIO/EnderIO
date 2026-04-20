package com.enderio.enderio.foundation.block.entity;

import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.api.io.SideConfig;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.EIOBlockEntity;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.io.IOConfig;
import com.enderio.enderio.foundation.io.SidedIOConfigurable;
import com.enderio.enderio.foundation.io.TransferUtil;
import com.enderio.enderio.foundation.network.packets.ServerboundCycleIOConfigPacket;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.state.MachineStateUpdater;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Base block entity implementation for machines.
 * Implements Redstone Control and the Machine State system.
 */
public abstract class MachineBlockEntity extends EIOBlockEntity implements MenuProvider, Wrenchable, IOConfigurable, MachineInventoryHolder,
    MachineStateUpdater {

    public static final ICapabilityProvider<MachineBlockEntity, Direction, SideConfig> SIDE_CONFIG_PROVIDER =
        (be, side) -> side != null && be.isIOConfigMutable() ? new SidedIOConfigurable(be, side) : null;

    public static final ICapabilityProvider<MachineBlockEntity, Direction, ResourceHandler<ItemResource>> ITEM_HANDLER_PROVIDER =
        (be, side) -> be.inventory != null ? SidedResourceHandler.of(be.inventory, side, be) : null;

    public static final ICapabilityProvider<MachineBlockEntity, Void, SoulBindable> SOUL_BINDABLE = (be, ctx)
        -> be instanceof SoulBindable bindable ? bindable : null;

    public static final ModelProperty<IOConfigurable> IO_CONFIG_PROPERTY = new ModelProperty<>();

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Nullable
    private final ItemStorage inventory;

    private IOConfig ioConfig;
    private final boolean isIoConfigMutable;

    private Set<MachineState> states = new HashSet<>();

    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;
    private boolean isRedstoneBlocked;

    private final boolean supportsActiveState;

    @Nullable
    private UUID owner;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState,
            boolean isIoConfigMutable) {
        super(type, worldPosition, blockState);

        this.isIoConfigMutable = isIoConfigMutable;
        this.ioConfig = getDefaultIOConfig();

        // Create inventory if the machine has a layout
        var layout = createInventoryLayout();
        if (layout != null) {
            inventory = new ItemStorage(layout) {
                @Override
                protected void onContentsChanged(int index, ItemStack previousContents) {
                    super.onContentsChanged(index, previousContents);
                    onInventoryContentsChanged(index);
                    setChanged();
                }
            };
        } else {
            inventory = null;
        }

        this.supportsActiveState = blockState.hasProperty(ProgressMachineBlock.POWERED);
    }

    /**
     * @return True if the machine is active, this applies the POWERED block state if available.
     */
    public abstract boolean isActive();

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct(distributeResourcesInterval())) {
            distributeResources();
        }

        // Every 5 ticks, ensure active state is up-to-date.
        // Doing this every 5 ticks instead of every tick should reduce visual flicker.
        if (level != null && level.getGameTime() % 5 == 0) {

            boolean isActive = isActive();
            boolean needBlockStateUpdate = supportsActiveState
                    && getBlockState().getValue(ProgressMachineBlock.POWERED) != isActive;
            boolean needStateUpdate = states.contains(MachineState.ACTIVE) != isActive;

            if (needBlockStateUpdate) {
                level.setBlockAndUpdate(worldPosition,
                        getBlockState().setValue(ProgressMachineBlock.POWERED, isActive));
            }

            if (needStateUpdate) {
                updateMachineState(MachineState.ACTIVE, isActive);
            }
        }
    }

    /**
     * Get the facing direction of the machine.
     */
    protected final Direction getBlockFacing() {
        BlockState state = getBlockState();
        if (state.hasProperty(FACING)) {
            return getBlockState().getValue(FACING);
        }

        return Direction.SOUTH;
    }

    // region Ticking Control

    public boolean canAct() {
        if (level == null) {
            return false;
        }

        return !isRedstoneBlocked();
    }

    public boolean canAct(int interval) {
        return level != null && canAct() && level.getGameTime() % interval == 0;
    }

    // endregion

    // region Inventory

    /**
     * Define the slot layout for the machine.
     * @return The slot layout or null for no inventory.
     */
    @Nullable
    protected ItemStorageLayout createInventoryLayout() {
        return null;
    }

    @Override
    public final ItemStorage getInventory() {
        if (!hasInventory()) {
            throw new IllegalStateException("This machine does not have an inventory.");
        }

        return inventory;
    }

    public final boolean hasInventory() {
        return inventory != null;
    }

    /**
     * @apiNote Must call this on custom MachineInventory handlers!
     */
    protected void onInventoryContentsChanged(int slot) {
    }

    // endregion

    // region IO Config

    public IOConfig getDefaultIOConfig() {
        return IOConfig.empty();
    }

    public final IOConfig getIOConfig() {
        if (isIOConfigMutable()) {
            return ioConfig;
        }

        return getDefaultIOConfig();
    }

    private void setIOConfig(IOConfig ioConfig) {
        this.ioConfig = ioConfig;

        if (level != null) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    // region IOConfigurable Implementation

    @Override
    public IOMode getIOMode(Direction side) {
        return getIOConfig().getMode(translateIOSide(side));
    }

    @Override
    public final boolean isIOConfigMutable() {
        return isIoConfigMutable;
    }

    @Override
    public boolean shouldRenderIOConfigOverlay() {
        return isIOConfigMutable();
    }

    @Override
    public void setIOMode(Direction side, IOMode mode) {
        if (!isIOConfigMutable()) {
            throw new IllegalStateException("Cannot edit fixed IO mode.");
        }

        if (!supportsIOMode(side, mode)) {
            throw new IllegalStateException("Cannot use this mode on this side.");
        }

        Direction localSide = translateIOSide(side);
        setIOConfig(ioConfig.withMode(localSide, mode));

        // Invalidate caps
        level.invalidateCapabilities(worldPosition);
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public ModelData getModelData() {
        if (!shouldRenderIOConfigOverlay()) {
            return ModelData.EMPTY;
        }

        return ModelData.builder().with(IO_CONFIG_PROPERTY, this).build();
    }

    @UseOnly(LogicalSide.CLIENT)
    private void clientIOConfigChanged() {
        if (shouldRenderIOConfigOverlay()) {
            requestModelDataUpdate();
        }

        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public boolean supportsIOMode(Direction side, IOMode state) {
        return true;
    }

    private Direction translateIOSide(Direction side) {
        // The block faces with its southern face. So the back of the machine.
        Direction south = getBlockFacing();
        return switch (side) {
        case NORTH -> south.getOpposite();
        case SOUTH -> south;
        case WEST -> south.getCounterClockWise();
        case EAST -> south.getClockWise();
        default -> side;
        };
    }

    // endregion

    // endregion

    public void setMachineOwner(UUID owner) {
        this.owner = owner;
        setChanged();
    }

    @Nullable
    public UUID getMachineOwner() {
        return this.owner;
    }

    public UUID getMachineOwnerOrRandom() {
        return Objects.requireNonNullElseGet(getMachineOwner(), UUID::randomUUID);
    }

    // region Resource Distribution

    // TODO: I kind of want to rewrite this without relying on getSelfCapability.

    /**
     * If you distribute resources faster than a normal machine, set the minimum interval here.
     */
    protected int distributeResourcesInterval() {
        return 5;
    }

    @UseOnly(LogicalSide.SERVER)
    protected void distributeResources() {
        // TODO: Quick way to see if any sides are set to force.

        for (Direction side : Direction.values()) {
            IOMode mode = getIOMode(side);
            if (mode.canForce()) {
                distributeResources(side);
            }
        }
    }

    protected void distributeResources(Direction side) {
        if (canAct(20)) {
            distributeItems(side);
        }

        if (canAct(5)) {
            distributeFluids(side);
        }
    }

    private void distributeItems(Direction side) {
        ResourceHandler<ItemResource> selfHandler = getSelfCapability(Capabilities.Item.BLOCK, side);
        ResourceHandler<ItemResource> otherHandler = getNeighbouringCapability(Capabilities.Item.BLOCK, side);
        if (selfHandler == null || otherHandler == null) {
            return;
        }

        TransferUtil.distributeItems(getIOMode(side), selfHandler, otherHandler);
    }

    private void distributeFluids(Direction side) {
        ResourceHandler<FluidResource> selfHandler = getSelfCapability(Capabilities.Fluid.BLOCK, side);
        ResourceHandler<FluidResource> otherHandler = getNeighbouringCapability(Capabilities.Fluid.BLOCK, side);
        if (selfHandler == null || otherHandler == null) {
            return;
        }

        TransferUtil.distributeFluids(getIOMode(side), selfHandler, otherHandler);
    }

    // endregion

    // region Machine States

    public Set<MachineState> getMachineStates() {
        return states;
    }

    @UseOnly(LogicalSide.CLIENT)
    public void clientSetMachineStates(Set<MachineState> states) {
        if (level == null) {
            return;
        }

        this.states = states;
    }

    @Override
    public void updateMachineState(MachineState state, boolean predicate) {
        if (predicate) {
            pushMachineState(state);
        } else {
            popMachineState(state);
        }
    }

    @UseOnly(LogicalSide.SERVER)
    protected void pushMachineState(MachineState state) {
        if (level == null) {
            return;
        }

        states.add(state);
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @UseOnly(LogicalSide.SERVER)
    protected void popMachineState(MachineState state) {
        if (level == null) {
            return;
        }

        if (states.remove(state)) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    // endregion

    // region Redstone Control

    // TODO: Maybe put this into a constructor parameter instead.
    /**
     * Whether the machine can be controlled by Redstone.
     * Do not turn on/off dynamically.
     */
    public boolean supportsRedstoneControl() {
        return true;
    }

    /**
     * @return The redstone control mode for the machine
     * @throws IllegalStateException if the machine does not support redstone control.
     */
    public final RedstoneControl getRedstoneControl() {
        if (!supportsRedstoneControl()) {
            throw new IllegalStateException("This machine does not support redstone control.");
        }

        return redstoneControl;
    }

    public void setRedstoneControl(RedstoneControl control) {
        redstoneControl = control;
        setChanged();
        checkIsRedstoneBlocked();
    }

    // Final, handoff to supportsRedstoneControl now.
    @Override
    protected final boolean supportsRedstonePower() {
        return supportsRedstoneControl();
    }

    @Override
    protected void updateRedstonePower() {
        super.updateRedstonePower();
        checkIsRedstoneBlocked();
    }

    private void checkIsRedstoneBlocked() {
        if (supportsRedstoneControl()) {
            isRedstoneBlocked = !redstoneControl.isActive(isRedstonePowered());
            updateMachineState(MachineState.REDSTONE, isRedstoneBlocked);
        }
    }

    protected boolean isRedstoneBlocked() {
        return isRedstoneBlocked;
    }

    // endregion

    // region Wrenchable Implementation

    @Override
    public InteractionResult onWrenched(UseOnContext context) {
        var player = context.getPlayer();
        if (player == null || level == null) {
            return InteractionResult.SUCCESS;
        }

        // Holding shift
        if (player.isSecondaryUseActive()) {
            BlockPos pos = getBlockPos();
            BlockState state = getBlockState();
            Block block = state.getBlock();

            if (level instanceof ServerLevel serverLevel) {
                List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, serverLevel.getBlockEntity(pos));
                Inventory inventory = player.getInventory();
                for (ItemStack item : drops) {
                    inventory.placeItemBackInInventory(item);
                }
            }

            block.playerWillDestroy(level, pos, state, player);
            level.removeBlock(pos, false);
            block.destroy(level, pos, state);

            // TODO: custom sound when sound manager is up and running??

            return InteractionResult.SUCCESS;
        } else {
            if (level.isClientSide()) {
                if (isIOConfigMutable()) {
                    ClientPacketDistributor.sendToServer(new ServerboundCycleIOConfigPacket(worldPosition, context.getClickedFace()));
                }
            }

            return InteractionResult.SUCCESS;
        }
    }

    // endregion

    // region Menu Provider Implementation

    @Override
    public Component getDisplayName() {
        // Default the menu title to the name of the machine's block.
        return getBlockState().getBlock().getName();
    }

    // endregion

    // region Serialization

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (supportsRedstoneControl()) {
            output.store(MachineNBTKeys.REDSTONE_CONTROL, RedstoneControl.CODEC, redstoneControl);
        }

        if (hasInventory()) {
            output.putChild(MachineNBTKeys.ITEMS, inventory);
        }
    }

    // Opt into network syncing
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);

        if (isIoConfigMutable && ioConfig != null) {
            output.store(MachineNBTKeys.IO_CONFIG, IOConfig.CODEC, ioConfig);
        }

        if (owner != null) {
            output.store(MachineNBTKeys.OWNER, UUIDUtil.CODEC, owner);
        }
    }

    @SuppressWarnings("removal")
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        if (hasInventory()) {
            input.child(MachineNBTKeys.ITEMS)
                .ifPresent(inventory::deserialize);
        }

        input.read(MachineNBTKeys.IO_CONFIG, IOConfig.CODEC)
            .ifPresent(ioConfig -> {
                this.ioConfig = ioConfig;
                if (level != null && level.isClientSide()) {
                    clientIOConfigChanged();
                }
            });

        if (supportsRedstoneControl()) {
            input.read(MachineNBTKeys.REDSTONE_CONTROL, RedstoneControl.CODEC)
                .ifPresent(control -> redstoneControl = control);
        }

        input.read(MachineNBTKeys.OWNER, UUIDUtil.CODEC)
            .ifPresent(owner -> this.owner = owner);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);

        if (hasInventory()) {
            this.inventory.copyFromItem(componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
        }

        if (isIOConfigMutable()) {
            ioConfig = componentInput.getOrDefault(EIODataComponents.IO_CONFIG, IOConfig.empty());
        }

        if (supportsRedstoneControl()) {
            var redstoneControl = componentInput.get(EIODataComponents.REDSTONE_CONTROL);
            if (redstoneControl != null) {
                this.redstoneControl = redstoneControl;
            }
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (hasInventory()) {
            components.set(DataComponents.CONTAINER, this.inventory.toItemContents());
        }

        if (isIOConfigMutable()) {
            components.set(EIODataComponents.IO_CONFIG, ioConfig);
        }

        if (supportsRedstoneControl()) {
            components.set(EIODataComponents.REDSTONE_CONTROL, redstoneControl);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.ITEMS);
        output.discard(MachineNBTKeys.IO_CONFIG);
        output.discard(MachineNBTKeys.REDSTONE_CONTROL);
    }

    // endregion
}
