package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.FluidTransformWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.menu.VatMenu;
import com.enderio.machines.common.recipe.FermentingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VatScreen extends EIOScreen<VatMenu> {

    private static final ResourceLocation VAT_BG = EnderIO.loc("textures/gui/vat.png");
    private static final ResourceLocation VAT_COVER = EnderIO.loc("vat_cover");
    private FermentingRecipe recipeCache;
    private ResourceLocation recipeId;

    public VatScreen(VatMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, false);
    }

    @Override
    protected void init() {
        super.init();
        updateRecipeCache();
        addRenderableOnly(new FluidStackWidget(this, this::wrappedInputTank, 30 + leftPos, 12 + topPos, 15, 47));
        addRenderableOnly(new FluidStackWidget(this, getMenu().getBlockEntity()::getOutputTank, 132 + leftPos, 12 + topPos, 15, 47));

        addRenderableOnly(
            new FluidTransformWidget(this::isCrafting, this::getInputFluid, this::getOutputFluid, this::getProgress, 76 + leftPos, 34 + topPos, 26, 28));

        addRenderableOnly(new ProgressWidget.BottomUp(this, this::getProgress, 82 + leftPos, 64 + topPos, 14, 14, 176, 0));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font));
        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        updateRecipeCache();
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

        guiGraphics.blitSprite(VAT_COVER, 76 + leftPos, 34 + topPos, 26, 28);
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return VAT_BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
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
        return getMenu().getBlockEntity().getCraftingHost().getProgress() > 0 && recipeCache != null;
    }

    private Fluid getInputFluid() {
        return getMenu().getBlockEntity().getInputTank().getFluid().getFluid();
    }

    private Fluid getOutputFluid() {
        return recipeCache.getOutputFluid();
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
                    reduced = recipeCache.getInputFluidAmount();
                }
                return tank.getFluidAmount() - reduced;
            }

            @Override
            public int getCapacity() {
                return tank.getCapacity();
            }
        };
    }
}
