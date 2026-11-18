package com.enderio.enderio.api.armory.upgrades.costs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record EffectDurabilityCost(int durability) implements EffectCost {

    public static final MapCodec<EffectDurabilityCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("durability").forGetter(EffectDurabilityCost::durability)
    ).apply(instance, EffectDurabilityCost::new));

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public boolean canAfford(Level level, Player player, ItemStack itemStack) {
        return itemStack.isDamageableItem() && itemStack.getDamageValue() + durability <= itemStack.getMaxDamage();
    }

    @Override
    public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        if (!canAfford(level, player, itemStack)) {
            return false;
        }

        itemStack.hurtAndBreak(durability, level, player, item -> {});
        return true;
    }
}
