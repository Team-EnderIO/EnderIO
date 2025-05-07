package com.enderio.base.api.filter;

import com.enderio.base.api.soul.Soul;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("8.0")
public interface SoulFilter {
    /**
     * Test whether the entity passes this filter.
     * @param entity Entity to test.
     * @return whether the entity passes.
     */
    boolean test(LivingEntity entity);

    /**
     * Test whether the soul passes this filter.
     * @param soul Soul to test.
     * @return whether the soul passes.
     */
    boolean test(Soul soul);

    /**
     * Test whether the entity type passes this filter.
     * @param entityType Entity type to test.
     * @return whether the entity type passes.
     */
    boolean test(EntityType<?> entityType);
}
