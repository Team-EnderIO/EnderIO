package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public class RedstoneORFilter implements RedstoneInsertFilter {
    public static final Codec<RedstoneORFilter> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(ColorControl.CODEC.fieldOf("channel1").forGetter(r -> r.channel1),
                ColorControl.CODEC.fieldOf("channel2").forGetter(r -> r.channel2))
            .apply(instance, RedstoneORFilter::new)
    );
    public static final StreamCodec<ByteBuf, RedstoneORFilter> STREAM_CODEC = StreamCodec.composite(
        ColorControl.STREAM_CODEC,
        r -> r.channel1,
        ColorControl.STREAM_CODEC,
        r -> r.channel2,
        RedstoneORFilter::new
    );

    private final ColorControl channel1;
    private final ColorControl channel2;

    public RedstoneORFilter(ColorControl channel1, ColorControl channel2) {
        this.channel1 = channel1;
        this.channel2 = channel2;
    }

    public RedstoneORFilter() {
        this(ColorControl.GREEN, ColorControl.BROWN);
    }

    @Override
    public int getOutputSignal(RedstoneExtendedData data, ColorControl control) {
        boolean b = data.isActive(channel1) || data.isActive(channel2);
        return b ? 15 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RedstoneORFilter that = (RedstoneORFilter) o;
        return channel1 == that.channel1 && channel2 == that.channel2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel1, channel2);
    }
}
