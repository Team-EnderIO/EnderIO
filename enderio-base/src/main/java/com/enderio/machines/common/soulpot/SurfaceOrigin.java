package com.enderio.machines.common.soulpot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.Heightmap;

public record SurfaceOrigin(Heightmap.Types heightmap) implements Origin<SurfaceOrigin> {
        public static final MapCodec<SurfaceOrigin> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(Heightmap.Types.CODEC.fieldOf("heightmap").forGetter(SurfaceOrigin::heightmap))
        .apply(inst, SurfaceOrigin::new));

    @Override
    public OriginType<SurfaceOrigin> type() {
        return OriginType.SURFACE;
    }

    @Override
    public boolean matches(OriginContext ctx) {
        return ctx.level().getHeight(heightmap, ctx.pos().getX(), ctx.pos().getZ()) <= ctx.pos().getY();
    }

}
