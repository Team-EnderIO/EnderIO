package com.enderio.base.api.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public class DumbStreamCodec {
    public static <T> StreamCodec<ByteBuf, T> of(Supplier<T> factory) {
        return StreamCodec.of(
            (buf, obj) -> {},
            buf -> factory.get());
    }
}
