package com.enderio.conduits.common.network;

import com.enderio.conduits.common.conduit.menu.ConduitMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ConduitCommonPayloadHandler {
    private static final ConduitCommonPayloadHandler INSTANCE = new ConduitCommonPayloadHandler();

    public void handle(SetConduitConnectionConfigPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.containerId() == context.player().containerMenu.containerId) {
                if (!context.player().isSpectator() && context.player().containerMenu instanceof ConduitMenu menu) {
                    menu.handleConnectionConfigUpdate(packet);
                }
            }
        });
    }

    public static ConduitCommonPayloadHandler getInstance() {
        return INSTANCE;
    }
}
