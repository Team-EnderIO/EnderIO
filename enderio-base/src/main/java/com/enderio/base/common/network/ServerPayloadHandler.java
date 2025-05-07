package com.enderio.base.common.network;

import com.enderio.base.api.travel.TravelTarget;
import com.enderio.base.api.travel.TravelTargetApi;
import com.enderio.base.common.handler.TravelHandler;
import com.enderio.base.common.filter.FilterSlot;
import com.enderio.base.common.filter.fluid.FluidFilterSlot;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleCoordinateSelectionName(UpdateCoordinateSelectionNameMenuPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                packet.getMenu(context).updateName(packet.name(), serverPlayer);
            }
        });
    }

    public void handleTravelRequest(RequestTravelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();

            Optional<TravelTarget> target = TravelTargetApi.INSTANCE.get(player.level(), packet.pos());

            // These errors should only ever be triggered if there's some form of desync
            if (!TravelHandler.canBlockTeleport(player)) {
                player.displayClientMessage(Component.nullToEmpty("ERROR: Cannot teleport"), true);
                return;
            }
            if (target.isEmpty()) {
                player.displayClientMessage(Component.nullToEmpty("ERROR: Destination not a valid target"), true);
                return;
            }
            // Eventually change the packet structure to include what teleport method was
            // used so this range can be selected correctly
            int range = Math.max(target.get().block2BlockRange(), target.get().item2BlockRange());
            if (packet.pos().distSqr(player.getOnPos()) > range * range) {
                player.displayClientMessage(Component.nullToEmpty("ERROR: Too far"), true);
                return;
            }

            TravelHandler.blockTeleportTo(player.level(), player, target.get(), false);
        });
    }

    public void handleSetItemFilterSlot(C2SSetItemFilterSlot packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var currentMenu = context.player().containerMenu;

            if (currentMenu == null || currentMenu.containerId != packet.containerId()
                    || currentMenu.slots.size() <= packet.slotIndex()) {
                return;
            }

            if (currentMenu.getSlot(packet.slotIndex()) instanceof FilterSlot<?> filterSlot) {
                filterSlot.safeInsert(packet.itemStack());
            }
        });
    }

    public void handleSetFluidFilterSlot(C2SSetFluidFilterSlot packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var currentMenu = context.player().containerMenu;

            if (currentMenu == null || currentMenu.containerId != packet.containerId()
                    || currentMenu.slots.size() <= packet.slotIndex()) {
                return;
            }

            if (currentMenu.getSlot(packet.slotIndex()) instanceof FluidFilterSlot filterSlot) {
                filterSlot.setResource(packet.fluidStack());
            }
        });
    }
}
