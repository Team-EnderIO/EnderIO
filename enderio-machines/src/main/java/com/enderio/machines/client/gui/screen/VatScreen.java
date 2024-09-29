package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.FermentationWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.menu.VatMenu;
import com.enderio.machines.common.recipe.FermentingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VatScreen extends MachineScreen<VatMenu> {

    public static final ResourceLocation VAT_BG = EnderIOBase.loc("textures/gui/screen/vat.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    private static final ResourceLocation VAT_COVER = EnderIOBase.loc("vat_cover");
    public static final ResourceLocation MOVE_FLUID = EnderIOBase.loc("buttons/move_fluid");
    public static final ResourceLocation VOID_FLUID = EnderIOBase.loc("buttons/void_fluid");

    private static final WidgetSprites MOVE_SPRITES = new WidgetSprites(MOVE_FLUID, MOVE_FLUID);
    private static final WidgetSprites VOID_SPRITES = new WidgetSprites(VOID_FLUID, VOID_FLUID);


    private FermentingRecipe recipeCache;
    private ResourceLocation recipeId;

    public VatScreen(VatMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        updateRecipeCache();
        addRenderableOnly(new FluidStackWidget(30 + leftPos, 12 + topPos, 15, 47, this::wrappedInputTank));
        addRenderableOnly(new FluidStackWidget(132 + leftPos, 12 + topPos, 15, 47, menu::getOutputTank));

        addRenderableOnly(
            new FermentationWidget(this::isCrafting, this::inputFluidStack, this::outputFluidStack, this::getProgress, 76 + leftPos, 34 + topPos, 26, 28));

        addRenderableOnly(new ProgressWidget.BottomUp(VAT_BG, this::getProgress, 82 + leftPos, 64 + topPos, 14, 14, 176, 0));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6, menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        addRenderableWidget(new ImageButton(leftPos + 29, topPos + 62, 16, 16, MOVE_SPRITES,
            (b) -> menu.moveFluidToOutputTank()));
        addRenderableWidget(new ImageButton(leftPos + 131, topPos + 62, 16, 16, VOID_SPRITES,
            (b) -> menu.dumpOutputTank()));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        updateRecipeCache();
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

        guiGraphics.blitSprite(VAT_COVER, 76 + leftPos, 34 + topPos, 26, 28);
        drawModifierStrings(guiGraphics);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(VAT_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    private void updateRecipeCache() {
        if (recipeId != getMenu().getBlockEntity().getRecipeId()) {
            recipeId = getMenu().getBlockEntity().getRecipeId();
            Optional<RecipeHolder<?>> optional = Minecraft.getInstance().level.getRecipeManager().byKey(recipeId);
            if (optional.isPresent() && optional.get().value() instanceof FermentingRecipe recipe) {
                recipeCache = recipe;
            }
        }
    }

    private boolean isCrafting() {
        return recipeCache != null && getMenu().getBlockEntity().getCraftingHost().getProgress() > 0;
    }

    private FluidStack inputFluidStack() {
        return getMenu().getBlockEntity().getInputTank().getFluid();
    }

    private FluidStack outputFluidStack() {
        return recipeCache.output();
    }

    private float getProgress() {
        return menu.getBlockEntity().getProgress();
    }

    /**
     * Wraps the essential parts of the input tank. Remove the amount of fluid in client screen to fake the effect of consumption of fluid.
     */
    private MachineFluidTank wrappedInputTank() {
        MachineFluidTank tank = getMenu().getBlockEntity().getInputTank();
        return new MachineFluidTank(0, null) {
            @Override
            public @NotNull FluidStack getFluid() {
                return tank.getFluid();
            }

            @Override
            public int getFluidAmount() {
                int reduced = 0;
                if (isCrafting()) {
                    reduced = recipeCache.input().amount();
                }
                return Math.max(tank.getFluidAmount() - reduced, 0);
            }

            @Override
            public int getCapacity() {
                return tank.getCapacity();
            }
        };
    }

    private void drawModifierStrings(GuiGraphics guiGraphics) {
        if (!isCrafting()) {
            return;
        }
        // left modifier
        ItemStack item = getMenu().getSlot(0).getItem();
        double modifier = FermentingRecipe.getModifier(item, recipeCache.leftReagent());
        String text = "x" + modifier;
        int x = getGuiLeft() + 63 - minecraft.font.width(text) / 2;
        guiGraphics.drawString(minecraft.font, text, x, getGuiTop() + 32, 4210752, false);

        // right modifier
        item = getMenu().getSlot(1).getItem();
        modifier = FermentingRecipe.getModifier(item, recipeCache.rightReagent());
        text = "x" + modifier;
        x = getGuiLeft() + 113 - minecraft.font.width(text) / 2;
        guiGraphics.drawString(minecraft.font, text, x, getGuiTop() + 32, 4210752, false);

    }
}
