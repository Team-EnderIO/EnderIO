package com.enderio.enderio.client.foundation.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * Tooltip element rendering an item's stored energy as a bar. Swapped in for the text line added by
 * {@link com.enderio.enderio.foundation.energy.EnergyTooltip}.
 *
 * @param showAmount label the bar with the stored amount rather than a percentage.
 */
public record EnergyBarTooltip(long stored, long max, boolean showAmount) implements TooltipComponent {
}
