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
 * Adds a null check to non primitive parameters
 */
public @interface EnsureNotNull {
}
