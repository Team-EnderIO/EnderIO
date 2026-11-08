package com.enderio.enderio.api.armory.upgrades.costs;

import com.enderio.enderio.api.EnderIORegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public interface EffectCost {
    Codec<EffectCost> CODEC = EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT_COST.byNameCodec()
        .dispatchStable(EffectCost::codec, Function.identity());

    MapCodec<? extends EffectCost> codec();

    boolean canAfford(Level level, Player player, ItemStack itemStack);

    // TODO: should this return a bool, or just throw if canAfford is false?
    boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack);
}
