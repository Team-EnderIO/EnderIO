package com.enderio.core.common.blockentity;

import com.mojang.logging.LogUtils;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Base block entity class for EnderIO.
 * Handles data slot syncing and capability providers.
 */
public class EnderBlockEntity extends BlockEntity {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String DATA = "Data";
    public static final String INDEX = "Index";

    private final Map<BlockCapability<?, ?>, EnumMap<Direction, BlockCapabilityCache<?, ?>>> selfCapabilities = new HashMap<>();
    private final Map<BlockCapability<?, ?>, EnumMap<Direction, BlockCapabilityCache<?, ?>>> neighbourCapabilities = new HashMap<>();

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
        blockEntity.endTick();
    }

    /**
     * Perform server-side ticking
     */
    @EnsureSide(EnsureSide.Side.SERVER)
    public void serverTick() {
    }

    /**
     * Perform client side ticking.
     */
    @EnsureSide(EnsureSide.Side.CLIENT)
    public void clientTick() {
    }

    /**
     * Perform client and server side ticking.
     */
    public void endTick() {
    }

    // endregion

    // region Sync

    /**
     * This is the initial packet sent to a client loading the block (or when it is placed).
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            var tag = TagValueOutput.createWithContext(problemReporter, registries);
            saveAdditionalSynced(tag);
            return tag.buildResult();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        saveAdditionalSynced(output);
    }

    /**
     * Override this to write data which should be synced over the network.
     * Must be opted-in by overriding {@link BlockEntity#getUpdatePacket}.
     */
    protected void saveAdditionalSynced(ValueOutput output) {
    }

    // endregion

    // region Neighboring Capabilities

    // TODO: NEO-PORT: We might want handling for Void contexts.
    // However cannot have two methods with same method name and different context
    // type params :(

    @Nullable
    protected <T> T getSelfCapability(BlockCapability<T, Direction> capability, Direction side) {
        if (level == null) {
            return null;
        }

        if (!selfCapabilities.containsKey(capability)) {
            // We've not seen this capability before, time to register it!
            selfCapabilities.put(capability, new EnumMap<>(Direction.class));

            for (Direction direction : Direction.values()) {
                populateSelfCachesFor(direction, capability);
            }
        }

        if (!selfCapabilities.get(capability).containsKey(side)) {
            return null;
        }

        // noinspection unchecked
        return (T) selfCapabilities.get(capability).get(side).getCapability();
    }

    private void populateSelfCachesFor(Direction direction, BlockCapability<?, Direction> capability) {
        if (level instanceof ServerLevel serverLevel) {
            selfCapabilities.get(capability)
                    .put(direction, BlockCapabilityCache.create(capability, serverLevel, getBlockPos(), direction));
        }
    }

    // TODO: Ensure SERVER usage sometime.
    @Nullable
    protected <T> T getNeighbouringCapability(BlockCapability<T, Direction> capability, Direction side) {
        if (level == null || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        var sidedCaches = neighbourCapabilities.computeIfAbsent(capability, c -> new EnumMap<>(Direction.class));
        var cache = sidedCaches.computeIfAbsent(side,
                s -> BlockCapabilityCache.create(capability, serverLevel, getBlockPos().relative(s), s.getOpposite()));

        // noinspection unchecked
        return (T) cache.getCapability();
    }

    // endregion
}
