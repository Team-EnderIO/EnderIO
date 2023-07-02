package com.enderio.machines.common.network;

import com.enderio.core.common.network.CoreNetwork;
import com.enderio.machines.common.souldata.SpawnerSoul;

public class MachineNetwork {

    public static void networkInit() {
        //Sync soul data (optional)
        SpawnerSoul.SPAWNER.subscribeAsSyncable(PoweredSpawnerSoulPacket::new);

        //packets
        CoreNetwork.registerPacket(new PoweredSpawnerSoulPacket.Handler(), PoweredSpawnerSoulPacket.class);
    }
}
