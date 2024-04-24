package com.enderio.base.common.network;

import com.enderio.api.travel.ITravelTarget;
import com.enderio.base.common.handler.TravelHandler;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.capability.IFilterCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class ServerPayloadHandler {
    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleCoordinateSelectionName(UpdateCoordinateSelectionNameMenuPacket packet, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                context.player().ifPresent(player -> {
                    if (player instanceof ServerPlayer serverPlayer) {
                        packet.getMenu(context).updateName(packet.getName(), serverPlayer);
                    }
                });
            });
    }

    public void handleTravelRequest(RequestTravelPacket packet, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                var player = context.player().orElse(null);

                if (player == null) {
                    return;
                }

                TravelSavedData travelData = TravelSavedData.getTravelData(player.level());
                Optional<ITravelTarget> target = travelData.getTravelTarget(packet.pos());

                // These errors should only ever be triggered if there's some form of desync
                if (!TravelHandler.canBlockTeleport(player)) {
                    player.displayClientMessage(Component.nullToEmpty("ERROR: Cannot teleport"), true);
                    return;
                }
                if (target.isEmpty()) {
                    player.displayClientMessage(Component.nullToEmpty("ERROR: Destination not a valid target"), true);
                    return;
                }
                // Eventually change the packet structure to include what teleport method was used so this range can be selected correctly
                int range = Math.max(target.get().getBlock2BlockRange(), target.get().getItem2BlockRange());
                if (packet.pos().distSqr(player.getOnPos()) > range * range) {
                    player.displayClientMessage(Component.nullToEmpty("ERROR: Too far"), true);
                    return;
                }

                TravelHandler.blockTeleportTo(player.level(), player, target.get(), false);
            });
    }

    public void handleFilterUpdate(FilterUpdatePacket packet, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                context.player().ifPresent(player -> {
                    IFilterCapability capability = player.getMainHandItem().getCapability(EIOCapabilities.Filter.ITEM);
                    if (capability != null) {
                        capability.setNbt(packet.nbt());
                        capability.setInverted(packet.inverted());
                    }
                });
            });
    }
}
