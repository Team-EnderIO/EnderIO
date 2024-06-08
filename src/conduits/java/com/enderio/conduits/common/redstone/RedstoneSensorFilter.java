package com.enderio.conduits.common.redstone;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;

public class RedstoneSensorFilter implements RedstoneExtractFilter{

    public static final RedstoneSensorFilter INSTANCE = new RedstoneSensorFilter();

    public static final Codec<RedstoneSensorFilter> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, RedstoneSensorFilter> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public int getInputSignal(Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof ComparatorBlockEntity comp ? comp.getOutputSignal() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        return INSTANCE == obj;
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
