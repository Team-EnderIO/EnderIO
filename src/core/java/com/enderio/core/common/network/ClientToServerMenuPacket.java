package com.enderio.core.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.Nullable;

public abstract class ClientToServerMenuPacket<Menu extends AbstractContainerMenu> implements CustomPacketPayload {

    private final int containerID;
    private final Class<Menu> menuClass;

    protected ClientToServerMenuPacket(Class<Menu> menuClass, int containerID) {
        this.containerID = containerID;
        this.menuClass = menuClass;
    }
    protected ClientToServerMenuPacket(Class<Menu> menuClass, FriendlyByteBuf buf) {
        this.containerID = buf.readInt();
        this.menuClass = menuClass;
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        writeInto.writeInt(containerID);
    }

    public boolean isValid(PlayPayloadContext context) {
        return context.player().map(player -> {
            AbstractContainerMenu menu = player.containerMenu;
            if (menu != null) {
                return menu.containerId == containerID
                    && menuClass.isAssignableFrom(menu.getClass());
            }

            return false;
        }).orElse(false);
    }

    @Nullable
    public Menu getMenu(PlayPayloadContext context) {
        return context.player().map(player -> menuClass.cast(player.containerMenu)).orElse(null);
    }

    public void handleWrongPlayer(PlayPayloadContext context) {
        //TODO: This method is called when a ClientPlayer sent invalid data to the server which can not be explained by a desync, but could be malicious intend or a version difference.
        // Currently there is no procedure to kick the player, or log that the player has sent invalid data. This should never happen using a unmodified mod on server or client
    }
}
