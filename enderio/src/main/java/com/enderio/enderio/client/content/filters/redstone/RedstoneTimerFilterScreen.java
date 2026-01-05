package com.enderio.enderio.client.content.filters.redstone;

import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.redstone.RedstoneTimerFilterMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

public class RedstoneTimerFilterScreen extends EIOScreen<RedstoneTimerFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183, 201);
    private static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/40/item_filter.png");

    public RedstoneTimerFilterScreen(RedstoneTimerFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        EditBox widget = new EditBox(this.font, this.leftPos + 60, this.topPos + 20, 60, 20,
                Component.literal("" + getMenu().getFilter().getMaxTicks())) {
            @Override
            public boolean charTyped(char codePoint, int modifiers) {
                return Character.isDigit(codePoint) && super.charTyped(codePoint, modifiers);
            }
        };
        widget.setValue("" + getMenu().getFilter().getMaxTicks());
        addRenderableWidget(widget);
        addRenderableWidget(Button.builder(FiltersLang.GUI_CONFIRM, button -> getMenu().setTimer(widget.getValue()))
                .pos(this.leftPos + 60, this.topPos + 41)
                .size(60, 20)
                .build());
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return BG_SIZE;
    }
}
