package com.enderio.enderio.api.filter;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

// TODO: Needs better name
@ApiStatus.Experimental
@ApiStatus.AvailableSince(value = "8.0.0")
public interface FilterMenuProvider {
    void openMenu(ServerPlayer player, @Nullable Runnable goBackRunnable);
}
