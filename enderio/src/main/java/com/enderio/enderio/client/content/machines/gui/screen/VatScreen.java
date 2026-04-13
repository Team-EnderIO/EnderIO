package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.FermentationWidget;
import com.enderio.enderio.client.foundation.widgets.FluidStackWidget;
import com.enderio.enderio.client.foundation.widgets.ProgressWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.content.machines.vat.VatMenu;
import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public class VatScreen extends MachineScreen<VatMenu> {

    public static final Identifier VAT_BG = EnderIO.id("textures/gui/screen/vat.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    private static final Identifier VAT_COVER = EnderIO.id("vat_cover");
    public static final Identifier MOVE_FLUID = EnderIO.id("buttons/move_fluid");
    public static final Identifier VOID_FLUID = EnderIO.id("buttons/void_fluid");

    private static final WidgetSprites MOVE_SPRITES = new WidgetSprites(MOVE_FLUID, MOVE_FLUID);
    private static final WidgetSprites VOID_SPRITES = new WidgetSprites(VOID_FLUID, VOID_FLUID);

    public VatScreen(VatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new FluidStackWidget(30 + leftPos, 12 + topPos, 15, 47, this::getFakedInputTankContents));
        addRenderableOnly(new FluidStackWidget(132 + leftPos, 12 + topPos, 15, 47, menu::getOutputTank));

        addRenderableOnly(new FermentationWidget(this::isCrafting, this::inputFluidStack, this::outputFluidStack,
                menu::getCraftingProgress, 76 + leftPos, 34 + topPos, 26, 28));

        addRenderableOnly(new ProgressWidget.BottomUp(VAT_BG, menu::getCraftingProgress, 82 + leftPos, 64 + topPos, 14,
                14, 176, 0));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        ImageButton transfer = new ImageButton(leftPos + 29, topPos + 62, 16, 16, MOVE_SPRITES, (b) -> handleButtonPress(VatMenu.MOVE_TO_OUTPUT_TANK_BUTTON_ID));
        transfer.setTooltip(Tooltip.create(MachinesLang.VAT_TRANSFER_TANK));
        addRenderableWidget(transfer);
        ImageButton dump = new ImageButton(leftPos + 131, topPos + 62, 16, 16, VOID_SPRITES, (b) -> handleButtonPress(VatMenu.DUMP_OUTPUT_TANK_BUTTON_ID));
        dump.setTooltip(Tooltip.create(MachinesLang.VAT_DUMP_TANK));
        addRenderableWidget(dump);

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, VAT_COVER, 76 + leftPos, 34 + topPos, 26, 28);
        drawModifierStrings(graphics);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, VAT_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
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

        return menu.getRecipe().value().output().create();
    }

    private FluidStorageInfo getFakedInputTankContents() {
        // Remove the amount of fluid in client screen to fake the effect of consumption of fluid.
        var currentContents = menu.getInputTank();

        int reduced = 0;
        var recipe = menu.getRecipe();
        if (isCrafting() && recipe != null) {
            reduced = recipe.value().input().amount();
        }

        int adjustedAmount = Math.max(menu.getInputTank().contents().getAmount() - reduced, 0);
        return new FluidStorageInfo(currentContents.contents().copyWithAmount(adjustedAmount), currentContents.capacity());
    }

    private void drawModifierStrings(GuiGraphicsExtractor graphics) {
        var recipe = menu.getRecipe();
        if (!isCrafting() || recipe == null) {
            return;
        }

        // left modifier
        ItemStack item = getMenu().getSlot(0).getItem();
        double modifier = FermentingRecipe.getModifier(item, recipe.value().firstReagent());
        String text = "x" + modifier;
        int x = getGuiLeft() + 63 - minecraft.font.width(text) / 2;
        graphics.text(minecraft.font, text, x, getGuiTop() + 32, CommonColors.DARK_GRAY, false);

        // right modifier
        item = getMenu().getSlot(1).getItem();
        modifier = FermentingRecipe.getModifier(item, recipe.value().secondReagent());
        text = "x" + modifier;
        x = getGuiLeft() + 113 - minecraft.font.width(text) / 2;
        graphics.text(minecraft.font, text, x, getGuiTop() + 32, CommonColors.DARK_GRAY, false);

    }
}
