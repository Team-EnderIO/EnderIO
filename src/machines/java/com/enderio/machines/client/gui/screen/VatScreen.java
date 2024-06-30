package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EIOImageButton;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.FermentationWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.VatMenu;
import com.enderio.machines.common.recipe.FermentingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VatScreen extends EIOScreen<VatMenu> {

    private static final ResourceLocation VAT_BG = EnderIO.loc("textures/gui/vat.png");
    private static final ResourceLocation VAT_COVER = EnderIO.loc("vat_cover");
    public static final ResourceLocation MOVE_FLUID = EnderIO.loc("buttons/move_fluid");
    public static final ResourceLocation VOID_FLUID = EnderIO.loc("buttons/void_fluid");

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
            new FermentationWidget(this::isCrafting, this::inputFluidStack, this::outputFluidStack, this::getProgress, 76 + leftPos, 34 + topPos, 26, 28));

        addRenderableOnly(new ProgressWidget.BottomUp(this, this::getProgress, 82 + leftPos, 64 + topPos, 14, 14, 176, 0));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font));
        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));

        addRenderableWidget(new EIOImageButton(this, leftPos + 29, topPos + 62, 16, 16, new WidgetSprites(MOVE_FLUID, MOVE_FLUID),
            press -> menu.getBlockEntity().moveFluidToOutputTank(), MachineLang.TRANSFER_TANK));

        addRenderableWidget(new EIOImageButton(this, leftPos + 131, topPos + 62, 16, 16, new WidgetSprites(VOID_FLUID, VOID_FLUID),
            press -> menu.getBlockEntity().dumpOutputTank(), MachineLang.DUMP_TANK));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        updateRecipeCache();
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

        guiGraphics.blitSprite(VAT_COVER, 76 + leftPos, 34 + topPos, 26, 28);
        drawModifierStrings(guiGraphics);
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
