package com.enderio.core.common.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidStackWithTank(int tank, FluidStack stack) {
    public static final Codec<FluidStackWithTank> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.UNSIGNED_BYTE.fieldOf("Tank").orElse(0).forGetter(FluidStackWithTank::tank),
        FluidStack.MAP_CODEC.forGetter(FluidStackWithTank::stack)
    ).apply(instance, FluidStackWithTank::new));

    public boolean isValidInHandler(int numTanks) {
        return tank >= 0 && tank < numTanks;
    }
}
