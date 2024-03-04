package com.enderio.base.common.network;

import com.enderio.EnderIO;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.core.common.network.ClientToServerMenuPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class UpdateCoordinateSelectionNameMenuPacket extends ClientToServerMenuPacket<CoordinateMenu> {

    public static ResourceLocation ID = EnderIO.loc("update_coordinate_selection_name");

    private final String name;

    public UpdateCoordinateSelectionNameMenuPacket(int containerID, String name) {
        super(CoordinateMenu.class, containerID);
        this.name = name;
    }

    public UpdateCoordinateSelectionNameMenuPacket(FriendlyByteBuf buf) {
        super(CoordinateMenu.class, buf);
        name = buf.readUtf(50);
    }

    public String getName() {
        return name;
    }

    @Override
    public void write(FriendlyByteBuf writeInto) {
        super.write(writeInto);
        writeInto.writeUtf(name, 50);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
