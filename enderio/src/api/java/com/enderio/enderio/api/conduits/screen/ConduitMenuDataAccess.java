package com.enderio.enderio.api.conduits.screen;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public interface ConduitMenuDataAccess<T extends ConnectionConfig> {
    Conduit<?, T> conduit();

    BlockPos getBlockPos();

    T getConnectionConfig();

    void updateConnectionConfig(Function<T, T> configModifier);

    @Nullable
    CompoundTag getExtraGuiData();
}
