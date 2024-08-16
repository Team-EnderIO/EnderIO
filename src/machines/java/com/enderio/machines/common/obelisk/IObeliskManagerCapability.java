package com.enderio.machines.common.obelisk;

import com.enderio.machines.common.blockentity.base.ObeliskBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@AutoRegisterCapability
public interface IObeliskManagerCapability<T extends ObeliskBlockEntity> {
    void register(T obelisk);

    void unregister(T obelisk);

    void update(T obelisk);

    @Nullable Set<T> getObelisksFor(BlockPos pos);
}
