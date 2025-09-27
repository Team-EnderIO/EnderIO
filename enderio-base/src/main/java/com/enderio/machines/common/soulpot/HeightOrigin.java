package com.enderio.machines.common.soulpot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;


public record HeightOrigin(boolean isMin, int height) implements Origin<HeightOrigin> {

    public static final MapCodec<HeightOrigin> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.BOOL.fieldOf("isMin").forGetter(HeightOrigin::isMin),
            Codec.INT.fieldOf("height").forGetter(HeightOrigin::height))
            .apply(inst, HeightOrigin::new));
    @Override
    public OriginType<HeightOrigin> type() {
        return OriginType.HEIGHT;
    }

    @Override
    public boolean matches(OriginContext context) {
        if (isMin) {
            return context.pos().getY() >= height;
        }
        return context.pos().getY() <= height;
    }
}
