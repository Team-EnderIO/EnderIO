package com.enderio.enderio.api.armory.upgrades.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Active when the entity has direct access to the sky above its head, in a dimension with skylight.
 */
public final class SkyAccessCondition implements EffectCondition {

    public static final SkyAccessCondition INSTANCE = new SkyAccessCondition();

    public static final MapCodec<SkyAccessCondition> CODEC = MapCodec.unit(INSTANCE);

    private SkyAccessCondition() {
    }

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        if (level == null || entity == null || !level.dimensionType().hasSkyLight()) {
            return false;
        }

        return level.canSeeSky(BlockPos.containing(entity.getEyePosition()));
    }
}
