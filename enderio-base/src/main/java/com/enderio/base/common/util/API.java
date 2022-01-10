package com.enderio.base.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks something as part of an api and should be binary compatible between patch versions.
 * Changes to the object marked with this should change only withing minor or major version,
 * but better only at major versions.
 * This is intended for IMC and ModInteractions
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface API {
}
