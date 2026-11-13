package com.enderio.enderio.api.armory.upgrades.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SteppingCarefullyCondition implements EffectCondition {

    public static final SteppingCarefullyCondition INSTANCE = new SteppingCarefullyCondition();

    public static final MapCodec<SteppingCarefullyCondition> CODEC = MapCodec.unit(INSTANCE);

    private SteppingCarefullyCondition() {
    }

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        if (entity == null) {
            return false;
        }

        return entity.isSteppingCarefully();
    }
}
