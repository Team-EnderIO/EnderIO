package com.enderio.core.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.capability.IEnderCapabilityProvider;
import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.SyncMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Base block entity class for EnderIO.
 * Handles data slot syncing and capability providers.
 */
public class EnderBlockEntity extends BlockEntity {
    /**
     * This list is needed to send a full update packet to Players who started tracking this BlockEntity.
     * The Clients only receive changed data to reduce the amount of Data sent to the client.
     */
    private final List<UUID> lastSyncedToPlayers = new ArrayList<>();

    private final List<EnderDataSlot<?>> dataSlots = new ArrayList<>();

    private final List<EnderDataSlot<?>> clientDecidingDataSlots = new ArrayList<>();

    private final List<Runnable> afterDataSync = new ArrayList<>();

    private final Map<Capability<?>, IEnderCapabilityProvider<?>> capabilityProviders = new HashMap<>();

    public EnderBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    // region Ticking

    public static void tick(Level level, BlockPos pos, BlockState state, EnderBlockEntity blockEntity) {
        if (level.isClientSide) {
            blockEntity.clientTick();
        } else {
            blockEntity.serverTick();
        }
    }

    /**
     * Perform server-side ticking
     */
    public void serverTick() {
        // Perform syncing.
        if (level != null && !level.isClientSide) {
            sync();
            setChanged();

            for (var dataSlot : dataSlots) {
                dataSlot.clearHasChangedFlag();
            }
        }
    }

    /**
     * Perform client side ticking.
     */
    public void clientTick() {

    }

    public boolean isClientSide() {
        if (level != null)
            return level.isClientSide;
        return false;
    }

    // endregion

    // region Sync

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return createUpdatePacket(false, SyncMode.WORLD);
    }

    /**
     * create the ClientBoundBlockEntityDataPacket for this BlockEntity
     * @return the UpdatePacket
     */
    @Nullable
    public ClientboundBlockEntityDataPacket createUpdatePacket(boolean fullUpdate, SyncMode mode) {
        CompoundTag nbt = new CompoundTag();
        ListTag listNBT = new ListTag();
        for (int i = 0; i < this.dataSlots.size(); i++) {
            EnderDataSlot<?> dataSlot = this.dataSlots.get(i);
            if (dataSlot.getSyncMode() == mode) {
                Optional<CompoundTag> optionalNBT = fullUpdate ? Optional.of(dataSlot.toFullNBT()) : dataSlot.toOptionalNBT();

                if (optionalNBT.isPresent()) {
                    CompoundTag elementNBT = optionalNBT.get();
                    elementNBT.putInt("dataSlotIndex", i);
                    listNBT.add(elementNBT);
                }
            }
        }

        if (listNBT.isEmpty())
            return null;

        nbt.put("data", listNBT);
        return new ClientboundBlockEntityDataPacket(getBlockPos(), getType(), nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null && nbt.contains("data", Tag.TAG_LIST)) {
            ListTag listNBT = nbt.getList("data", Tag.TAG_COMPOUND);
            for (Tag tag : listNBT) {
                CompoundTag elementNBT = (CompoundTag) tag;
                int dataSlotIndex = elementNBT.getInt("dataSlotIndex");
                dataSlots.get(dataSlotIndex).handleNBT(elementNBT);
            }
            afterDataSync.forEach(Runnable::run);
        }
    }

    public void addDataSlot(EnderDataSlot<?> slot) {
        dataSlots.add(slot);
    }

    public void addClientDecidingDataSlot(EnderDataSlot<?> slot) {
        clientDecidingDataSlots.add(slot);
    }

    public void add2WayDataSlot(EnderDataSlot<?> slot) {
        addDataSlot(slot);
        addClientDecidingDataSlot(slot);
    }

    public void addAfterSyncRunnable(Runnable runnable) {
        afterDataSync.add(runnable);
    }

    /**
     * Sync the BlockEntity to all tracking players. Don't call this if you don't know what you do
     */
    @UseOnly(LogicalSide.SERVER)
    public void sync() {
        ClientboundBlockEntityDataPacket fullUpdate = createUpdatePacket(true, SyncMode.WORLD);
        ClientboundBlockEntityDataPacket partialUpdate = getUpdatePacket();

        List<UUID> currentlyTracking = new ArrayList<>();

        getTrackingPlayers().forEach(serverPlayer -> {
            currentlyTracking.add(serverPlayer.getUUID());
            if (lastSyncedToPlayers.contains(serverPlayer.getUUID())) {
                sendPacket(serverPlayer, partialUpdate);
            } else {
                sendPacket(serverPlayer, fullUpdate);
            }
        });
        lastSyncedToPlayers.clear();
        lastSyncedToPlayers.addAll(currentlyTracking);
    }

    public void sendPacket(ServerPlayer player, @Nullable Packet<?> packet) {
        if (packet != null)
            player.connection.send(packet);
    }

    /**
     * never call this on client
     * @return all ServerPlayers tracking this BlockEntity
     */
    @UseOnly(LogicalSide.SERVER)
    private List<ServerPlayer> getTrackingPlayers() {
        return ((ServerChunkCache)level.getChunkSource()).chunkMap.getPlayers(new ChunkPos(worldPosition), false);
    }

    public List<EnderDataSlot<?>> getDataSlots() {
        return dataSlots;
    }

    public List<EnderDataSlot<?>> getClientDecidingDataSlots() {
        return clientDecidingDataSlots;
    }

    // endregion

    // region Capabilities

    /**
     * Get all capability providers
     */
    public Map<Capability<?>, IEnderCapabilityProvider<?>> getCapabilityProviders() {
        return capabilityProviders;
    }

    /**
     * Add a capability provider to the block entity.
     */
    public void addCapabilityProvider(IEnderCapabilityProvider<?> provider) {
        capabilityProviders.put(provider.getCapabilityType(), provider);
    }

    /**
     * Invalidate capabilities serving the given side.
     */
    public void invalidateCaps(@Nullable Direction side) {
        for (IEnderCapabilityProvider<?> capProvider : capabilityProviders.values()) {
            capProvider.invalidateSide(side);
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (capabilityProviders.containsKey(cap)) {
            return capabilityProviders.get(cap).getCapability(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (IEnderCapabilityProvider<?> provider : capabilityProviders.values()) {
            provider.invalidateCaps();
        }
    }

    // endregion
}
