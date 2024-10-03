package com.enderio.core.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface CustomMenuPacketPayload<Menu extends AbstractContainerMenu> extends CustomPacketPayload {

    int containerId();

    Class<Menu> menuClass();

    default boolean isValid(IPayloadContext context) {
        var player = context.player();
        AbstractContainerMenu menu = player.containerMenu;
        return menu.containerId == containerId() && menuClass().isAssignableFrom(menu.getClass());
    }

    default Menu getMenu(IPayloadContext context) {
        var player = context.player();
        return menuClass().cast(player.containerMenu);
    }
}
