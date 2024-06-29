package com.enderio.api.filter;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Predicate;

public interface EntityFilter extends ResourceFilter, Predicate<Entity> {

    boolean test(EntityType<?> entity);
}
