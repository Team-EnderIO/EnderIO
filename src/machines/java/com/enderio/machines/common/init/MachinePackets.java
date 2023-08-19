package com.enderio.machines.common.init;

import com.enderio.base.common.network.SyncTravelDataPacket;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.machines.common.network.PoweredSpawnerSoulPacket;
import com.enderio.machines.common.network.UpdateCrafterTemplatePacket;
import com.enderio.machines.common.souldata.SpawnerSoul;

public class MachinePackets {
    public static void register() {
        //Sync soul data (optional)
        SpawnerSoul.SPAWNER.subscribeAsSyncable(PoweredSpawnerSoulPacket::new);

        //packets
        CoreNetwork.registerPacket(new UpdateCrafterTemplatePacket.Handler(), UpdateCrafterTemplatePacket.class);
        CoreNetwork.registerPacket(new PoweredSpawnerSoulPacket.Handler(), PoweredSpawnerSoulPacket.class);
    }
}
