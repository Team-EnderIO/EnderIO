package com.enderio.machines.common.transceiver;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;


public class ChannelListWidget extends AbstractSelectionList<ChannelListWidget.Entry> {
    
    private Set<Channel> channels;

    public ChannelListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    public void setChannels(Set<Channel> channels) {
        this.clearEntries();
        this.channels = new LinkedHashSet<>(channels);

        for (Channel channel : channels) {
            String playerName = Minecraft.getInstance().player.getName().getString();
            if (channel.canDisplay(playerName)) {
                this.addEntry(new Entry(channel));
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getX() + this.getWidth() - 6;
    }

    @Override
    public int getRowWidth() {
        return this.getWidth();
    }

    @Override
    public int addEntry(Entry entry) {
        return super.addEntry(entry);
    }

    @Override
    public boolean removeEntry(Entry entry) {
        return super.removeEntry(entry);
    }

    public void removeChannelEntry(Entry entry, Channel channel) {
        channels.remove(channel);
        super.removeEntry(entry);
    }

    public void addChannel(Channel channel) {
        if (!channels.contains(channel)) {
            Entry entry = new Entry(channel);
            channels.add(channel);
            super.addEntry(entry);
        }
    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        private final Channel channel;

        public Entry(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight,
            int mouseX, int mouseY, boolean hovered, float partialTick) {

            int bgColor;
            if (hovered) {
                bgColor = 0xFF666666;
            } else {
                bgColor = 0xFF202020;
            }

            guiGraphics.fill(x, y, x + entryWidth, y + entryHeight, bgColor);

            int textColor =  0xFFE0E0E0;
            guiGraphics.drawString(Minecraft.getInstance().font, channel.name(), x, y + 6, textColor);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                ChannelListWidget.this.setSelected(this);
                return true;
            }
            return false;
        }

        @Nullable
        public Channel getChannel() {
            return channel;
        }
    }
}
