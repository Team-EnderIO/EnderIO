package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.core.common.util.NumberUtils;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.EnergyTextboxWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.blockentity.PowerBufferBlockEntity;
import com.enderio.machines.common.menu.OmniBufferMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Objects;

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

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 8 - 12, topPos + 22, 16, 16, menu, this::addRenderableWidget, font));

        input = new EnergyTextboxWidget(this, () -> this.getMenu().getBlockEntity().getEnergyStorage().getMaxEnergyUse(), this.font, leftPos + 33, topPos + 18, 49, this.font.lineHeight + 2, Component.literal("PBInputBox"));
        input.setResponder(this.getMenu().getBlockEntity()::setMaxInputText);
        input.setValue(input.formatEnergy(""+getMenu().getBlockEntity().getMaxInput()));
        input.OnFocusStoppedResponder(this::updateInput);
        addRenderableOnly(addRenderableWidget(input));

        output = new EnergyTextboxWidget(this, () -> this.getMenu().getBlockEntity().getEnergyStorage().getMaxEnergyUse(), this.font, leftPos + 33, topPos + 52, 49, this.font.lineHeight + 2, Component.literal("PBOutputBox"));
        output.setResponder(this.getMenu().getBlockEntity()::setMaxOutputText);
        output.setValue(output.formatEnergy(""+getMenu().getBlockEntity().getMaxOutput()));
        output.OnFocusStoppedResponder(this::updateOutput);
        addRenderableOnly(addRenderableWidget(output));

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
        guiGraphics.drawString(font, EIOLang.INPUT.getString(), leftPos + 33, topPos + 18 - font.lineHeight - 2, 1, false);
        guiGraphics.drawString(font, EIOLang.OUTPUT.getString(), leftPos + 33, topPos + 52 - font.lineHeight - 2, 1, false);

        String maxIO = "Max: " + NumberUtils.formatWithPrefix(Objects.requireNonNull(getMenu().getBlockEntity()).getEnergyStorage().getMaxEnergyUse());
        guiGraphics.drawString(font, maxIO, leftPos + 33 + 49 - font.width(maxIO), topPos + 18 + font.lineHeight + 4, 0xff8b8b8b, false);
        guiGraphics.drawString(font, maxIO, leftPos + 33 + 49 - font.width(maxIO), topPos + 52 + font.lineHeight + 4, 0xff8b8b8b, false);

        if (!this.getMenu().getBlockEntity().isCapacitorInstalled()) {
            input.setValue("0");
            output.setValue("0");
        }
    }

    private void updateInput(String val){
        int amount = NumberUtils.getInteger(val);
        PowerBufferBlockEntity be = this.getMenu().getBlockEntity();
        if (be != null) {
            amount = Math.max(0, Math.min(amount, be.getEnergyStorage().getMaxEnergyUse()));
            be.setMaxInput(amount);
        }
    }
    private void updateOutput(String val){
        int amount = NumberUtils.getInteger(val);
        PowerBufferBlockEntity be = this.getMenu().getBlockEntity();
        if (be != null) {
            amount = Math.max(0, Math.min(amount, be.getEnergyStorage().getMaxEnergyUse()));
            be.setMaxOutput(amount);
        }
    }
}
