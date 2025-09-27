package com.enderio.machines.common.soulpot;

import com.enderio.base.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record OriginType<T extends Origin<T>>(MapCodec<T> codec) {


    public static Codec<OriginType<?>> CODEC = EnderIORegistries.ORIGIN_TYPES.byNameCodec();

    public static OriginType<BiomeOrigin> BIOME = new OriginType<>(BiomeOrigin.CODEC);
    public static OriginType<StructureOrigin> STRUCTURE = new OriginType<>(StructureOrigin.CODEC);
    public static OriginType<LogicOrigin> LOGIC = new OriginType<>(LogicOrigin.CODEC);
    public static OriginType<LightOrigin> LIGHT = new OriginType<>(LightOrigin.CODEC);
    public static OriginType<BlockOrigin> BLOCK = new OriginType<>(BlockOrigin.CODEC);
    public static OriginType<SurfaceOrigin> SURFACE = new OriginType<>(SurfaceOrigin.CODEC);
    public static OriginType<HeightOrigin> HEIGHT = new OriginType<>(HeightOrigin.CODEC);
    public static OriginType<NotOrigin> NOT = new OriginType<>(NotOrigin.CODEC);


}
