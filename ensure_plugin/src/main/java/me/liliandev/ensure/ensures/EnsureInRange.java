package me.liliandev.ensure.ensures;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER})
/**
 * Adds a range check to primitive Numbers
 * min  is inclusive
 * max is exclusive
 */
public @interface EnsureInRange {

    long min();
    long max();
}
