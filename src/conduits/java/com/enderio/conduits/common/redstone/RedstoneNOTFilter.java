package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class RedstoneNOTFilter implements RedstoneInsertFilter {
    public static final RedstoneNOTFilter INSTANCE = new RedstoneNOTFilter();

    public static final Codec<RedstoneNOTFilter> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, RedstoneNOTFilter> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public int getOutputSignal(RedstoneExtendedData data, ColorControl control) {
        return data.isActive(control) ? 0 : 15;
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
