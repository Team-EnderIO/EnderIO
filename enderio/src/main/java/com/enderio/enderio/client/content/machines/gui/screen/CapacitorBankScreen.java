//package com.enderio.enderio.client.content.machines.gui.screen;
//
//import com.enderio.enderio.EnderIO;
//import com.enderio.enderio.client.content.machines.gui.screen.base.LegacyMachineScreen;
//import com.enderio.enderio.client.content.machines.gui.widget.EnergyWidget;
//import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
//import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankMenu;
//import com.enderio.enderio.foundation.lang.EIOCommonLang;
//import net.minecraft.client.gui.GuiGraphicsExtractor;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Inventory;
//
//public class CapacitorBankScreen extends LegacyMachineScreen<CapacitorBankMenu> {
//
//    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/capacitor_bank.png");
//
//    public CapacitorBankScreen(CapacitorBankMenu menu, Inventory playerInventory, Component title) {
//        super(menu, playerInventory, title);
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//        addRenderableOnly(new EnergyWidget(8 + leftPos, 9 + topPos, 9, 68, menu::getEnergyStorage));
//
//        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
//                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));
//
//        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
//        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
//    }
//
//    @Override
//    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
//        super.extractBackground(graphics, mouseX, mouseY, a);
//        graphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
//    }
//}
