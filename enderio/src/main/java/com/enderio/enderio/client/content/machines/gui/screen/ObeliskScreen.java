package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.CapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.EIOCommonWidgets;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.obelisks.ObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public abstract class ObeliskScreen<J extends ObeliskBlockEntity<J>, T extends ObeliskMenu<J>>
        extends MachineScreen<T> {

    private final Identifier background;

    public ObeliskScreen(T menu, Inventory playerInventory, Component title, Identifier background,
            int imageWidth, int imageHeight) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
        this.background = background;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + 34, MachinesLang.HIDE_RANGE,
            MachinesLang.SHOW_RANGE, menu::isRangeVisible,
            (ignored) -> handleButtonPress(ObeliskMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(EIOCommonWidgets.createRangeIncrease(leftPos + imageWidth - 2 * 16, topPos + 2 + 16 * 2,
                (b) -> handleButtonPress(ObeliskMenu.INCREASE_BUTTON_ID)));
        addRenderableWidget(EIOCommonWidgets.createRangeDecrease(leftPos + imageWidth - 2 * 16, topPos + 2 + 16 * 2 + 8,
                (b) -> handleButtonPress(ObeliskMenu.DECREASE_BUTTON_ID)));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(font, getMenu().getBlockEntity().getRange() + "",
                leftPos + imageWidth - 8 - 16 - font.width(getMenu().getBlockEntity().getRange() + "") - 10,
                topPos + 16 * 2 + 6, 0, false);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.text(font, MachinesLang.RANGE, imageWidth - 6 - font.width(MachinesLang.RANGE), 16 + 8, CommonColors.DARK_GRAY, false);

        graphics.text(font, MachinesLang.MAX_RANGE, imageWidth / 2 - font.width(MachinesLang.MAX_RANGE) / 2, 5, CommonColors.DARK_GRAY,
                false);
        String maxRange = getMenu().getMaxRange() + "";
        graphics.text(font, maxRange, imageWidth / 2 - font.width(maxRange) / 2, 5 + font.lineHeight + 3, CommonColors.DARK_GRAY,
                false);

        graphics.text(font,
                TooltipUtil.withArgs(MachinesLang.OBELISK_UPKEEP, getMenu().getBlockEntity().getPerTickEnergyCost()),
                imageWidth / 2 - font.width(MachinesLang.MAX_RANGE) / 2, 62, 0, false);

        super.extractLabels(graphics, xm, ym);
    }

    @Override
    protected void renderSlotContents(GuiGraphicsExtractor graphics, ItemStack itemstack, Slot slot,
            @Nullable String countString) {
        if (menu.getBlockEntity().requiresFilter() && slot.index == 1 && itemstack.isEmpty()) {
            ItemStack stack = new ItemStack(EIOItems.BASIC_SOUL_FILTER.get());
            graphics.fakeItem(stack, slot.x, slot.y);
            graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80888888);
        } else {
            super.renderSlotContents(graphics, itemstack, slot, countString);
        }
    }

}
