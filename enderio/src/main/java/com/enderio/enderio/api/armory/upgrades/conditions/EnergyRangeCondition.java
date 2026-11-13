package com.enderio.enderio.api.armory.upgrades.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public record EnergyRangeCondition(int min, int max) implements EffectCondition {

    public static final MapCodec<EnergyRangeCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("min").forGetter(EnergyRangeCondition::min),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max", Integer.MAX_VALUE).forGetter(EnergyRangeCondition::max)
        ).apply(inst, EnergyRangeCondition::new));

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        var energyHandler = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyHandler == null) {
            return false;
        }

        int energy = energyHandler.getEnergyStored();
        return energy >= min && energy <= max;
    }
}
