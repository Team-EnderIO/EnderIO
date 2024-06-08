package com.enderio.conduits.common.redstone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class RedstoneTimerFilter implements RedstoneExtractFilter{
    public static final Codec<RedstoneTimerFilter> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(ExtraCodecs.POSITIVE_INT.fieldOf("maxTicks").forGetter(r -> r.maxTicks))
            .apply(instance, RedstoneTimerFilter::new)
    );
    public static final StreamCodec<ByteBuf, RedstoneTimerFilter> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(RedstoneTimerFilter::new, r -> r.maxTicks);

    private final int maxTicks;
    private int ticks;

    public RedstoneTimerFilter(int maxTicks) {
        this.maxTicks = maxTicks - (maxTicks % 2); //TODO Conduits tick every 2 ticks, so make this clear in the gui
    }

    public RedstoneTimerFilter() {
        this(20);
    }

    @Override
    public int getInputSignal(Level level, BlockPos pos, Direction direction) {
        ticks += 1;
        if (ticks >= maxTicks) {
            ticks = 0;
            return 15;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RedstoneTimerFilter that = (RedstoneTimerFilter) o;
        return maxTicks == that.maxTicks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxTicks);
    }
}
