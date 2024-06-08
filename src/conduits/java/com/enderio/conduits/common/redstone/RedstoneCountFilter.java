package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;

public class RedstoneCountFilter implements RedstoneInsertFilter {
    public static final Codec<RedstoneCountFilter> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(ColorControl.CODEC.fieldOf("channel1").forGetter(r -> r.channel1),
            ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter(r -> r.maxCount))
            .apply(instance, RedstoneCountFilter::new)
    );
    public static final StreamCodec<ByteBuf, RedstoneCountFilter> STREAM_CODEC = StreamCodec.composite(
      ColorControl.STREAM_CODEC,
      r -> r.channel1,
      ByteBufCodecs.VAR_INT,
      r -> r.maxCount,
      RedstoneCountFilter::new
    );

    private final ColorControl channel1;
    private final int maxCount;
    private int count;
    private boolean lastActive = false;

    public RedstoneCountFilter(ColorControl channel1, int maxCount) {
        this.channel1 = channel1;
        this.maxCount = maxCount;
    }

    public RedstoneCountFilter() {
        this(ColorControl.GREEN, 8);
    }

    @Override
    public int getOutputSignal(RedstoneExtendedData data, ColorControl control) {
        if (lastActive !=  data.isActive(channel1)) {
            lastActive = !lastActive;
            if (data.isActive(channel1)) {
                count = (count + 1) % maxCount;
            }
        }
        return count == maxCount ? 15 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RedstoneCountFilter that = (RedstoneCountFilter) o;
        return maxCount == that.maxCount && channel1 == that.channel1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel1, maxCount);
    }
}
