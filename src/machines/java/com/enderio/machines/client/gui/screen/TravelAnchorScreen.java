package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.machines.common.menu.TravelAnchorMenu;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TravelAnchorScreen extends MachineScreen<TravelAnchorMenu> {

    private static final ResourceLocation TRAVEL_ANCHOR_BG = EnderIO.loc("textures/gui/travel_anchor.png");
    private static final ResourceLocation VISIBILITY_BTNS = EnderIO.loc("textures/gui/icons/visibility_buttons.png");

    public TravelAnchorScreen(TravelAnchorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, false);
    }

    @Override
    protected void init() {
        super.init();
        EditBox name = new EditBox(this.font, leftPos + 25, topPos + 14, 87, 18, Component.literal("name"));
        name.setCanLoseFocus(true);
        name.setTextColor(0xFFFFFFFF);
        name.setTextColorUneditable(0xFFFFFFFF);
        name.setBordered(false);
        name.setMaxLength(50);
        name.setResponder(getMenu().getBlockEntity()::setName);
        name.setValue(getMenu().getBlockEntity().getName());
        this.addRenderableWidget(name);
        this.setInitialFocus(name);
        name.setEditable(true);

        addRenderableWidget(
            new ToggleImageButton<>(this, leftPos + 150, topPos + 10, 16, 16, 0, 0, 16, 0, VISIBILITY_BTNS, 32, 16, () -> menu.getBlockEntity().isVisible(),
                menu.getBlockEntity()::setIsVisible, () -> menu.getBlockEntity().isVisible() ? EIOLang.VISIBLE : EIOLang.NOT_VISIBLE));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return TRAVEL_ANCHOR_BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 184);
    }
}
