package com.enderio.enderio.client.content.armory;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Tooltip element showing the contents of an item's capacitor socket. May be empty.
 */
public record SocketedCapacitorTooltip(ItemStack capacitor) implements TooltipComponent {
}
