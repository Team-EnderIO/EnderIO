package com.enderio.api;

import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks a method or field to only be used on a side without using the {@link net.neoforged.api.distmarker.OnlyIn} Annotation
 * Todo: Maybe do some bytecode magic to add a check before each invocation of this method to {@link EffectiveSide#get()} and log a warning and print a stacktrace
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface UseOnly {
    LogicalSide value();
}
