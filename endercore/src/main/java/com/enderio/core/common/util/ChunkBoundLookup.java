package com.enderio.core.common.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

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

    public void addForChunk(ChunkPos pos, T value) {
        chunkData.computeIfAbsent(pos.toLong(), k -> new HashSet<>()).add(value);
        valueKeys.computeIfAbsent(value, k -> new HashSet<>()).add(pos);
    }

    public void addForRadius(BlockPos pos, int radius, T value) {
        BlockPos startBlockPos = new BlockPos(pos.getX() - radius, 0, pos.getZ() - radius);
        BlockPos endBlockPos = new BlockPos(pos.getX() + radius, 0, pos.getZ() + radius);

        ChunkPos startChunkPos = new ChunkPos(startBlockPos);
        ChunkPos endChunkPos = new ChunkPos(endBlockPos);
        
        ChunkPos.rangeClosed(startChunkPos, endChunkPos).forEach(chunkPos -> addForChunk(chunkPos, value));
    }

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
