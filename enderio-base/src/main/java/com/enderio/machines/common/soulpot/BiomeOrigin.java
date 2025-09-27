package com.enderio.machines.common.soulpot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.world.level.biome.Biome;


public record BiomeOrigin(HolderSet<Biome> biomes) implements Origin<BiomeOrigin> {

    public BiomeOrigin(Holder<Biome> biome) {
        this(HolderSet.direct(biome));
    }
    public static final MapCodec<BiomeOrigin> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(HolderSetCodec.create(Registries.BIOME, Biome.CODEC, true).fieldOf("biomes").forGetter(BiomeOrigin::biomes)).apply(inst, BiomeOrigin::new));

    @Override
    public OriginType<BiomeOrigin> type() {
        return OriginType.BIOME;
    }

    @Override
    public boolean matches(OriginContext context) {
        return biomes.contains(context.getData(OriginContext.Type.BIOME));
    }
}
