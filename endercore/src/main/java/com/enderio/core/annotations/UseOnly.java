package com.enderio.core.annotations;

import net.neoforged.fml.LogicalSide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks a method or field to only be used on a side without using the {@link net.neoforged.api.distmarker.OnlyIn} Annotation
 * This is merely a documentation annotation, no runtime checks are done.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface UseOnly {
    LogicalSide value();
}
