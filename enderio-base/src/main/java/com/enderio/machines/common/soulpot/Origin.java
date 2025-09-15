package com.enderio.machines.common.soulpot;

import com.mojang.serialization.Codec;

public interface Origin<T extends Origin<T>> {

    Codec<Origin<?>> CODEC = OriginType.CODEC.dispatch(Origin::type, OriginType::codec);

    OriginType<T> type();

    boolean matches(OriginContext ctx);
}
