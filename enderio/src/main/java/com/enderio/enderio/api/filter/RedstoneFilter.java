package com.enderio.enderio.api.filter;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface RedstoneFilter {

    /**
     * Returns whether this filter has been configured by the player.
     * Used to determine whether to show configuration hints in tooltips.
     *
     * @return true if the filter has been configured, false if it's in its default state
     */
    boolean isConfigured();
}
