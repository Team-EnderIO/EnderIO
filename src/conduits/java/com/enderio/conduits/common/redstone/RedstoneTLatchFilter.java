package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class RedstoneTLatchFilter implements RedstoneInsertFilter {
    public static final RedstoneTLatchFilter INSTANCE = new RedstoneTLatchFilter();

    public static final Codec<RedstoneTLatchFilter> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, RedstoneTLatchFilter> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private boolean lastActive = false;

    @Override
    public int getOutputSignal(RedstoneExtendedData data, ColorControl control) {
        if (lastActive !=  data.isActive(control)) {
            lastActive = !lastActive;
        }
        return lastActive ? 15 : 0;
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
