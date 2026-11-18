package com.enderio.enderio.api.armory.upgrades.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Active when the time of day ({@link Level#getDayTime()} within the current day, 0 to 23999) is between {@link #min()} and {@link #max()}, inclusive.
 * If {@code min} is greater than {@code max} the range wraps over midnight, e.g. {@code min = 13000, max = 1000} covers the night.
 */
public record DayTimeRangeCondition(int min, int max, boolean noLevelResult) implements EffectCondition {

    public static final int DAY_LENGTH = 24_000;

    public static final MapCodec<DayTimeRangeCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            Codec.intRange(0, DAY_LENGTH - 1).fieldOf("min").forGetter(DayTimeRangeCondition::min),
            Codec.intRange(0, DAY_LENGTH - 1).fieldOf("max").forGetter(DayTimeRangeCondition::max),
            Codec.BOOL.optionalFieldOf("no_level_result", false).forGetter(DayTimeRangeCondition::noLevelResult)
        ).apply(inst, DayTimeRangeCondition::new));

    public DayTimeRangeCondition(int min, int max) {
        this(min, max, false);
    }

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        if (level == null) {
            return noLevelResult;
        }

        long dayTime = Math.floorMod(level.getDayTime(), DAY_LENGTH);
        if (min <= max) {
            return dayTime >= min && dayTime <= max;
        }

        return dayTime >= min || dayTime <= max;
    }
}
