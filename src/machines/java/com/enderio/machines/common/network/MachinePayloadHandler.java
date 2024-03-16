package com.enderio.machines.common.network;

import com.enderio.machines.common.menu.CrafterMenu;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.FarmSoul;
import com.enderio.machines.common.souldata.SpawnerSoul;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MachinePayloadHandler {
    public static class Client {
        private static final Client INSTANCE = new Client();

        public static Client getInstance() {
            return INSTANCE;
        }

        public void handlePoweredSpawnerSoul(PoweredSpawnerSoulPacket packet, PlayPayloadContext context) {
            context.workHandler()
                .submitAsync(() -> SpawnerSoul.SPAWNER.map = packet.map());
        }

        public void handleSoulEngineSoul(SoulEngineSoulPacket packet, PlayPayloadContext context) {
            context.workHandler()
                .submitAsync(() -> EngineSoul.ENGINE.map = packet.map());
        }

        public void handleFarmingStationSoul(FarmStationSoulPacket packet, PlayPayloadContext context) {
            context.workHandler()
                .submitAsync(() -> FarmSoul.FARM.map = packet.map());
        }
    }

    public static class Server {
        private static final Server INSTANCE = new Server();

        public static Server getInstance() {
            return INSTANCE;
        }

        public void updateCrafterTemplate(UpdateCrafterTemplatePacket packet, PlayPayloadContext context) {
            context.workHandler()
                .submitAsync(() -> {
                    context.player().ifPresent(player -> {
                        if (player.containerMenu instanceof CrafterMenu crafterMenu) {
                            for (int i = 0; i < packet.recipeInputs().size(); i++) {
                                crafterMenu.slots.get(CrafterMenu.INPUTS_INDEX + i).set(packet.recipeInputs().get(i));
                            }
                        }
                    });
                });
        }
    }
}
