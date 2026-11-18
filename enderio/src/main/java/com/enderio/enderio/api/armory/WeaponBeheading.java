package com.enderio.enderio.api.armory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.ApiStatus;

/**
 * Any item with this component will perform 'beheading' when killing an entity.
 * @param headDropChance Chance between 0 and 1 of dropping a head on kill.
 */
@ApiStatus.Experimental
public record WeaponBeheading(float headDropChance) {
    public static final Codec<WeaponBeheading> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.floatRange(0, 1).fieldOf("head_drop_chance").forGetter(WeaponBeheading::headDropChance)
        ).apply(instance, WeaponBeheading::new)
    );
}
