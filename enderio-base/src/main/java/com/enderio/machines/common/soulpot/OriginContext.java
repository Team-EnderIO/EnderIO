package com.enderio.machines.common.soulpot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record OriginContext(Level level, BlockPos pos, Map<Type<?>, Object> backingData) {

    public OriginContext(Level level, BlockPos pos) {
        this(level, pos, new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Type<T> type) {
        return (T) backingData.computeIfAbsent(type, t -> t.compute(this));
    }

    public abstract static class Type<V> {
        public static final Type<Holder<Biome>> BIOME = new Type<>() {
            @Override
            public Holder<Biome> compute(OriginContext ctx) {
                return ctx.level().getBiome(ctx.pos());
            }
        };
        public static final Type<List<Holder<Structure>>> STRUCTURE = new Type<>() {
            @Override
            public List<Holder<Structure>> compute(OriginContext ctx) {
                if (ctx.level() instanceof ServerLevel serverLevel) {
                    Registry<Structure> structures = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE);
                    return structures.holders().filter(holder -> serverLevel.structureManager().getStructureAt(ctx.pos(), holder.value()).isValid()).map(OriginContext::minimize).toList();
                }
                return List.of();
            }
        };

        public abstract V compute(OriginContext ctx);
    }

    private static Holder<Structure> minimize(Holder.Reference<Structure> holder) {
        return holder;
    }
}
