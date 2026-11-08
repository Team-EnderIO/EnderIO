package com.enderio.enderio.api.armory.upgrades.costs;

import com.enderio.enderio.api.armory.DarkSteelEnergyStorage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;

public record EnergyCost(int energy) implements EffectCost {

    public static final MapCodec<EnergyCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("energy").forGetter(EnergyCost::energy)
    ).apply(instance, EnergyCost::new));

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public boolean canAfford(Level level, Player player, ItemStack itemStack) {
        return consume(itemStack, true);
    }

    @Override
    public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        return consume(itemStack, false);
    }

    private boolean consume(ItemStack itemStack, boolean simulate) {
        var energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStorage == null) {
            return false;
        }

        if (energyStorage instanceof DarkSteelEnergyStorage darkSteelEnergyStorage) {
            return darkSteelEnergyStorage.consumeEnergy(energy, simulate);
        }

        return energyStorage.extractEnergy(energy, simulate) >= energy;
    }
}
