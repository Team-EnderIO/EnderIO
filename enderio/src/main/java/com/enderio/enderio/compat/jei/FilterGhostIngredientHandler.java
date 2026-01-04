package com.enderio.enderio.compat.jei;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.content.filters.FilterSlot;
import com.enderio.enderio.content.filters.fluid.FluidFilterSlot;
import com.enderio.enderio.content.filters.item.ItemFilterSlot;
import com.enderio.enderio.foundation.network.packets.ServerboundSetFluidFilterSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSetItemFilterSlotPacket;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class FilterGhostIngredientHandler implements IGhostIngredientHandler<EnderContainerScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(EnderContainerScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();

        var menu = gui.getMenu();
        for (int i = 0; i < menu.slots.size(); i++) {
            var slot = menu.getSlot(i);
            if (!slot.isActive()) {
                continue;
            }

            Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 17, 17);

            if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
                ItemStack currentIngredient = (ItemStack) ingredient.getIngredient();

                if (slot instanceof ItemFilterSlot itemFilterSlot) {
                    targets.add(new ItemStackTarget<>(bounds, menu.containerId, i, itemFilterSlot));
                } else if (slot instanceof FilterSlot<?> otherFilterSlot) {
                    // If the item can be converted to the resource, allow it to be dragged too.
                    if (otherFilterSlot.getResourceFrom(currentIngredient).isPresent()) {
                        targets.add(new IndirectItemStackTarget<>(bounds, menu.containerId, i, otherFilterSlot));
                    }
                }
            } else if (ingredient.getType() == NeoForgeTypes.FLUID_STACK) {
                if (slot instanceof FluidFilterSlot fluidFilterSlot) {
                    targets.add(new FluidStackTarget<>(bounds, menu.containerId, i, fluidFilterSlot));
                }
            }
        }

        return targets;
    }

    @Override
    public void onComplete() {
    }

    private record ItemStackTarget<I>(Rect2i bounds, int containerId, int slotIndex, ItemFilterSlot slot)
            implements Target<I> {
        @Override
        public Rect2i getArea() {
            return bounds;
        }

        @Override
        public void accept(I ingredient) {
            slot.setResource((ItemStack) ingredient);
            ClientPacketDistributor.sendToServer(new ServerboundSetItemFilterSlotPacket(containerId, slotIndex, (ItemStack) ingredient));
        }
    }

    private record IndirectItemStackTarget<I>(Rect2i bounds, int containerId, int slotIndex, FilterSlot<?> slot)
            implements Target<I> {
        @Override
        public Rect2i getArea() {
            return bounds;
        }

        @Override
        public void accept(I ingredient) {
            slot.safeInsert((ItemStack) ingredient);
            ClientPacketDistributor.sendToServer(new ServerboundSetItemFilterSlotPacket(containerId, slotIndex, (ItemStack) ingredient));
        }
    }

    private record FluidStackTarget<I>(Rect2i bounds, int containerId, int slotIndex, FluidFilterSlot slot)
            implements Target<I> {
        @Override
        public Rect2i getArea() {
            return bounds;
        }

        @Override
        public void accept(I ingredient) {
            slot.setResource((FluidStack) ingredient);
            ClientPacketDistributor.sendToServer(new ServerboundSetFluidFilterSlotPacket(containerId, slotIndex, (FluidStack) ingredient));
        }
    }

}
