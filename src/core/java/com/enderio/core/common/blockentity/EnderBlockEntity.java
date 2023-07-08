package com.enderio.core.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.capability.IEnderCapabilityProvider;
import com.enderio.core.common.network.C2SDataSlotChange;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.core.common.network.S2CDataSlotUpdate;
import com.enderio.core.common.network.slot.NetworkDataSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Base block entity class for EnderIO.
 * Handles data slot syncing and capability providers.
 */
public class EnderBlockEntity extends BlockEntity {

    private final List<NetworkDataSlot<?>> dataSlots = new ArrayList<>();

    private final List<Runnable> afterDataSync = new ArrayList<>();

    private final Map<Capability<?>, IEnderCapabilityProvider<?>> capabilityProviders = new HashMap<>();

    public EnderBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    // region Ticking

    @SuppressWarnings("unused")
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
        }
    }

    /**
     * Perform client side ticking.
     */
    public void clientTick() {

    }

    // endregion

    // region Sync

    /**
     * This is the initial packet sent to a client loading the block (or when it is placed).
     */
    @Override
    public CompoundTag getUpdateTag() {
        return createDataSlotUpdate(true);
    }

    /**
     * This is the client handling the tag above.
     * @param tag The {@link CompoundTag} sent from {@link BlockEntity#getUpdateTag()}
     */
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        clientHandleDataSync(tag);
    }

    @Nullable
    private CompoundTag createDataSlotUpdate(boolean fullUpdate) {
        ListTag dataList = new ListTag();
        for (int i = 0; i < dataSlots.size(); i++) {
            var slot = dataSlots.get(i);
            var nbt = slot.serializeNBT(fullUpdate);
            if (nbt == null)
                continue;

            CompoundTag slotTag = new CompoundTag();
            slotTag.putInt("Index", i);
            slotTag.put("Data", nbt);

            dataList.add(slotTag);
        }

        if (!fullUpdate && dataList.isEmpty()) {
            return null;
        }

        CompoundTag data = new CompoundTag();
        data.put("Data", dataList);
        return data;
    }

    public void addDataSlot(NetworkDataSlot<?> slot) {
        dataSlots.add(slot);
    }

    public void addAfterSyncRunnable(Runnable runnable) {
        afterDataSync.add(runnable);
    }

    /**
     * Fire this when you change the value of a {@link NetworkDataSlot} on the client side.
     */
    @UseOnly(LogicalSide.CLIENT)
    public <T> void clientUpdateSlot(@Nullable NetworkDataSlot<T> slot, T value) {
        if (slot == null) {
            return;
        }

        if (dataSlots.contains(slot)) {
            CompoundTag updateData = new CompoundTag();
            updateData.putInt("Index", dataSlots.indexOf(slot));
            updateData.put("Data", slot.serializeValueNBT(value));
            CoreNetwork.sendToServer(new C2SDataSlotChange(getBlockPos(), updateData));
        }
    }

    /**
     * Sync the BlockEntity to all tracking players. Don't call this if you don't know what you do
     */
    @UseOnly(LogicalSide.SERVER)
    public void sync() {
        var syncData = createDataSlotUpdate(false);
        if (syncData != null) {
            CoreNetwork.sendToTracking(level.getChunkAt(getBlockPos()), new S2CDataSlotUpdate(getBlockPos(), syncData));
        }
    }

    @UseOnly(LogicalSide.CLIENT)
    public void clientHandleDataSync(CompoundTag syncData) {
        if (syncData.contains("Data", Tag.TAG_LIST)) {
            ListTag dataList = syncData.getList("Data", Tag.TAG_COMPOUND);

            for (Tag dataEntry : dataList) {
                if (dataEntry instanceof CompoundTag slotData) {
                    int slotIdx = slotData.getInt("Index");
                    dataSlots.get(slotIdx).fromNBT(slotData.get("Data"));
                }
            }

            for (Runnable task : afterDataSync) {
                task.run();
            }
        }
    }

    @UseOnly(LogicalSide.SERVER)
    public void serverHandleDataChange(CompoundTag data) {
        if (data.contains("Index", Tag.TAG_INT) && data.contains("Data")) {
            int slotIdx = data.getInt("Index");
            dataSlots.get(slotIdx).fromNBT(data.get("Data"));
        }
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

    @NotNull
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
