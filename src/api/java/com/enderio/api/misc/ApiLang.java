package com.enderio.api.misc;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

/**
 * These Components are pushed from mod construction of Enderio to this place, so it's available as soon as possible
 */
@ApiStatus.Internal
public class ApiLang {
    public static Component REDSTONE_ALWAYS_ACTIVE;
    public static Component REDSTONE_ACTIVE_WITH_SIGNAL;
    public static Component REDSTONE_ACTIVE_WITHOUT_SIGNAL;
    public static Component REDSTONE_NEVER_ACTIVE;

}
