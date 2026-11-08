package com.enderio.enderio.api.armory.upgrades.costs;

import com.enderio.enderio.api.armory.DarkSteelEnergyStorage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

public record EnergyCost(IntProvider energy) implements EffectCost {

    public static final MapCodec<EnergyCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IntProvider.CODEC.fieldOf("energy").forGetter(EnergyCost::energy)
    ).apply(instance, EnergyCost::new));

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public EffectCostInstance createInstance(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        return new Instance(energy.sample(level.getRandom()));
    }

    private record Instance(int energy) implements EffectCostInstance {
        @Override
        public boolean canAfford(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            var energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energyStorage == null) {
                return false;
            }

            if (energyStorage instanceof DarkSteelEnergyStorage darkSteelEnergyStorage) {
                return darkSteelEnergyStorage.consumeEnergy(energy, true);
            }

            return energyStorage.extractEnergy(energy, true) >= energy;
        }

        @Override
        public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            var energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energyStorage == null) {
                return false;
            }

            if (energyStorage instanceof DarkSteelEnergyStorage darkSteelEnergyStorage) {
                return darkSteelEnergyStorage.consumeEnergy(energy, false);
            }

            return energyStorage.extractEnergy(energy, false) >= energy;
        }
    }
}
