package com.enderio.base.common.integrations.jei;

import com.enderio.base.common.menu.EntityFilterSlot;
import com.enderio.base.common.menu.FilterSlot;
import com.enderio.base.common.menu.FluidFilterSlot;
import com.enderio.base.common.menu.ItemFilterSlot;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.client.gui.screen.EIOScreen;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class BaseGhostSlotHandler implements IGhostIngredientHandler<EIOScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(EIOScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        for (var slot : gui.getMenu().slots) {
            if (!slot.isActive()) {
                continue;
            }

            Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 17, 17);

            if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
                ItemStack currentIngredient = (ItemStack)ingredient.getIngredient();

                // Any filter slot will accept items too, in case they are supported (like entity for example)
                if (slot instanceof ItemFilterSlot itemFilterSlot) {
                    targets.add(new ItemStackTarget<>(bounds, itemFilterSlot));
                } else if (slot instanceof FluidFilterSlot fluidFilterSlot) {
                    if (currentIngredient.getCapability(Capabilities.FluidHandler.ITEM) != null) {
                        targets.add(new IndirectItemStackTarget<>(bounds, fluidFilterSlot));
                    }
                } else if (slot instanceof EntityFilterSlot entityFilterSlot) {
                    if (currentIngredient.is(EIOTags.Items.ENTITY_STORAGE) || currentIngredient.getItem() instanceof SpawnEggItem) {
                        targets.add(new IndirectItemStackTarget<>(bounds, entityFilterSlot));
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
