package com.enderio.machines.common.io.fluid;

import com.enderio.machines.common.attachment.FluidTankUser;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;

/**
 * An interface that block entities may implement in order to interact with items support IFluidHandlerItem
 * Adapted from Mekanism's FluidUtils.
 */
public interface FluidItemInteractive {

    // Requires direct tank access which is undesirable. MachineFluidHandler would be better to for multi-tank block.
    default boolean handleFluidItemInteraction(Player player, InteractionHand hand, ItemStack itemStack, FluidTankUser machine, TankAccess tankAccess) {
        ItemStack copyStack = itemStack.copyWithCount(1);
        Optional<IFluidHandlerItem> fluidHandlerItem = FluidUtil.getFluidHandler(copyStack);
        if (fluidHandlerItem.isPresent()) {
            IFluidHandlerItem handler = fluidHandlerItem.get();
            FluidStack fluidInItem;
            if (tankAccess.isEmpty(machine)) {
                //If we don't have a fluid stored try draining in general
                fluidInItem = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
            } else {
                //Otherwise, try draining the same type of fluid we have stored
                // We do this to better support multiple tanks in case the fluid we have stored we could pull out of a block's
                // second tank but just asking to drain a specific amount
                fluidInItem = handler.drain(new FluidStack(tankAccess.getFluid(machine).getFluid(), Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
            }
            if (fluidInItem.isEmpty()) {
                if (!tankAccess.isEmpty(machine) && tankAccess.canExtract(machine)) {
                    int filled = handler.fill(tankAccess.getFluid(machine),
                        player.isCreative() ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                    ItemStack container = handler.getContainer();
                    if (filled > 0) {
                        if (itemStack.getCount() == 1) {
                            player.setItemInHand(hand, container);
                        } else if (itemStack.getCount() > 1 && player.getInventory().add(container)) {
                            itemStack.shrink(1);
                        } else {
                            player.drop(container, false, true);
                            itemStack.shrink(1);
                        }
                        if (player.isCreative()) {
                            ItemStack copy = itemStack.copyWithCount(1);
                            Optional<IFluidHandlerItem> newHandler = FluidUtil.getFluidHandler(copy);
                            newHandler.get().fill(tankAccess.getFluid(machine), IFluidHandler.FluidAction.EXECUTE);
                            container = newHandler.get().getContainer();
                            if (!player.getInventory().add(container)) {
                                player.drop(container, false, true);
                            }
                        }
                        tankAccess.drain(machine, filled, IFluidHandler.FluidAction.EXECUTE);
                        return true;
                    }
                }
            } else if (tankAccess.canInsert(machine)) {
                int filledAmount = tankAccess.fill(machine, fluidInItem, IFluidHandler.FluidAction.SIMULATE);
                if (filledAmount > 0) {
                    boolean filled = false;
                    FluidStack fluidToFill = handler.drain(new FluidStack(fluidInItem.getFluid(), filledAmount),
                        player.isCreative() ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                    if (!fluidToFill.isEmpty()) {
                        ItemStack container = handler.getContainer();
                        if (player.isCreative()) {
                            filled = true;
                        } else if (!container.isEmpty()) {
                            if (itemStack.getCount() == 1) {
                                player.setItemInHand(hand, container);
                                filled = true;
                            } else if (player.getInventory().add(container)) {
                                itemStack.shrink(1);
                                filled = true;
                            }
                        } else {
                            itemStack.shrink(1);
                            if (itemStack.isEmpty()) {
                                player.setItemInHand(hand, ItemStack.EMPTY);
                            }
                            filled = true;
                        }
                        if (filled) {
                            tankAccess.fill(machine, fluidToFill, IFluidHandler.FluidAction.EXECUTE);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
