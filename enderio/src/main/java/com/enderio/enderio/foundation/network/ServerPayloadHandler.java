package com.enderio.enderio.foundation.network;

import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.content.filters.FilterSlot;
import com.enderio.enderio.content.filters.fluid.FluidFilterSlot;
import com.enderio.enderio.content.tools.ElectromagnetItem;
import com.enderio.enderio.content.tools.PoweredToggledItem;
import com.enderio.enderio.content.travel.TravelHandler;
import com.enderio.enderio.foundation.network.packets.ServerboundRequestShortTravelPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundRequestTravelPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSetFluidFilterSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSetItemFilterSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundToggleMagnetPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundUpdateCoordinateSelectionNameMenuPacket;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;
import java.util.stream.Collectors;

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

            if(!canBlockTeleport && !travelItemStack.isEmpty()){
                TravelHandler.consumeResources(travelItemStack);
                player.getCooldowns().addCooldown(travelItemStack.getItem(), BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
            }
            TravelHandler.blockTeleportTo(player.level(), player, target.get(), false);
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
                player.displayClientMessage(Component.nullToEmpty("ERROR: Cannot teleport"), true);
                return;
            }

            TravelHandler.consumeResources(travelItemStack);

            if(TravelHandler.shortTeleport(player.level(), player, false))
                player.getCooldowns().addCooldown(travelItemStack.getItem(), BaseConfig.COMMON.ITEMS.TRAVELLING_BLINK_DISABLED_TIME.get());
        });
    }

    /**
     * Toggles first magnet it finds and displays message on action bar based on new active/inactive status of magnet
     */
    public void handleMagnetToggle(ServerboundToggleMagnetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();

            if(ModList.get().isLoaded("curios")) {
                ICuriosItemHandler curiosItemHandler = player.getCapability(CuriosCapability.INVENTORY);
                if(curiosItemHandler != null){
                    for(IDynamicStackHandler curiosStackHandler : curiosItemHandler.getCurios().values().stream().map(ICurioStacksHandler::getStacks).collect(
                        Collectors.toSet())){
                        for(int slot = 0; slot < curiosStackHandler.getSlots(); slot++){
                            ItemStack stack = curiosStackHandler.getStackInSlot(slot);
                            if(toggleMagnetAndDisplayToPlayer(stack, player)) {
                                return;
                            }
                        }
                    }
                }
            }

            for(int i = 0; i < player.getInventory().getContainerSize(); i++){
                ItemStack stack = player.getInventory().getItem(i);
                if(toggleMagnetAndDisplayToPlayer(stack, player)) {
                    return;
                }
            }

        });
    }

    private boolean toggleMagnetAndDisplayToPlayer(ItemStack stack, Player player) {
        if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ElectromagnetItem) {
            Boolean magnetActive = stack.getComponents().get(EIODataComponents.TOGGLED);
            if(magnetActive != null){
                if(magnetActive){
                    stack.set(EIODataComponents.TOGGLED, false);
                    player.displayClientMessage(Component.nullToEmpty("Electromagnet Off"), true);
                }else{
                    stack.set(EIODataComponents.TOGGLED, true);
                    player.displayClientMessage(Component.nullToEmpty("Electromagnet On"), true);
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
