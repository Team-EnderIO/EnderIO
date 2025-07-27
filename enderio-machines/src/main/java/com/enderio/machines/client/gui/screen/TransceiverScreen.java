package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.ChannelSelectWidget;
import com.enderio.machines.common.transceiver.Channel;
import com.enderio.machines.common.transceiver.ChannelType;
import com.enderio.machines.common.transceiver.TransceiverMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.LinkedHashSet;
import java.util.Set;

public class TransceiverScreen extends MachineScreen<TransceiverMenu> {

    private static final ResourceLocation TRANSCEIVER_GENERAL_TEXTURE = EnderIO.loc("textures/gui/screen/transceiver_general.png");
    private static final ResourceLocation ICON_CONFIGURE = EnderIO.loc("icon/configure");

    private static final int WIDTH = 255;
    private static final int HEIGHT = 166;
    private static final int TAB_BUTTON_SIZE = 12;
    private static final int TAB_BUTTON_SPACING = 20;

    public TransceiverScreen(TransceiverMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        refreshWidgets();
    }

    private void switchToTab(ChannelType newType) {
        menu.setSelectedType(newType);
        refreshWidgets();
    }

    private void refreshWidgets() {
        clearWidgets();
        addTabButtons();

        if (menu.getSelectedType() != null) {
            createChannelSelectWidget();
        } else {
            addRenderableOnly(new CapacitorEnergyWidget(11 + leftPos, 14 + topPos, 9, 58, menu::getEnergyStorage,
                menu::isCapacitorInstalled));
        }
    }

    private void createChannelSelectWidget() {
        ChannelType selectedType = menu.getSelectedType();

        // Get channels from block entity
        Set<Channel> receiveChannels = menu.getBlockEntity().getReceiveChannels().getChannels(selectedType);
        Set<Channel> sendChannels = menu.getBlockEntity().getSendChannels().getChannels(selectedType);
        Set<Channel> allChannels = menu.getChannelList().get(selectedType);

        // Calculate available channels (not in send or receive)
        Set<Channel> availableChannels = calculateAvailableChannels(allChannels, sendChannels, receiveChannels);

        ChannelSelectWidget.Config config = new ChannelSelectWidget.Config(
            this,
            availableChannels,
            sendChannels,
            receiveChannels,
            leftPos,
            topPos,
            imageWidth,
            imageHeight
        );

        ChannelSelectWidget channelSelectWidget = new ChannelSelectWidget(config);
        registerChannelSelectWidget(channelSelectWidget);
    }

    private Set<Channel> calculateAvailableChannels(Set<Channel> allChannels, Set<Channel> sendChannels, Set<Channel> receiveChannels) {
        Set<Channel> availableChannels = new LinkedHashSet<>();

        for (Channel channel : allChannels) {
            if (!receiveChannels.contains(channel) && !sendChannels.contains(channel)) {
                availableChannels.add(channel);
            }
        }

        return availableChannels;
    }

    private void registerChannelSelectWidget(ChannelSelectWidget widget) {
        addOverlayRenderable(1, widget);
        addRenderableWidget(widget);

        for (var childWidget : widget.getChildren()) {
            addOverlayRenderable(2, childWidget);
            addRenderableWidget(childWidget);
        }
    }

    public void addTabButtons() {
        int x = this.leftPos + WIDTH;
        int y = this.topPos + 10;

        IconButton generalButton = createTabButton(x, y, ICON_CONFIGURE, Component.empty(), () -> switchToTab(null));
        addRenderableWidget(generalButton);

        y += TAB_BUTTON_SPACING;

        for (ChannelType type : ChannelType.values()) {
            IconButton button = createTabButton(x, y, type.icon, Component.literal(type.tooltip), () -> switchToTab(type));
            addRenderableWidget(button);
            y += TAB_BUTTON_SPACING;
        }
    }

    private IconButton createTabButton(int x, int y, ResourceLocation icon, Component tooltip, Runnable action) {
        return new IconButton(x, y, TAB_BUTTON_SIZE, TAB_BUTTON_SIZE, icon, tooltip, action);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TRANSCEIVER_GENERAL_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
