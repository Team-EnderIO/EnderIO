package com.enderio.enderio.api.armory.upgrades.costs;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FreeCost implements EffectCost {

    public static final FreeCost INSTANCE = new FreeCost();

    public static final MapCodec<FreeCost> CODEC = MapCodec.unit(INSTANCE);

    private FreeCost() {
    }

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public boolean canAfford(Level level, Player player, ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        return true;
    }
}
