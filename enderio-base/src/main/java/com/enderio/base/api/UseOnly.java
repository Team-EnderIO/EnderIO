package com.enderio.base.api;

import net.neoforged.fml.LogicalSide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks a method or field to only be used on a side without using the {@link net.neoforged.api.distmarker.OnlyIn} Annotation
 * This is only a documentation thing, to add a runtime check you can use {@link me.liliandev.ensure.ensures.EnsureSide}
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface UseOnly {
    LogicalSide value();
}
