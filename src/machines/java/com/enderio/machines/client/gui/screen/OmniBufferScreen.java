package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.EnergyTextboxWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.blockentity.PowerBufferBlockEntity;
import com.enderio.machines.common.menu.OmniBufferMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OmniBufferScreen extends EIOScreen<OmniBufferMenu> {

    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/omni_buffer.png");
    private EnergyTextboxWidget input;
    private EnergyTextboxWidget output;

    public OmniBufferScreen(OmniBufferMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 16 + leftPos, 14 + topPos, 9, 42)));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 22, 16, 16, menu, this::addRenderableWidget, font));

        input = new EnergyTextboxWidget(this, () -> this.getMenu().getBlockEntity().getEnergyStorage().getMaxEnergyUse(), this.font, leftPos + 33, topPos + 18, 49, this.font.lineHeight + 2, Component.empty());
        input.setResponder(getMenu().getBlockEntity()::setMaxInput);
        input.setValue(Integer.toString(this.getMenu().getBlockEntity().getMaxInput()));
        addRenderableWidget(input);

        output = new EnergyTextboxWidget(this, () -> this.getMenu().getBlockEntity().getEnergyStorage().getMaxEnergyUse(), this.font, leftPos + 33, topPos + 48, 49, this.font.lineHeight + 2, Component.empty());
        output.setResponder(getMenu().getBlockEntity()::setMaxOutput);
        output.setValue(output.formatEnergy(Integer.toString(this.getMenu().getBlockEntity().getMaxOutput())));
        addRenderableWidget(output);

    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(175, 166);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        PowerBufferBlockEntity be = this.getMenu().getBlockEntity();
        guiGraphics.drawString(font, EIOLang.INPUT.getString() + ":", leftPos + 33, topPos + 18 - font.lineHeight - 2, 1, false);
        guiGraphics.drawString(font, EIOLang.OUTPUT.getString() + ":", leftPos + 33, topPos + 48 - font.lineHeight - 2, 1, false);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        PowerBufferBlockEntity be = this.getMenu().getBlockEntity();
        if (be != null) {
            input.setValue(input.formatEnergy(Integer.toString(be.getMaxInput())));
            output.setValue(output.formatEnergy(Integer.toString(be.getMaxOutput())));
        }
    }
}
