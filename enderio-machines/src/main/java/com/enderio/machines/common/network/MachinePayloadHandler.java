package com.enderio.machines.common.network;

import com.enderio.machines.common.blocks.crafter.CrafterMenu;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.SolarSoul;
import com.enderio.machines.common.souldata.SpawnerSoul;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MachinePayloadHandler {
    public static class Client {
        private static final Client INSTANCE = new Client();

        public static Client getInstance() {
            return INSTANCE;
        }

        public void handlePoweredSpawnerSoul(PoweredSpawnerSoulPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> SpawnerSoul.SPAWNER.map = packet.map());
        }

        public void handleSoulEngineSoul(SoulEngineSoulPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> EngineSoul.ENGINE.map = packet.map());
        }

        public void handleSolarSoul(SolarSoulPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> SolarSoul.SOLAR.map = packet.map());
        }
    }

    public static class Server {
        private static final Server INSTANCE = new Server();

        public static Server getInstance() {
            return INSTANCE;
        }

        public void updateCrafterTemplate(UpdateCrafterTemplatePacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player().containerMenu instanceof CrafterMenu crafterMenu) {
                    for (int i = 0; i < packet.recipeInputs().size(); i++) {
                        crafterMenu.slots.get(CrafterMenu.INPUTS_INDEX + i).set(packet.recipeInputs().get(i));
                    }
                }
            });
        }
    }
}
