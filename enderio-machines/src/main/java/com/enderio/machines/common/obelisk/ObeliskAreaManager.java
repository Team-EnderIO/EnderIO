package com.enderio.machines.common.obelisk;

import com.enderio.core.common.util.ChunkBoundLookup;
import com.enderio.machines.common.blocks.obelisks.ObeliskBlockEntity;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public abstract class ObeliskAreaManager<T extends ObeliskBlockEntity> {
    private final ChunkBoundLookup<T> lookup = new ChunkBoundLookup<>();

    public void register(T obelisk) {
        lookup.addForBlockRadius(obelisk.getBlockPos(), obelisk.getRange(), obelisk);
    }

    public void unregister(T obelisk) {
        lookup.remove(obelisk);
    }

    public void update(T obelisk) {
        lookup.updateForBlockRadius(obelisk.getBlockPos(), obelisk.getRange(), obelisk);
    }

    @Nullable
    public Set<T> getObelisksFor(BlockPos pos) {
        return lookup.getForChunk(new ChunkPos(pos));
    }
}
