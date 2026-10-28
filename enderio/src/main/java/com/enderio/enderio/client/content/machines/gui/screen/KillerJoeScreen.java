package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.foundation.widgets.EIOCommonWidgets;
import com.enderio.enderio.client.foundation.widgets.FluidStackWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.killer_joe.KillerJoeMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;

public class KillerJoeScreen extends MachineScreen<KillerJoeMenu> {

    private static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/killer_joe.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 187;

    public KillerJoeScreen(KillerJoeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);

        shouldRenderLabels = true;

        titleLabelY = 6 + 2;
        inventoryLabelY = 94;
    }

    @Override
    protected void init() {
        super.init();
        centerAlignTitleLabelX();

        // Add fluid tank widget
        addRenderableOnly(new FluidStackWidget(8 + leftPos, 7 + topPos, 17, 50, menu::getFluidTank));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
            menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        // Range controls
        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + 6 + (16 + 2),
            MachinesLang.HIDE_RANGE, MachinesLang.SHOW_RANGE, menu::isRangeVisible,
            (ignored) -> handleButtonPress(KillerJoeMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(EIOCommonWidgets.createRangeIncrease(leftPos + imageWidth - 6 - 16 - 10, topPos + 6 + (16 + 2),
            (b) -> handleButtonPress(KillerJoeMenu.INCREASE_BUTTON_ID)));
        addRenderableWidget(EIOCommonWidgets.createRangeDecrease(leftPos + imageWidth - 6 - 16 - 10, topPos + 6 + (16 + 2) + 8,
            (b) -> handleButtonPress(KillerJoeMenu.DECREASE_BUTTON_ID)));

        // Add activity widget to show machine states
        addRenderableWidget(new ActivityWidget(152 + leftPos, 68 + topPos, menu::getMachineStates));

        // Add IO config overlay
        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 93, 162, 87);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 6 + (16 + 2) * 2, overlay);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        String range = String.valueOf(menu.getRange());
        int rangeRight = imageWidth - 6 - 16 - 10 - 2;
        graphics.text(font, MachinesLang.RANGE, rangeRight - font.width(MachinesLang.RANGE), 6 + 16 + 2 + 8 - font.lineHeight, CommonColors.DARK_GRAY, false);
        graphics.text(font, range, rangeRight - font.width(range), 6 + 16 + 2 + 8 + 1, CommonColors.DARK_GRAY, false);

        super.extractLabels(graphics, mouseX, mouseY);
    }
}
