package com.enderio.core.common.util;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

/**
 * Implements the concept of a spatial hashmap, bucketed by chunk.
 */
public class ChunkBoundLookup<T> {
    private final Long2ObjectMap<Set<T>> chunkData = new Long2ObjectOpenHashMap<>();
    private final Reference2ObjectMap<T, Set<ChunkPos>> valueKeys = new Reference2ObjectOpenHashMap<>();

    @Nullable
    public Set<T> getForChunk(ChunkPos pos) {
        return chunkData.get(pos.toLong());
    }

    // region Direct Chunk Manipulation

    /**
     * Adds a value to a chunk in the lookup.
     *
     * @param pos   The chunk to be added to.
     * @param value The value to add.
     */
    public void addToChunk(ChunkPos pos, T value) {
        chunkData.computeIfAbsent(pos.toLong(), k -> new HashSet<>()).add(value);
        valueKeys.computeIfAbsent(value, k -> new HashSet<>()).add(pos);
    }

    /**
     * Remove a value from a chunk in the lookup.
     *
     * @param pos   The chunk to be removed from.
     * @param value The value to remove.
     */
    public void removeFromChunk(ChunkPos pos, T value) {
        if (chunkData.containsKey(pos.toLong())) {
            chunkData.get(pos.toLong()).remove(value);
        }

        if (valueKeys.containsKey(value)) {
            valueKeys.get(value).remove(pos);
        }
    }

    // endregion

    // region Chunk and Block Ranges

    /**
     * Add the given value to the lookup with the given center point and square radius.
     *
     * @param centerPos   The center block position of the range.
     * @param blockRadius The radius (in blocks) of the range.
     * @param value       The value to add.
     */
    public void addForBlockRadius(BlockPos centerPos, int blockRadius, T value) {
        getBlockRadius(centerPos, blockRadius).forEach(chunkPos -> addToChunk(chunkPos, value));
    }

    /**
     * Update the center position and radius of an existing entry efficiently.
     * Instead of removing from all chunks then adding to all chunks, this only adds and removes the differences.
     * If the item does not yet exist, behaves the same as addForBlockRadius.
     *
     * @param centerPos   The center block position of the range.
     * @param blockRadius The radius (in blocks) of the range.
     * @param value       The value to update.
     */
    public void updateForBlockRadius(BlockPos centerPos, int blockRadius, T value) {
        Set<ChunkPos> currentChunks = valueKeys.get(value);
        if (currentChunks == null) {
            addForBlockRadius(centerPos, blockRadius, value);
            return;
        }
        // Copy or else can get a concurrent modification exception if the current
        // chunks are modified in the update
        currentChunks = new HashSet<>(currentChunks);
        Set<ChunkPos> newChunks = getBlockRadius(centerPos, blockRadius).collect(Collectors.toSet());
        bulkUpdate(value, currentChunks, newChunks);
    }

    /**
     * Add the given value to the lookup with the given center point and square radius.
     *
     * @param centerPos   The center chunk position of the range.
     * @param chunkRadius The radius (in chunks) of the range.
     * @param value       The value to add.
     */
    public void addForChunkRadius(ChunkPos centerPos, int chunkRadius, T value) {
        ChunkPos.rangeClosed(centerPos, chunkRadius).forEach(chunkPos -> addToChunk(chunkPos, value));
    }

    /**
     * Update the center position and radius of an existing entry efficiently.
     * Instead of removing from all chunks then adding to all chunks, this only adds and removes the differences.
     * If the item does not yet exist, behaves the same as addForBlockRadius.
     *
     * @param centerPos   The center chunk position of the range.
     * @param chunkRadius The radius (in chunks) of the range.
     * @param value       The value to update.
     */
    public void updateForChunkRadius(ChunkPos centerPos, int chunkRadius, T value) {
        Set<ChunkPos> currentChunks = valueKeys.get(value);
        if (currentChunks == null) {
            addForChunkRadius(centerPos, chunkRadius, value);
            return;
        }

        Set<ChunkPos> newChunks = ChunkPos.rangeClosed(centerPos, chunkRadius).collect(Collectors.toSet());
        bulkUpdate(value, currentChunks, newChunks);
    }

    private Stream<ChunkPos> getBlockRadius(BlockPos centerPos, int blockRadius) {
        BlockPos startBlockPos = new BlockPos(centerPos.getX() - blockRadius, 0, centerPos.getZ() - blockRadius);
        BlockPos endBlockPos = new BlockPos(centerPos.getX() + blockRadius, 0, centerPos.getZ() + blockRadius);

        ChunkPos startChunkPos = new ChunkPos(startBlockPos);
        ChunkPos endChunkPos = new ChunkPos(endBlockPos);

        return ChunkPos.rangeClosed(startChunkPos, endChunkPos);
    }

    private void bulkUpdate(T value, Set<ChunkPos> chunksBefore, Set<ChunkPos> chunksAfter) {
        Sets.SetView<ChunkPos> removedChunks = Sets.difference(chunksBefore, chunksAfter);
        Sets.SetView<ChunkPos> addedChunks = Sets.difference(chunksAfter, chunksBefore);

        removedChunks.forEach(chunkPos -> removeFromChunk(chunkPos, value));
        addedChunks.forEach(chunkPos -> addToChunk(chunkPos, value));
    }

    // endregion

    /**
     * Remove all instances of this value from the lookup.
     *
     * @param value The value to remove.
     */
    public void remove(T value) {
        Set<ChunkPos> chunks = valueKeys.get(value);
        for (ChunkPos chunkPos : chunks) {
            Set<T> dataAtChunk = chunkData.get(chunkPos.toLong());

            if (dataAtChunk != null) {
                dataAtChunk.remove(value);
                if (dataAtChunk.isEmpty()) {
                    chunkData.remove(chunkPos.toLong());
                }
            }
        }

        valueKeys.remove(value);
    }
}
