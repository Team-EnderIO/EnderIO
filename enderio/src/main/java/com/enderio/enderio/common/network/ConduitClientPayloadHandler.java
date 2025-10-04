package com.enderio.enderio.common.network;

import com.enderio.enderio.common.conduits.menu.ConduitMenu;
import com.enderio.enderio.common.network.packets.ClientboundConduitExtraGuiDataPacket;
import com.enderio.enderio.common.network.packets.ClientboundConduitListPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ConduitClientPayloadHandler {
    private static final ConduitClientPayloadHandler INSTANCE = new ConduitClientPayloadHandler();

    public void handle(ClientboundConduitExtraGuiDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.containerId() == context.player().containerMenu.containerId) {
                if (context.player().containerMenu instanceof ConduitMenu menu) {
                    menu.setExtraGuiData(packet.extraGuiData());
                }
            }
        });
    }

    public void handle(ClientboundConduitListPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.containerId() == context.player().containerMenu.containerId) {
                if (context.player().containerMenu instanceof ConduitMenu menu) {
                    menu.setConnectedConduits(packet.conduits());
                }
            }
        });
    }

    public static ConduitClientPayloadHandler getInstance() {
        return INSTANCE;
    }
}
