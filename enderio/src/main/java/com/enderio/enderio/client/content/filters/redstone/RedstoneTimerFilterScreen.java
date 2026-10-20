package com.enderio.enderio.client.content.filters.redstone;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.redstone.RedstoneTimerFilterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneTimerFilterScreen extends EnderContainerScreen<RedstoneTimerFilterMenu> {

    private static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/40/item_filter.png");

    private EditBox timerInput;

    public RedstoneTimerFilterScreen(RedstoneTimerFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 183, 201);
    }

    @Override
    protected void init() {
        super.init();
        timerInput = new EditBox(this.font, this.leftPos + 60, this.topPos + 20, 60, 20, Component.literal("" + getMenu().getFilter().getMaxTicks())) {

            @Override
            public boolean charTyped(CharacterEvent event) {
                return Character.isDigit(event.codepoint()) && super.charTyped(event);
            }
        };
        timerInput.setValue("" + getMenu().getFilter().getMaxTicks());
        addRenderableWidget(timerInput);
        addRestorableState("timer", timerInput);
        addRenderableWidget(Button
            .builder(FiltersLang.GUI_CONFIRM, button -> getMenu().setTimer(timerInput.getValue()))
            .pos(this.leftPos + 60, this.topPos + 41)
            .size(60, 20)
            .build());
    }

    @Override
    public boolean onKeyPressed(KeyEvent event) {
        if (timerInput.isFocused()) {
            if (timerInput.keyPressed(event) || timerInput.canConsumeInput()) {
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
