package com.enderio.machines.common.integrations.jei;

import com.enderio.machines.client.gui.screen.MachineScreen;
import com.enderio.machines.common.menu.GhostMachineSlot;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MachinesGhostSlotHandler implements IGhostIngredientHandler<MachineScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(MachineScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {

        if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
            List<Target<I>> targets = new ArrayList<>();
            for (var slot : gui.getMenu().slots) {
                if (!slot.isActive()) {
                    continue;
                }

                Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 17, 17);

                if (slot instanceof GhostMachineSlot ghostSlot) {
                    targets.add(new Target<>() {
                        @Override
                        public Rect2i getArea() {
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient) {
                            ghostSlot.set((ItemStack) ingredient);
                        }
                    });
                }
            }

            return targets;
        }

        return List.of();
    }

    @Override
    public void onComplete() {
    }
}
