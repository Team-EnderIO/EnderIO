package com.enderio.enderio.api.armory.upgrades.costs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface EffectCostInstance {
    boolean canAfford(ServerLevel level, ServerPlayer player, ItemStack itemStack);

    // TODO: should this return a bool, or just throw if canAfford is false?
    boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack);
}
