package com.enderio.enderio.api.armory.upgrades.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class NeverActiveCondition implements EffectCondition {

    public static final NeverActiveCondition INSTANCE = new NeverActiveCondition();

    public static final MapCodec<NeverActiveCondition> CODEC = MapCodec.unit(INSTANCE);

    private NeverActiveCondition() {
    }

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        return false;
    }
}
