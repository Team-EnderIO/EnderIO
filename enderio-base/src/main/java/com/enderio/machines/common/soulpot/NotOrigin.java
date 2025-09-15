package com.enderio.machines.common.soulpot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NotOrigin(Origin<?> origin) implements Origin<NotOrigin> {
    public static final MapCodec<NotOrigin> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(Origin.CODEC.fieldOf("potPos").forGetter(NotOrigin::origin)).apply(inst, NotOrigin::new));

    @Override
    public OriginType<NotOrigin> type() {
        return OriginType.NOT;
    }

    @Override
    public boolean matches(OriginContext ctx) {
        return !origin.matches(ctx);
    }

}
