package com.enderio.enderio.api.armory.upgrades.costs;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record CompoundEffectCost(List<EffectCost> costs) implements EffectCost {

    public static final MapCodec<CompoundEffectCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(
            EffectCost.CODEC.sizeLimitedListOf(32).fieldOf("costs").forGetter(CompoundEffectCost::costs)
        ).apply(instance, CompoundEffectCost::new));

    public CompoundEffectCost(EffectCost... effectCosts) {
        this(List.of(effectCosts));
    }

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public EffectCostInstance createInstance(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        return new Instance(costs.stream().map(i -> i.createInstance(level, player, itemStack)).toList());
    }

    private record Instance(List<EffectCostInstance> instances) implements EffectCostInstance {
        @Override
        public boolean canAfford(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            for (EffectCostInstance instance : instances) {
                if (!instance.canAfford(level, player, itemStack)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            if (!canAfford(level, player, itemStack)) {
                return false;
            }

            // TODO: Not great that we ignore return value, but might go away soon.
            for (EffectCostInstance instance : instances) {
                instance.take(level, player, itemStack);
            }

            return true;
        }
    }
}
