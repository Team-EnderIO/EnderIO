package com.enderio.api.capability;

/**
 * Defines something that can be toggled, like an item.
 */
public interface IToggled {
    /**
     * Get whether the toggleable is enabled.
     */
    boolean isEnabled();

    /**
     * Toggle whether this is enabled.
     */
    void toggle();

    /**
     * Set whether this is enabled.
     */
    void setEnabled(boolean isEnabled);
}
