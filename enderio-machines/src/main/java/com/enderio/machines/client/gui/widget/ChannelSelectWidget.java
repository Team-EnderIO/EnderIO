package com.enderio.machines.client.gui.widget;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.machines.client.gui.screen.TransceiverScreen;
import com.enderio.machines.common.network.transceiver.AddRemoveGlobalChannelPacket;
import com.enderio.machines.common.network.transceiver.AddRemoveTransceiverChannelPacket;
import com.enderio.machines.common.transceiver.Channel;
import com.enderio.machines.common.transceiver.ChannelListWidget;
import com.enderio.machines.common.transceiver.ChannelType;
import com.enderio.machines.common.transceiver.TransceiverBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChannelSelectWidget extends EIOWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = EnderIO.loc("textures/gui/screen/transceiver_channel_select.png");
    private static final ResourceLocation ICON_CHANNEL_PRIVATE = EnderIO.loc("buttons/channel_private");
    private static final ResourceLocation ICON_CHANNEL_PUBLIC = EnderIO.loc("buttons/channel_public");
    private static final ResourceLocation ICON_CHANNEL_ADD = EnderIO.loc("buttons/channel_add");
    private static final ResourceLocation ICON_CHANNEL_DELETE = EnderIO.loc("buttons/channel_delete");
    private static final ResourceLocation ICON_ARROWS = EnderIO.loc("buttons/arrows");

    private static final int CHANNEL_NAME_MAX_LENGTH = 50;

    public record Config(TransceiverScreen screen, Set<Channel> availableChannels, Set<Channel> sendChannels, Set<Channel> receiveChannels, int x, int y, int width, int height) {
            public Config(TransceiverScreen screen, Set<Channel> availableChannels, Set<Channel> sendChannels, Set<Channel> receiveChannels, int x, int y, int width, int height) {
                this.screen = Objects.requireNonNull(screen, "Screen cannot be null");
                this.availableChannels = Objects.requireNonNull(availableChannels, "Available channels cannot be null");
                this.sendChannels = Objects.requireNonNull(sendChannels, "Send channels cannot be null");
                this.receiveChannels = Objects.requireNonNull(receiveChannels, "Receive channels cannot be null");
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }
        }

    public interface ChannelOperations {
        void addSendChannel(Channel channel);
        void removeSendChannel(Channel channel);
        void addReceiveChannel(Channel channel);
        void removeReceiveChannel(Channel channel);
        ChannelType getSelectedType();
        Supplier<Boolean> isPrivate();
        void setPrivate(boolean isPrivate);
    }

    private final ChannelOperations channelOps;
    private final ChannelListWidget availableChannelsWidget;
    private final ChannelListWidget sendChannelsWidget;
    private final ChannelListWidget receiveChannelsWidget;
    private final EditBox channelNameBox;
    private final ToggleIconButton privateButton;
    private final IconButton addChannelButton;
    private final IconButton deleteChannelButton;
    private final IconButton sendButton;
    private final IconButton receiveButton;
    private final List<AbstractWidget> children = new ArrayList<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public ChannelSelectWidget(Config config) {
        super(config.x(), config.y(), config.width(), config.height());

        this.channelOps = createChannelOperations(config.screen());

        this.availableChannelsWidget = createChannelListWidget(config.availableChannels(),
            config.x() + 7, config.y() + 48, 104, 90);

        int sideX = config.x() + 7 + 104 + 32;
        this.sendChannelsWidget = createChannelListWidget(config.sendChannels(),
            sideX, config.y() + 48, 104, 35);
        this.receiveChannelsWidget = createChannelListWidget(config.receiveChannels(),
            sideX, config.y() + 48 + 35 + 20, 104, 35);

        this.channelNameBox = createChannelNameBox(config.x() + 25, config.y() + 12);
        this.privateButton = createPrivateButton(config.x() + 118, config.y() + 12);
        this.addChannelButton = createAddChannelButton(config.x() + 137, config.y() + 12);
        this.deleteChannelButton = createDeleteChannelButton(config.x() + 91, config.y() + 142);
        this.sendButton = createSendButton(config.x() + 119, config.y() + 58);
        this.receiveButton = createReceiveButton(config.x() + 119, config.y() + 113);


        Collections.addAll(children,
            availableChannelsWidget, sendChannelsWidget, receiveChannelsWidget,
            channelNameBox, privateButton, addChannelButton, deleteChannelButton,
            sendButton, receiveButton
        );
    }


    private ChannelListWidget createChannelListWidget(Set<Channel> channels, int x, int y, int width, int height) {
        ChannelListWidget widget = new ChannelListWidget(Minecraft.getInstance(), width, height, 0, 17);
        widget.setPosition(x, y);
        widget.setChannels(channels);
        return widget;
    }

    private EditBox createChannelNameBox(int x, int y) {
        EditBox box = new EditBox(Minecraft.getInstance().font, x, y, 87, 18, Component.literal("ChannelName"));
        box.setCanLoseFocus(true);
        box.setTextColor(0xFFFFFFFF);
        box.setTextColorUneditable(0xFFFFFFFF);
        box.setBordered(true);
        box.setMaxLength(CHANNEL_NAME_MAX_LENGTH);
        box.setEditable(true);
        return box;
    }

    private ToggleIconButton createPrivateButton(int x, int y) {
        return new ToggleIconButton(x, y, 16, 16,
            (b) -> b ? ICON_CHANNEL_PRIVATE : ICON_CHANNEL_PUBLIC,
            (b) -> b ? EIOLang.CHANNEL_PRIVATE : EIOLang.CHANNEL_PUBLIC,
            channelOps.isPrivate(),
            channelOps::setPrivate
        );
    }

    private IconButton createAddChannelButton(int x, int y) {
        return new IconButton(x, y, 16, 16, ICON_CHANNEL_ADD, EIOLang.ADD_CHANNEL, this::addChannel);
    }

    private IconButton createDeleteChannelButton(int x, int y) {
        return new IconButton(x, y, 16, 16, ICON_CHANNEL_DELETE, EIOLang.DELETE_CHANNEL, this::removeSelectedChannel);
    }

    private IconButton createSendButton(int x, int y) {
        return new IconButton(x, y, 16, 16, ICON_ARROWS, Component.empty(), this::handleSendButton);
    }

    private IconButton createReceiveButton(int x, int y) {
        return new IconButton(x, y, 16, 16, ICON_ARROWS, Component.empty(), this::handleReceiveButton);
    }

    private ChannelOperations createChannelOperations(TransceiverScreen screen) {
        return new ChannelOperations() {
            @Override
            public void addSendChannel(Channel channel) {
                TransceiverBlockEntity blockEntity = screen.getMenu().getBlockEntity();
                PacketDistributor.sendToServer(new AddRemoveTransceiverChannelPacket(blockEntity.getBlockPos(), channel, true, true, false));
            }

            @Override
            public void removeSendChannel(Channel channel) {
                TransceiverBlockEntity blockEntity = screen.getMenu().getBlockEntity();
                PacketDistributor.sendToServer(new AddRemoveTransceiverChannelPacket(blockEntity.getBlockPos(), channel, false, true, false));
            }

            @Override
            public void addReceiveChannel(Channel channel) {
                TransceiverBlockEntity blockEntity = screen.getMenu().getBlockEntity();
                PacketDistributor.sendToServer(new AddRemoveTransceiverChannelPacket(blockEntity.getBlockPos(), channel, true, false, true));
            }

            @Override
            public void removeReceiveChannel(Channel channel) {
                TransceiverBlockEntity blockEntity = screen.getMenu().getBlockEntity();
                PacketDistributor.sendToServer(new AddRemoveTransceiverChannelPacket(blockEntity.getBlockPos(), channel, false, false, true));
            }

            @Override
            public ChannelType getSelectedType() {
                return screen.getMenu().getSelectedType();
            }

            @Override
            public Supplier<Boolean> isPrivate() {
                return () -> screen.getMenu().isPrivate().get();
            }

            @Override
            public void setPrivate(boolean isPrivate) {
                screen.getMenu().setPrivate(isPrivate);
            }
        };
    }

    private void handleSendButton() {
        handleChannelTransfer(
            availableChannelsWidget, sendChannelsWidget,
            channelOps::addSendChannel, channelOps::removeSendChannel
        );
    }

    private void handleReceiveButton() {
        handleChannelTransfer(
            availableChannelsWidget, receiveChannelsWidget,
            channelOps::addReceiveChannel, channelOps::removeReceiveChannel
        );
    }

    private void handleChannelTransfer(
        ChannelListWidget sourceWidget1, ChannelListWidget targetWidget1,
        Consumer<Channel> addOperation,
        Consumer<Channel> removeOperation) {

        if (transferChannel(sourceWidget1, targetWidget1, addOperation, true)) {
            return;
        }

        transferChannel(targetWidget1, sourceWidget1, removeOperation, false);
    }

    private boolean transferChannel(
        ChannelListWidget fromWidget, ChannelListWidget toWidget,
        Consumer<Channel> operation, boolean isAddOperation) {

        ChannelListWidget.Entry selectedEntry = fromWidget.getSelected();
        if (selectedEntry == null) {
            return false;
        }

        Channel channel = selectedEntry.getChannel();
        if (channel == null) {
            return false;
        }

        operation.accept(channel);

        if (isAddOperation) {
            fromWidget.removeEntry(selectedEntry);
            toWidget.addChannel(channel);
        } else {
            fromWidget.removeChannelEntry(selectedEntry, channel);
            toWidget.addEntry(selectedEntry);
        }

        return true;
    }

    private void addChannel() {
        String channelName = channelNameBox.getValue().trim();
        if (channelName.isEmpty()) {
            return;
        }

        try {
            String playerName = Minecraft.getInstance().player.getName().getString();
            boolean isPrivate = channelOps.isPrivate().get();
            ChannelType type = channelOps.getSelectedType();

            Channel channel = new Channel(channelName, playerName, type, isPrivate);
            PacketDistributor.sendToServer(new AddRemoveGlobalChannelPacket(channel, true));

            availableChannelsWidget.addChannel(channel);
            channelNameBox.setValue("");
        } catch (Exception e) {
            LOGGER.error("Failed to create channel {}", channelName);
        }
    }

    private void removeSelectedChannel() {
        ChannelListWidget.Entry selectedEntry = availableChannelsWidget.getSelected();
        if (selectedEntry == null) {
            return;
        }

        Channel channel = selectedEntry.getChannel();
        if (channel == null) {
            return;
        }

        try {
            PacketDistributor.sendToServer(new AddRemoveGlobalChannelPacket(channel, false));
            availableChannelsWidget.removeChannelEntry(selectedEntry, channel);
        } catch (Exception e) {
            LOGGER.error("Failed to delete channel {}", channel.name());
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(BACKGROUND_TEXTURE, getX(), getY(), 0, 0, getWidth(), getHeight());

        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, EIOLang.AVAILABLE, getX() + 59, getY() + 36, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(font, EIOLang.SEND, getX() + 199, getY() + 36, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(font, EIOLang.RECEIVE, getX() + 199, getY() + 92, 0xFFFFFFFF);
    }

    public List<AbstractWidget> getChildren() {
        return children;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (channelNameBox.isFocused()) {
            if (channelNameBox.keyPressed(keyCode, scanCode, modifiers) || channelNameBox.canConsumeInput()) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
