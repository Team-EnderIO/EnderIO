package com.enderio.base.api.filter;

import com.enderio.base.api.attachment.StoredEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public interface EntityFilter {
    boolean test(LivingEntity entity);
    boolean test(StoredEntityData storedEntity);
    boolean test(EntityType<?> entity);
}
