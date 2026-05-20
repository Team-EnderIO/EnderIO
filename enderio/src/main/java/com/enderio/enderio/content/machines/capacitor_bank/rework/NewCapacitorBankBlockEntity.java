package com.enderio.enderio.content.machines.capacitor_bank.rework;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorTier;
import com.enderio.enderio.content.machines.capacitor_bank.DisplayMode;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.EIOBlockEntity;
import com.enderio.enderio.foundation.block.entity.Wrenchable;
import com.enderio.enderio.foundation.block.entity.legacy.LegacyMachineBlockEntity;
import com.enderio.enderio.foundation.block.legacy.LegacyMachineBlock;
import com.enderio.enderio.foundation.io.IOConfig;
import com.enderio.enderio.foundation.network.packets.ClientBoundSyncCapacitorBankPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundCycleIOConfigPacket;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOAttachments;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class NewCapacitorBankBlockEntity extends EIOBlockEntity implements MenuProvider, Wrenchable, IOConfigurable {

    public static final ICapabilityProvider<NewCapacitorBankBlockEntity, Direction, IEnergyStorage> ENERGY_STORAGE_PROVIDER = (
        be, side) -> side == null ? be.energyStorage : be.energyStorage.getSided(side);
    public static final int MAX_SIZE = 4_096;
    public static final String NODE_ID = "NODE_ID";
    public static final int AVERAGE_IO_OVER_X_TICKS = 10;

    private IOConfig ioConfig = IOConfig.empty();

    private final CapacitorTier tier;
    private final CapacitorBankNode node;
    private final CapacitorBankEnergyStorage energyStorage;
    private UUID uuid;

    private final Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> energyStorageCaches = new EnumMap<>(Direction.class);
    private final Set<IEnergyStorage> validPushTargetCache = new HashSet<>();
    private boolean isValidPushTargetCacheDirty = true;

    private static final ModelProperty<IOConfigurable> IO_CONFIG_PROPERTY = LegacyMachineBlockEntity.IO_CONFIG_PROPERTY;
    private static final String DISPLAY_MODES = "displaymodes";

    private final Map<Direction, DisplayMode> displayModes = Util.make(() -> {
        Map<Direction, DisplayMode> map = new EnumMap<>(Direction.class);
        for (Direction direction : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH,
            Direction.WEST }) {
            map.put(direction, DisplayMode.NONE);
        }

        return map;
    });

    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;
    private boolean isRedstoneBlocked;

    public NewCapacitorBankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, CapacitorTier tier) {
        super(type, worldPosition, blockState);
        this.tier = tier;

        this.node = new CapacitorBankNode(this);
        this.energyStorage = new CapacitorBankEnergyStorage(node);
    }

    public CapacitorTier getTier() {
        return tier;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setRemoved() {
        if (node.isValid()) {
            node.getNetwork().remove(node);
        }

        super.setRemoved();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        // Create all energy caches
        if (level instanceof ServerLevel serverLevel) {
            for (Direction side : Direction.values()) {
                if (side == Direction.UP) {
                    continue;
                }

                energyStorageCaches.put(side, BlockCapabilityCache.create(
                    Capabilities.EnergyStorage.BLOCK,
                    serverLevel,
                    getBlockPos().relative(side),
                    side.getOpposite(),
                    () -> !isRemoved(),
                    () -> isValidPushTargetCacheDirty = true));
            }
        }

        for (Direction side : Direction.values()) {
            if (level.getBlockEntity(getBlockPos().relative(side)) instanceof NewCapacitorBankBlockEntity bank) {
                node.getNetwork().connect(node, bank.node);
            }
        }
    }

    @Override
    public void neighborChanged(Block neighborBlock, BlockPos neighborPos) {
        super.neighborChanged(neighborBlock, neighborPos);

        if (!level.isClientSide()) {
            isValidPushTargetCacheDirty = true;
        }
    }

    Set<IEnergyStorage> getValidPushTargets() {
        if (isValidPushTargetCacheDirty) {
            validPushTargetCache.clear();
            for (Direction side : Direction.values()) {
                if (side == Direction.UP) {
                    continue;
                }

                var energyStorage = energyStorageCaches.get(side).getCapability();
                if (energyStorage != null && !(energyStorage instanceof CapacitorBankEnergyStorage || energyStorage instanceof CapacitorBankEnergyStorage.SidedAccess) &&
                    energyStorage.canReceive() && getIOMode(side).canOutput()) {
                    validPushTargetCache.add(energyStorage);
                }
            }
            isValidPushTargetCacheDirty = false;
        }

        return validPushTargetCache;
    }

    @Override
    public void serverTick() {
        // Push energy to non-bank neighbors
        if (node.isPrimaryNode()) {
            node.distributeEnergy();
            if (level.getGameTime() % AVERAGE_IO_OVER_X_TICKS == 0) {
                CapacitorBankNetwork network = node.getNetwork();
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(getBlockPos()),
                    new ClientBoundSyncCapacitorBankPacket(network.getUuid(), network.getTotalEnergyStored(),
                        network.getTotalMaxEnergyStored(), network.getAddedEnergy()  / AVERAGE_IO_OVER_X_TICKS,
                        network.getSendEnergy() / AVERAGE_IO_OVER_X_TICKS, network.positions()));
                network.reset(level.getGameTime());
            }
        }

        super.serverTick();
    }

    private void setIOConfig(IOConfig ioConfig) {
        this.ioConfig = ioConfig;

        if (level != null) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public IOMode getIOMode(Direction side) {
        return ioConfig.getMode(translateIOSide(side));
    }

    @Override
    public boolean isIOConfigMutable() {
        return true;
    }

    @Override
    public boolean shouldRenderIOConfigOverlay() {
        return true;
    }

    @Override
    public void setIOMode(Direction side, IOMode mode) {
        Direction localSide = translateIOSide(side);
        setIOConfig(ioConfig.withMode(localSide, mode));

        // Invalidate caps
        level.invalidateCapabilities(worldPosition);
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        isValidPushTargetCacheDirty = true;
    }

    @Override
    public boolean supportsIOMode(Direction side, IOMode state) {
        return true;
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(IO_CONFIG_PROPERTY, this).build();
    }

    @UseOnly(LogicalSide.CLIENT)
    private void clientIOConfigChanged() {
        requestModelDataUpdate();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
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

    public final RedstoneControl getRedstoneControl() {
        return redstoneControl;
    }

    public void setRedstoneControl(RedstoneControl control) {
        redstoneControl = control;
        setChanged();
        checkIsRedstoneBlocked();
    }

    public void setNetworkRedstoneControl(RedstoneControl control) {
        node.getNetwork().setRedstoneControl(control);
    }

    @Override
    protected boolean supportsRedstonePower() {
        return true;
    }

    @Override
    protected void updateRedstonePower() {
        super.updateRedstonePower();
        checkIsRedstoneBlocked();
    }

    private void checkIsRedstoneBlocked() {
        isRedstoneBlocked = !redstoneControl.isActive(isRedstonePowered());
    }

    protected boolean isRedstoneBlocked() {
        return isRedstoneBlocked;
    }

    /**
     * Get the facing direction of the machine.
     */
    protected final Direction getBlockFacing() {
        BlockState state = getBlockState();
        if (state.hasProperty(LegacyMachineBlock.FACING)) {
            return getBlockState().getValue(LegacyMachineBlock.FACING);
        }

        return Direction.SOUTH;
    }

    @Override
    public ItemInteractionResult onWrenched(UseOnContext context) {
        var player = context.getPlayer();
        if (player == null || level == null) {
            return ItemInteractionResult.SUCCESS;
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

            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        } else {
            if (level.isClientSide()) {
                if (isIOConfigMutable()) {
                    PacketDistributor.sendToServer(new ServerboundCycleIOConfigPacket(worldPosition, context.getClickedFace()));
                }
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
    }

    public DisplayMode getDisplayMode(Direction direction) {
        if (getLevel() == null || !Block.shouldRenderFace(getBlockState(), getLevel(), worldPosition, direction,
            worldPosition.relative(direction))) {
            return DisplayMode.NONE;
        }

        return displayModes.get(direction);
    }

    public void setDisplayMode(Direction direction, DisplayMode mode) {
        displayModes.put(direction, mode);
    }

    public boolean onShiftRightClick(Direction direction, Player player) {
        if (direction.getAxis().getPlane() == Direction.Plane.VERTICAL) {
            return false;
        }

        if (player.getMainHandItem().getItem() instanceof BlockItem
            || player.getOffhandItem().getItem() instanceof BlockItem) {
            return false;
        }

        if (player.getMainHandItem().is(EIOTags.Items.WRENCH)) {
            return false;
        }

        displayModes.put(direction,
            DisplayMode.values()[(displayModes.get(direction).ordinal() + 1) % DisplayMode.values().length]);
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        return true;
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new NewCapacitorBankMenu(i, inventory, this);
    }

    // Opt into network syncing
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        // TODO: Ender IO 8 - remove.
        if (hasData(EIOAttachments.IO_CONFIG)) {
            ioConfig = getData(EIOAttachments.IO_CONFIG);
            removeData(EIOAttachments.IO_CONFIG);
        } else if (tag.contains(MachineNBTKeys.IO_CONFIG)) {
            ioConfig = IOConfig.parseOptional(registries, tag.getCompound(MachineNBTKeys.IO_CONFIG));

            if (level != null && level.isClientSide) {
                clientIOConfigChanged();
            }
        }

        // Old serialization format
        if (tag.contains(MachineNBTKeys.ENERGY)) {
            var energyStorage = tag.getCompound(MachineNBTKeys.ENERGY);
            if (energyStorage.contains(MachineNBTKeys.ENERGY_STORED)) {
                this.node.setEnergyStored(energyStorage.getInt(MachineNBTKeys.ENERGY_STORED));
            }
        } else if (tag.contains(MachineNBTKeys.ENERGY_STORED)) {
            this.node.setEnergyStored(tag.getInt(MachineNBTKeys.ENERGY_STORED));
        }

        if (tag.contains(NODE_ID)) {
            uuid = tag.getUUID(NODE_ID);
        }

        if (tag.contains(DISPLAY_MODES, Tag.TAG_COMPOUND)) {
            loadDisplayModes(tag.getCompound(DISPLAY_MODES));
        }

        // TODO: Ender IO 8 - remove.
        if (hasData(EIOAttachments.REDSTONE_CONTROL)) {
            redstoneControl = getData(EIOAttachments.REDSTONE_CONTROL);
            removeData(EIOAttachments.REDSTONE_CONTROL);
        } else if (tag.contains(MachineNBTKeys.REDSTONE_CONTROL)) {
            redstoneControl = RedstoneControl.parse(registries,
                Objects.requireNonNull(tag.get(MachineNBTKeys.REDSTONE_CONTROL)));
        }

        if (node.isPrimaryNode()) {
            node.getNetwork().setRedstoneControl(redstoneControl);
        }
    }

    public void loadDisplayModes(CompoundTag nbt) {
        displayModes.clear();
        for (String key : nbt.getAllKeys()) {
            @Nullable
            Direction dir = Direction.byName(key);
            if (dir != null) {
                displayModes.put(dir, DisplayMode.values()[nbt.getInt(key)]);
            }
        }
    }

    @Override
    protected void saveAdditionalSynced(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalSynced(tag, registries);

        if (isIOConfigMutable() && ioConfig != null) {
            tag.put(MachineNBTKeys.IO_CONFIG, ioConfig.save(registries));
        }

        tag.putUUID(NODE_ID, node.getNetwork().getUuid());

        tag.put(DISPLAY_MODES, saveDisplayModes());

        tag.put(MachineNBTKeys.REDSTONE_CONTROL, node.getNetwork().getRedstoneControl().save(registries));
    }

    public CompoundTag saveDisplayModes() {
        CompoundTag nbt = new CompoundTag();
        for (var entry : displayModes.entrySet()) {
            nbt.putInt(entry.getKey().getName(), entry.getValue().ordinal());
        }

        return nbt;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        // TODO: 26.1 - serialize the node instead?
        tag.putInt(MachineNBTKeys.ENERGY_STORED, node.getEnergyStored());

        super.saveAdditional(tag, lookupProvider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        if (isIOConfigMutable()) {
            ioConfig = components.getOrDefault(EIODataComponents.IO_CONFIG, IOConfig.empty());
        }

        node.setEnergyStored(components.getOrDefault(EIODataComponents.ENERGY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (isIOConfigMutable()) {
            components.set(EIODataComponents.IO_CONFIG, ioConfig);
        }

        if (node.getEnergyStored() != 0) {
            components.set(EIODataComponents.ENERGY, node.getEnergyStored());
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(MachineNBTKeys.IO_CONFIG);
    }
}
