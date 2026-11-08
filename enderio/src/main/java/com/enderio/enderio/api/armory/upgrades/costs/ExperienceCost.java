package com.enderio.enderio.api.armory.upgrades.costs;

import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;

public record ExperienceCost(IntProvider experience) implements EffectCost {

    public static final MapCodec<ExperienceCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IntProvider.CODEC.fieldOf("experience").forGetter(ExperienceCost::experience)
    ).apply(instance, ExperienceCost::new));

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public EffectCostInstance createInstance(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        return new Instance(experience.sample(level.getRandom()));
    }

    private record Instance(int experienceToTake) implements EffectCostInstance {
        @Override
        public boolean canAfford(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            return ExperienceUtil.getPlayerTotalXp(player) >= experienceToTake;
        }

        @Override
        public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            if (!canAfford(level, player, itemStack)) {
                return false;
            }

            player.giveExperiencePoints(-experienceToTake);
            return true;
        }
    }
}
