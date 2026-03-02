package com.enderio.enderio.foundation.network;

import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.compat.curios.CuriosCompat;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.content.filters.FilterSlot;
import com.enderio.enderio.content.filters.fluid.FluidFilterSlot;
import com.enderio.enderio.content.tools.ElectromagnetItem;
import com.enderio.enderio.content.travel.TravelHandler;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.network.packets.ServerboundRequestShortTravelPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundRequestTravelPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSetFluidFilterSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSetItemFilterSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundToggleMagnetPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundUpdateCoordinateSelectionNameMenuPacket;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public class ServerPayloadHandler {
    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleCoordinateSelectionName(ServerboundUpdateCoordinateSelectionNameMenuPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                packet.getMenu(context).updateName(packet.name(), serverPlayer);
            }
        });
    }

    public void handleTravelRequest(ServerboundRequestTravelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            // For keybind anchor teleports
            ItemStack travelItemStack = TravelHandler.findValidTravelItem(player);

            Optional<TravelTarget> target = TravelTargetApi.INSTANCE.get(player.level(), packet.pos());

            boolean canBlockTeleport = TravelHandler.canBlockTeleport(player);

            // These errors should only ever be triggered if there's some form of desync
            if (!canBlockTeleport && travelItemStack.isEmpty()) {
                player.displayClientMessage(EIOCommonLang.ERROR_CANNOT_TELEPORT, true);
                return;
            }
            if (target.isEmpty()) {
                player.displayClientMessage(EIOCommonLang.ERROR_INVALID_DESTINATION, true);
                return;
            }
            // Eventually change the packet structure to include what teleport method was
            // used so this range can be selected correctly
            int range = Math.max(target.get().block2BlockRange(), target.get().item2BlockRange());
            if (packet.pos().distSqr(player.getOnPos()) > range * range) {
                player.displayClientMessage(EIOCommonLang.ERROR_TOO_FAR, true);
                return;
            }

            // Try to do teleport
            boolean successfulTeleport = TravelHandler.blockTeleportTo(player.level(), player, target.get(), false);
            if(successfulTeleport && !canBlockTeleport && !travelItemStack.isEmpty()){
                TravelHandler.consumeResources(travelItemStack);
                player.getCooldowns().addCooldown(travelItemStack.getItem(), BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
            }
        });
    }

    public void handleShortTravelRequest(ServerboundRequestShortTravelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if(player == null)
                return;

            // For keybind anchor teleports
            ItemStack travelItemStack = TravelHandler.findValidTravelItem(player);

            // These errors should only ever be triggered if there's some form of desync
            if (travelItemStack.isEmpty()) {
                player.displayClientMessage(EIOCommonLang.ERROR_CANNOT_TELEPORT, true);
                return;
            }

            if(TravelHandler.shortTeleport(player.level(), player, false)){
                TravelHandler.consumeResources(travelItemStack);
                player.getCooldowns().addCooldown(travelItemStack.getItem(), BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
            }
        });
    }

    /**
     * Toggles first magnet it finds and displays message on action bar based on new active/inactive status of magnet
     */
    public void handleMagnetToggle(ServerboundToggleMagnetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();

            Optional<List<ItemStack>> optList = CuriosCompat.getAllCuriosOnPlayer(player);
            if(optList.isPresent()){
                for(ItemStack curioStack : optList.get()){
                    if(toggleMagnet(curioStack, player)) {
                        return;
                    }
                }
            }


            for(int i = 0; i < player.getInventory().getContainerSize(); i++){
                ItemStack stack = player.getInventory().getItem(i);
                if(toggleMagnet(stack, player)) {
                    return;
                }
            }

        });
    }

    private boolean toggleMagnet(ItemStack stack, Player player) {
        if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ElectromagnetItem) {
            Boolean magnetActive = stack.getComponents().get(EIODataComponents.TOGGLED);
            if(magnetActive != null){
                if(magnetActive){
                    stack.set(EIODataComponents.TOGGLED, false);
                    player.displayClientMessage(EIOCommonLang.ELECTROMAGNET_OFF, true);
                }else{
                    stack.set(EIODataComponents.TOGGLED, true);
                    player.displayClientMessage(EIOCommonLang.ELECTROMAGNET_ON, true);
                }
                return true;
            }
        }
        return false;
    }

    public void handleSetItemFilterSlot(ServerboundSetItemFilterSlotPacket packet, IPayloadContext context) {
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

    public void handleSetFluidFilterSlot(ServerboundSetFluidFilterSlotPacket packet, IPayloadContext context) {
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
