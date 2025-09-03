package com.enderio.conduits.client.gui;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.common.menu.RedstoneTimerFilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EIOImageButton;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneTimerFilterScreen extends EIOScreen<RedstoneTimerFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");
    private static final ResourceLocation BACK_TEXTURE = EnderIO.loc("textures/gui/icons/back.png");

    public RedstoneTimerFilterScreen(RedstoneTimerFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        EditBox pWidget = new EditBox(this.font, this.leftPos + 65, this.topPos + 40, 60, 20, Component.literal("" + getMenu().getFilter().getMaxTicks())) {
            @Override
            public boolean charTyped(char pCodePoint, int pModifiers) {
                return Character.isDigit(pCodePoint) && super.charTyped(pCodePoint, pModifiers);
            }
        };
        pWidget.setValue("" + getMenu().getFilter().getMaxTicks());
        addRenderableWidget(pWidget);
        addRenderableWidget(Button.builder(EIOLang.CONFIRM, pButton -> getMenu().setTimer(pWidget.getValue()))
            .pos(this.leftPos + 65, this.topPos + 62)
            .size(60, 20)
            .build());
        addRenderableWidget(new EIOImageButton(this, getGuiLeft() + 3, getGuiTop() + 3, 16, 16, 0, 0, 0, BACK_TEXTURE, button -> closeContainer()));
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
