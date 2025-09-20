package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.FermentationWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.blocks.vat.FermentingRecipe;
import com.enderio.machines.common.blocks.vat.VatMenu;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.lang.MachineLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class VatScreen extends MachineScreen<VatMenu> {

    public static final ResourceLocation VAT_BG = EnderIO.loc("textures/gui/screen/vat.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    private static final ResourceLocation VAT_COVER = EnderIO.loc("vat_cover");
    public static final ResourceLocation MOVE_FLUID = EnderIO.loc("buttons/move_fluid");
    public static final ResourceLocation VOID_FLUID = EnderIO.loc("buttons/void_fluid");

    private static final WidgetSprites MOVE_SPRITES = new WidgetSprites(MOVE_FLUID, MOVE_FLUID);
    private static final WidgetSprites VOID_SPRITES = new WidgetSprites(VOID_FLUID, VOID_FLUID);

    public VatScreen(VatMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(FluidStackWidget.legacy(30 + leftPos, 12 + topPos, 15, 47, this::wrappedInputTank));
        addRenderableOnly(new FluidStackWidget(132 + leftPos, 12 + topPos, 15, 47, menu::getOutputTank));

        addRenderableOnly(new FermentationWidget(this::isCrafting, this::inputFluidStack, this::outputFluidStack,
                menu::getCraftingProgress, 76 + leftPos, 34 + topPos, 26, 28));

        addRenderableOnly(new ProgressWidget.BottomUp(VAT_BG, menu::getCraftingProgress, 82 + leftPos, 64 + topPos, 14,
                14, 176, 0));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        ImageButton transfer = new ImageButton(leftPos + 29, topPos + 62, 16, 16, MOVE_SPRITES, (b) -> handleButtonPress(VatMenu.MOVE_TO_OUTPUT_TANK_BUTTON_ID));
        transfer.setTooltip(Tooltip.create(MachineLang.TRANSFER_TANK));
        addRenderableWidget(transfer);
        ImageButton dump = new ImageButton(leftPos + 131, topPos + 62, 16, 16, VOID_SPRITES, (b) -> handleButtonPress(VatMenu.DUMP_OUTPUT_TANK_BUTTON_ID));
        dump.setTooltip(Tooltip.create(MachineLang.DUMP_TANK));
        addRenderableWidget(dump);

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

        guiGraphics.blitSprite(VAT_COVER, 76 + leftPos, 34 + topPos, 26, 28);
        drawModifierStrings(guiGraphics);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(VAT_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    private boolean isCrafting() {
        return menu.getRecipe() != null && menu.getCraftingProgress() > 0;
    }

    private FluidStack inputFluidStack() {
        return getMenu().getInputTank().contents();
    }

    private FluidStack outputFluidStack() {
        if (menu.getRecipe() == null) {
            return FluidStack.EMPTY;
        }

        return menu.getRecipe().value().output();
    }

    /**
     * Wraps the essential parts of the input tank. Remove the amount of fluid in client screen to fake the effect of consumption of fluid.
     */
    private MachineFluidTank wrappedInputTank() {
        return new MachineFluidTank(0, null) {
            @Override
            public @NotNull FluidStack getFluid() {
                return menu.getInputTank().contents();
            }

            @Override
            public int getFluidAmount() {
                int reduced = 0;
                var recipe = menu.getRecipe();
                if (isCrafting() && recipe != null) {
                    reduced = recipe.value().input().amount();
                }

                return Math.max(menu.getInputTank().contents().getAmount() - reduced, 0);
            }

            @Override
            public int getCapacity() {
                return menu.getInputTank().capacity();
            }
        };
    }

    private void drawModifierStrings(GuiGraphics guiGraphics) {
        var recipe = menu.getRecipe();
        if (!isCrafting() || recipe == null) {
            return;
        }

        // left modifier
        ItemStack item = getMenu().getSlot(0).getItem();
        double modifier = FermentingRecipe.getModifier(item, recipe.value().leftReagent());
        String text = "x" + modifier;
        int x = getGuiLeft() + 63 - minecraft.font.width(text) / 2;
        guiGraphics.drawString(minecraft.font, text, x, getGuiTop() + 32, 4210752, false);

        // right modifier
        item = getMenu().getSlot(1).getItem();
        modifier = FermentingRecipe.getModifier(item, recipe.value().rightReagent());
        text = "x" + modifier;
        x = getGuiLeft() + 113 - minecraft.font.width(text) / 2;
        guiGraphics.drawString(minecraft.font, text, x, getGuiTop() + 32, 4210752, false);

    }
}
