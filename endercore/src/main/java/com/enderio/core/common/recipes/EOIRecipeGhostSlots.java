package com.enderio.core.common.recipes;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class EOIRecipeGhostSlots extends GhostSlots {
    private final Reference2ObjectMap<GuiSlot, EOIRecipeGhostSlots.GhostSlot> EIOIngredients = new Reference2ObjectArrayMap<>();

    public EOIRecipeGhostSlots(SlotSelectTime slotSelectTime) {
        super(slotSelectTime);
    }

    public void setSlot(GuiSlot slot, ContextMap context, SlotDisplay contents, EOIRecipeGhostSlots.GhostSlot ghostSlot) {
        List<ItemStack> entries = contents.resolveForStacks(context);
        if (!entries.isEmpty()) {
            this.EIOIngredients.put(slot, ghostSlot);
        }
    }

    @Override
    public void clear() {
        super.clear();
        EIOIngredients.clear();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, boolean isResultSlotBig) {
        this.ingredients.forEach((slot, ingredient) -> {
            int x = slot.x;
            int y = slot.y;
            if (ingredient.isResultSlot() && isResultSlotBig) {
                graphics.fill(x - 4, y - 4, x + 20, y + 20, 822018048);
            } else {
                graphics.fill(x, y, x + 16, y + 16, 822018048);
            }

            ItemStack itemStack = ingredient.getItem(this.slotSelectTime.currentIndex());
            graphics.fakeItem(itemStack, x, y);
            graphics.fill(x, y, x + 16, y + 16, 822083583);
            if (itemStack.count() > 1) {
                graphics.itemDecorations(minecraft.font, itemStack, x, y);
            }
        });
        this.EIOIngredients.forEach((slot, ingredient) -> ingredient.extractRenderState(graphics, minecraft, isResultSlotBig));
    }

    @Override
    public void extractTooltip(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, @Nullable Slot hoveredSlot) {
        super.extractTooltip(graphics, minecraft, mouseX, mouseY, hoveredSlot);
        for (var entry : this.EIOIngredients.entrySet()) {
            if (entry.getKey().isHovered(mouseX, mouseY)) {
                entry.getValue().extractTooltip(graphics, minecraft, mouseX, mouseY, this.slotSelectTime.currentIndex());
            }
        }
    }

    record GuiSlot(int x, int y, int width, int height) {
        public boolean isHovered(int mouseX, int mouseY) {
            return true;
        }
    }

    public interface GhostSlot {

        boolean isResult();

        void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, boolean isResultSlotBig);

        void extractTooltip(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int time);

    }

    public record FluidSlot(GuiSlot slot, FluidStack fluidStack, boolean isResult) implements GhostSlot {

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, boolean isResultSlotBig) {

        }

        @Override
        public void extractTooltip(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int time) {

        }
    }
}
