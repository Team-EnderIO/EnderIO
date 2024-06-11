package com.enderio.base.common.blockentity;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

/**
 * An interface that block entities may implement in order to support item right-click behaviour. Used for auto-equipping capacitors on supported machines.
 */
public interface MachineInstallable {

    InteractionResult tryItemInstall(ItemStack stack, UseOnContext context);
}

