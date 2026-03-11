package com.enderio.core.common.network;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface CustomMenuPacketPayload<Menu extends AbstractContainerMenu> {

    int containerId();

    Class<Menu> menuClass();

    default boolean isValid(Supplier<NetworkEvent.Context> context) {
        var player = context.get().getSender();
        AbstractContainerMenu menu = player.containerMenu;
        return menu.containerId == containerId() && menuClass().isAssignableFrom(menu.getClass());
    }

    default Menu getMenu(Supplier<NetworkEvent.Context> context) {
        var player = context.get().getSender();
        return menuClass().cast(player.containerMenu);
    }
}
