package com.enderio.machines.common.obelisk;

import com.enderio.EnderIO;
import com.enderio.core.common.util.ChunkBoundLookup;
import com.enderio.machines.common.blockentity.base.ObeliskBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Set;

public abstract class ObeliskManager<T extends ObeliskBlockEntity> implements IObeliskManagerCapability<T> {
    private final ChunkBoundLookup<T> lookup = new ChunkBoundLookup<>();

    @Override
    public void register(T obelisk) {
        lookup.addForBlockRadius(obelisk.getBlockPos(), obelisk.getRange(), obelisk);
    }

    @Override
    public void unregister(T obelisk) {
        lookup.remove(obelisk);
    }

    @Override
    public void update(T obelisk) {
        lookup.updateForBlockRadius(obelisk.getBlockPos(), obelisk.getRange(), obelisk);
    }

    @Override
    @Nullable
    public Set<T> getObelisksFor(BlockPos pos) {
        return lookup.getForChunk(new ChunkPos(pos));
    }
}
