package com.enderio.enderio.api.armory.upgrades.costs;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;

public record EffectDurabilityCost(IntProvider durability) implements EffectCost {

    public static final MapCodec<EffectDurabilityCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IntProvider.CODEC.fieldOf("durability").forGetter(EffectDurabilityCost::durability)
    ).apply(instance, EffectDurabilityCost::new));

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public EffectCostInstance createInstance(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        return new Instance(durability.sample(level.getRandom()));
    }

    private record Instance(int durabilityToTake) implements EffectCostInstance {
        @Override
        public boolean canAfford(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            return itemStack.isDamageableItem() && itemStack.getDamageValue() + durabilityToTake <= itemStack.getMaxDamage();
        }

        @Override
        public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            if (!canAfford(level, player, itemStack)) {
                return false;
            }

            itemStack.hurtAndBreak(durabilityToTake, level, player, item -> {});
            return true;
        }
    }
}
