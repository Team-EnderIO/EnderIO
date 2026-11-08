package com.enderio.enderio.api.armory.upgrades.costs;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FreeCost implements EffectCost {

    public static final FreeCost INSTANCE = new FreeCost();

    public static final MapCodec<FreeCost> CODEC = MapCodec.unit(INSTANCE);

    private static final EffectCostInstance COST_INSTANCE = new Instance();

    private FreeCost() {
    }

    @Override
    public MapCodec<? extends EffectCost> codec() {
        return CODEC;
    }

    @Override
    public EffectCostInstance createInstance(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
        return COST_INSTANCE;
    }

    private record Instance() implements EffectCostInstance {
        @Override
        public boolean canAfford(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            return true;
        }

        @Override
        public boolean take(ServerLevel level, ServerPlayer player, ItemStack itemStack) {
            return true;
        }
    }
}
