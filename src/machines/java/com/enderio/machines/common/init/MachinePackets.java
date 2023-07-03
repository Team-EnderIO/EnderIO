package com.enderio.machines.common.init;

import com.enderio.core.common.network.CoreNetwork;
import com.enderio.machines.common.network.UpdateCrafterTemplatePacket;

public class MachinePackets {
    public static void register() {
        CoreNetwork.registerPacket(new UpdateCrafterTemplatePacket.Handler(), UpdateCrafterTemplatePacket.class);
    }
}
