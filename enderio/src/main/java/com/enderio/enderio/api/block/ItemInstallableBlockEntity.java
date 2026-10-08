package com.enderio.enderio.api.block;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * An interface that block entities may implement in order to support item right-click behaviour. Used for auto-equipping capacitors on supported machines.
 */
@ApiStatus.AvailableSince("10.0.0")
public interface ItemInstallableBlockEntity {

    InteractionResult tryItemInstall(ItemStack stack, UseOnContext context);
}

