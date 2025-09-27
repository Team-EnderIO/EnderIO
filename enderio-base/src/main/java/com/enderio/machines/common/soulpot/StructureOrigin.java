package com.enderio.machines.common.soulpot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;

public record StructureOrigin(HolderSet<Structure> structures) implements Origin<StructureOrigin> {

    public StructureOrigin(Holder<Structure>... structures) {
        this(HolderSet.direct(structures));
    }
    public static final MapCodec<StructureOrigin> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(RegistryCodecs.homogeneousList(Registries.STRUCTURE, true).fieldOf("structures").forGetter(StructureOrigin::structures)).apply(inst, StructureOrigin::new));

    @Override
    public OriginType<StructureOrigin> type() {
        return OriginType.STRUCTURE;
    }

    @Override
    public boolean matches(OriginContext context) {
        return context.getData(OriginContext.Type.STRUCTURE).stream().anyMatch(structures::contains);
    }
}
