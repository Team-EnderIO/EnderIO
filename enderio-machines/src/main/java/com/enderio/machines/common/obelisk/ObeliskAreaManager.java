package com.enderio.machines.common.obelisk;

import com.enderio.core.common.util.ChunkBoundLookup;
import com.enderio.machines.common.blockentity.base.ObeliskBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class ObeliskAreaManager<T extends ObeliskBlockEntity> {
    private final ChunkBoundLookup<T> lookup = new ChunkBoundLookup<>();

    public void register(T obelisk) {
        lookup.addForRadius(obelisk.getBlockPos(), obelisk.getRange(), obelisk);
    }

    public void unregister(T obelisk) {
        lookup.remove(obelisk);
    }

    public void update(T obelisk) {
        // TODO: Do we need to do anything fancier in here? We have enough information to create a "diff" between before and after in the lookup.
        unregister(obelisk);
        register(obelisk);
    }

    @Nullable
    public Set<T> getObelisksFor(BlockPos pos) {
        return lookup.getForChunk(new ChunkPos(pos));
    }
}
