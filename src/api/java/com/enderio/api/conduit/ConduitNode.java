package com.enderio.api.conduit;

import com.enderio.api.ConduitDataAccessor;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ConduitNode extends ConduitDataAccessor {
    Optional<IOState> getIOState(Direction direction);
    BlockPos getPos();

    @Nullable
    ConduitUpgrade getUpgrade(Direction direction);

    @Nullable
    ResourceFilter getExtractFilter(Direction direction);

    @Nullable
    ResourceFilter getInsertFilter(Direction direction);

    @Nullable
    ConduitNetwork getParentGraph();

    record IOState(Optional<ColorControl> insert, Optional<ColorControl> extract, RedstoneControl control, ColorControl redstoneChannel) {

        public boolean isInsert() {
            return insert().isPresent();
        }

        public boolean isExtract() {
            return extract().isPresent();
        }

        public static IOState of(@Nullable ColorControl in, @Nullable ColorControl extract, RedstoneControl control, ColorControl redstoneChannel) {
            return new IOState(Optional.ofNullable(in), Optional.ofNullable(extract), control, redstoneChannel);
        }
    }
}
