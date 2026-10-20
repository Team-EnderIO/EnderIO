package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.block.WrenchableBlockEntity;
import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.EIOBlockEntity;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.io.IOConfig;
import com.enderio.enderio.foundation.network.packets.ServerboundCycleIOConfigPacket;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Prediction;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CapacitorBankBlockEntity extends EIOBlockEntity implements MenuProvider, WrenchableBlockEntity, IOConfigurable {

    public static final ICapabilityProvider<CapacitorBankBlockEntity, Direction, EnergyHandler> ENERGY_STORAGE_PROVIDER = CapacitorBankEnergyStorage::getSided;
    public static final int MAX_SIZE = 4_096;
    public static final String NODE_ID = "NODE_ID";
    public static final Codec<Map<Direction, DisplayMode>> DISPLAY_MAP = Codec.unboundedMap(Direction.CODEC, DisplayMode.CODEC).xmap(to -> {
        var map = new EnumMap<Direction, DisplayMode>(Direction.class);
        map.putAll(to);
        return map;
    }, from -> from);

    private IOConfig ioConfig = IOConfig.empty();

    private final CapacitorTier tier;
    private CapacitorBankNode node;
    private CapacitorBankNetwork oldNetwork;
    private UUID uuid = UUID.randomUUID();

    private final Map<Direction, BlockCapabilityCache<EnergyHandler, Direction>> energyStorageCaches = new EnumMap<>(Direction.class);
    private final Set<SidedEnergy> validPushTargetCache = new HashSet<>();
    private boolean isValidPushTargetCacheDirty = true;

    private static final ModelProperty<IOConfigurable> IO_CONFIG_PROPERTY = MachineBlockEntity.IO_CONFIG_PROPERTY;
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

    public CapacitorBankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, CapacitorTier tier) {
        super(type, worldPosition, blockState);
        this.tier = tier;
    }

    public CapacitorTier tier() {
        return tier;
    }

    public UUID getUuid() {
        return uuid;
    }

    public CapacitorBankNetwork getNetwork() {
        return node.getNetwork();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.node.isValid()) {
            this.oldNetwork = node.getNetwork(); //Network is still needed for the picked up block
            this.node.getNetwork().remove(this.node);
        }
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
                    Capabilities.Energy.BLOCK,
                    serverLevel,
                    getBlockPos().relative(side),
                    side.getOpposite(),
                    () -> !isRemoved(),
                    () -> isValidPushTargetCacheDirty = true));
            }

            //Load the cap node
            CapacitorBankNode savedNote = CapacitorBankSavedData.get(serverLevel).claimNode(this.getBlockPos());
            if (savedNote != null) { //Load saved node
                this.node = savedNote;
                this.node.attach(this);
                this.redstoneControl = this.getNetwork().getRedstoneControl();
            } else {
                if (this.node == null) { //Create a new node if not created by placing a block
                    this.node = new CapacitorBankNode(this);
                }
                CapacitorBankSavedData.onNetworkCreated(serverLevel, this.node.getNetwork()); //Broadcast the network creation
            }

            for (Direction side : Direction.values()) {
                if (level.getBlockEntity(getBlockPos().relative(side)) instanceof CapacitorBankBlockEntity bank) {
                    if (bank.node != null && node != null) { //Happens in game tests
                        bank.node.getNetwork().connect(bank.node, node); //Connect to neighbours
                    }
                }
            }
        }
    }

    @Override
    public void onNeighbourBlockChanged(Block neighborBlock, BlockPos neighborPos) {
        if (!level.isClientSide()) {
            isValidPushTargetCacheDirty = true;
        }
        super.onNeighbourBlockChanged(neighborBlock, neighborPos);
    }

    Set<SidedEnergy> getValidPushTargets() {
        if (isValidPushTargetCacheDirty) {
            validPushTargetCache.clear();
            for (Direction side : Direction.values()) {
                if (side == Direction.UP) {
                    continue;
                }

                BlockCapabilityCache<EnergyHandler, Direction> cache = energyStorageCaches.get(side);
                var energyStorage = cache.getCapability();
                if (energyStorage != null && !(energyStorage instanceof CapacitorBankEnergyStorage) && getIOMode(side).canPush()) {
                    validPushTargetCache.add(new SidedEnergy(energyStorage, new CapacitorBankNetwork.SidedPos(getBlockPos(), side)));
                }
            }
            isValidPushTargetCacheDirty = false;
        }

        return validPushTargetCache;
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
        if (state.hasProperty(MachineBlockEntity.FACING)) {
            return getBlockState().getValue(MachineBlockEntity.FACING);
        }

        return Direction.SOUTH;
    }

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
                    inventory.placeItemBackInInventory(item, Prediction.SERVER_ONLY);
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

    public DisplayMode getDisplayMode(Direction direction) {
        if (getLevel() == null || !Block.shouldRenderFace(getLevel(), worldPosition, getBlockState(),
            getLevel().getBlockState(worldPosition.relative(direction)), direction)) {
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
        return new CapacitorBankMenu(i, inventory, this);
    }

    // Opt into network syncing
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.read(MachineNBTKeys.IO_CONFIG, IOConfig.CODEC)
            .ifPresent(ioConfig -> {
                this.ioConfig = ioConfig;
                if (level != null && level.isClientSide()) {
                    clientIOConfigChanged();
                }
            });

        input.read(MachineNBTKeys.REDSTONE_CONTROL, RedstoneControl.CODEC)
            .ifPresent(control -> redstoneControl = control);

        input.read(NODE_ID, UUIDUtil.CODEC)
            .ifPresent(uuid -> this.uuid = uuid);

        input.read(DISPLAY_MODES, DISPLAY_MAP)
            .ifPresent(list -> {
                displayModes.clear();
                displayModes.putAll(list);
            });

    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);

        if (ioConfig != null) {
            output.store(MachineNBTKeys.IO_CONFIG, IOConfig.CODEC, ioConfig);
        }

        output.store(DISPLAY_MODES, DISPLAY_MAP, displayModes);

        if (node != null && node.isValid()) { //Save data if present
            output.store(NODE_ID, UUIDUtil.CODEC, getNetwork().getUuid());
            output.store(MachineNBTKeys.REDSTONE_CONTROL, RedstoneControl.CODEC, redstoneControl);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        ioConfig = components.getOrDefault(EIODataComponents.IO_CONFIG, IOConfig.empty());

        if (this.node == null) { //Called before being placed, so a node needs to be made
            this.node = new CapacitorBankNode(this);
        }

        try (Transaction transaction = Transaction.openRoot()){
            getNetwork().receiveEnergy(this.getBlockPos(), null, components.getOrDefault(EIODataComponents.ENERGY, 0), transaction);
            transaction.commit();
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        components.set(EIODataComponents.IO_CONFIG, ioConfig);

        if (oldNetwork != null) { //Network already kicked out the node, so we keep an old ref
            int energy = oldNetwork.getEnergyForNode(this.tier());
            if (energy != 0) {
                components.set(EIODataComponents.ENERGY, energy);
            }
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.IO_CONFIG);
        output.discard(MachineNBTKeys.REDSTONE_CONTROL);
    }

    public record SidedEnergy(EnergyHandler storage, CapacitorBankNetwork.SidedPos sidedPos) {}
}
