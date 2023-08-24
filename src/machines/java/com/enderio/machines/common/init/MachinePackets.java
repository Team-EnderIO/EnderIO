package com.enderio.machines.common.init;

import com.enderio.core.common.network.CoreNetwork;
import com.enderio.machines.common.network.PlayerExperiencePacket;
import com.enderio.machines.common.network.PoweredSpawnerSoulPacket;
import com.enderio.machines.common.network.SoulEngineSoulPacket;
import com.enderio.machines.common.network.UpdateCrafterTemplatePacket;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.SpawnerSoul;

public class MachinePackets {
    public static void register() {
        //Sync soul data (optional)
        SpawnerSoul.SPAWNER.subscribeAsSyncable(PoweredSpawnerSoulPacket::new);
        EngineSoul.ENGINE.subscribeAsSyncable(SoulEngineSoulPacket::new);

        //packets
        CoreNetwork.registerPacket(new UpdateCrafterTemplatePacket.Handler(), UpdateCrafterTemplatePacket.class);
        CoreNetwork.registerPacket(new PoweredSpawnerSoulPacket.Handler(), PoweredSpawnerSoulPacket.class);
        CoreNetwork.registerPacket(new SoulEngineSoulPacket.Handler(), SoulEngineSoulPacket.class);
        CoreNetwork.registerPacket(new PlayerExperiencePacket.Handler(), PlayerExperiencePacket.class);
    }
}
