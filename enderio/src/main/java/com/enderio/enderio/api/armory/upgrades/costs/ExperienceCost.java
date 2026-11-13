package com.enderio.enderio.api.armory.upgrades.costs;

import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ExperienceCost(int experience) implements EffectCost {

    public static final MapCodec<ExperienceCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("experience").forGetter(ExperienceCost::experience)
    ).apply(instance, ExperienceCost::new));

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public boolean canAfford(Level level, Player player, ItemStack itemStack) {
        return ExperienceUtil.getPlayerTotalXp(player) >= experience;
    }

    @Override
    public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        if (!canAfford(level, player, itemStack)) {
            return false;
        }

        player.giveExperiencePoints(-experience);
        return true;
    }
}
