package com.enderio.base.common.integrations.jei;

import com.enderio.base.common.menu.FilterSlot;
import com.enderio.base.common.menu.FluidFilterSlot;
import com.enderio.base.common.menu.ItemFilterSlot;
import com.enderio.core.client.gui.screen.EIOScreen;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class FilterGhostIngredientHandler implements IGhostIngredientHandler<EIOScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(EIOScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        for (var slot : gui.getMenu().slots) {
            if (!slot.isActive()) {
                continue;
            }

            Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 17, 17);

            if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
                ItemStack currentIngredient = (ItemStack) ingredient.getIngredient();

                if (slot instanceof ItemFilterSlot itemFilterSlot) {
                    targets.add(new ItemStackTarget<>(bounds, itemFilterSlot));
                } else if (slot instanceof FilterSlot<?> otherFilterSlot) {
                    // If the item can be converted to the resource, allow it to be dragged too.
                    if (otherFilterSlot.getResourceFrom(currentIngredient).isPresent()) {
                        targets.add(new IndirectItemStackTarget<>(bounds, otherFilterSlot));
                    }
                }
            } else if (ingredient.getType() == NeoForgeTypes.FLUID_STACK) {
                if (slot instanceof FluidFilterSlot fluidFilterSlot) {
                    targets.add(new FluidStackTarget<>(bounds, fluidFilterSlot));
                }
            }
        }

        return targets;
    }

    @Override
    public void onComplete() {
    }

    private record ItemStackTarget<I>(Rect2i bounds, ItemFilterSlot slot) implements Target<I> {
        @Override
        public Rect2i getArea() {
            return bounds;
        }

        @Override
        public void accept(I ingredient) {
            slot.setResource((ItemStack) ingredient);
        }
    }

    private record IndirectItemStackTarget<I>(Rect2i bounds, FilterSlot<?> slot) implements Target<I> {
        @Override
        public Rect2i getArea() {
            return bounds;
        }

        @Override
        public void accept(I ingredient) {
            slot.safeInsert((ItemStack) ingredient);
        }
    }

    private record FluidStackTarget<I>(Rect2i bounds, FluidFilterSlot slot) implements Target<I> {
        @Override
        public Rect2i getArea() {
            return bounds;
        }

        @Override
        public void accept(I ingredient) {
            slot.setResource((FluidStack) ingredient);
        }
    }

}
