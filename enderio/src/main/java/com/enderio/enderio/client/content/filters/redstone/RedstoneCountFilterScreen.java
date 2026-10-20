package com.enderio.enderio.client.content.filters.redstone;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.foundation.widgets.DyeColorPickerWidget;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.redstone.RedstoneCountFilterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneCountFilterScreen extends EnderContainerScreen<RedstoneCountFilterMenu> {
    private static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/40/item_filter.png");

    private EditBox countInput;

    public RedstoneCountFilterScreen(RedstoneCountFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 183, 201);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new DyeColorPickerWidget(this.leftPos + 15, this.topPos + 30,
                getMenu().getFilter()::getChannel, getMenu()::setChannel, ConduitLang.REDSTONE_CHANNEL));

        countInput = new EditBox(this.font, this.leftPos + 60, this.topPos + 20, 60, 20,
                Component.literal("" + getMenu().getFilter().getMaxCount())) {

            @Override
            public boolean charTyped(CharacterEvent event) {
                return Character.isDigit(event.codepoint()) && super.charTyped(event);
            }
        };
        countInput.setValue("" + getMenu().getFilter().getMaxCount());
        addRenderableWidget(countInput);
        addRestorableState("count", countInput);
        addRenderableWidget(Button.builder(FiltersLang.GUI_CONFIRM, button -> getMenu().setCount(countInput.getValue()))
                .pos(this.leftPos + 60, this.topPos + 41)
                .size(60, 20)
                .build());

    }

    @Override
    public boolean onKeyPressed(KeyEvent event) {
        if (countInput.isFocused()) {
            if (countInput.keyPressed(event) || countInput.canConsumeInput()) {
                return true;
            }
        }

        return super.onKeyPressed(event);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, getLeftPos(), getTopPos(), 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
