package com.enderio.enderio.api.armory.upgrades.costs;

import com.enderio.enderio.api.EnderIORegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface EffectCost {
    Codec<EffectCost> CODEC = EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT_COST.byNameCodec()
        .dispatchStable(EffectCost::codec, Function.identity());

    MapCodec<? extends EffectCost> codec();

    EffectCostInstance createInstance(ServerLevel level, ServerPlayer player, ItemStack itemStack);
}
