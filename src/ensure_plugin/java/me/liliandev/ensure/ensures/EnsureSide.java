package me.liliandev.ensure.ensures;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
/**
 * Adds a range check to primitive Numbers
 * min  is inclusive
 * max is exclusive
 */
public @interface EnsureSide {

    Side value();

    enum Side {
        CLIENT,
        SERVER;

        public boolean isClient() {
            return this == CLIENT;
        }

        public boolean isServer() {
            return this == SERVER;
        }

        public Side getOpposite() {
            return this.isClient() ? SERVER : CLIENT;
        }
    }
}
