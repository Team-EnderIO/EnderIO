package com.enderio.machines.common.network;

import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.enderio.machines.common.blocks.crafter.CrafterMenu;
import com.enderio.machines.common.blocks.enderface.EnderfaceBlockEntity;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.FarmSoul;
import com.enderio.machines.common.souldata.SolarSoul;
import com.enderio.machines.common.souldata.SpawnerSoul;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
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

        public void handleFarmingStationSoul(FarmStationSoulPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> FarmSoul.FARM.map = packet.map());
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

        public void handleCycleIOConfigPacket(CycleIOConfigPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                var level = context.player().level();
                BlockEntity be = level.getBlockEntity(packet.pos());

                if (be instanceof MachineBlockEntity machineBlockEntity) {
                    machineBlockEntity.cycleIOMode(packet.side());
                }
            });
        }

        public void handleEnderfaceInteract(EnderfaceInteractPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                var pos = packet.getHitResult().getBlockPos();
                var level = context.player().level();
                if (EnderfaceBlockEntity.canPlayerInteractWithBlock(context.player(), level, pos)) {
                    var state = level.getBlockState(pos);
                    state.useWithoutItem(level, context.player(), packet.getHitResult());
                }
            });
        }

        public void handleTransferItems(TransferItemsPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                AbstractContainerMenu menu = context.player().containerMenu;
                for (int i = packet.endslot(); i < menu.slots.size(); i++) {
                    for (int j = packet.startslot(); j < packet.endslot(); j++) {
                        int relative = j - packet.startslot();
                        Slot recipeSlot = menu.getSlot(j);
                        Slot invSlot = menu.getSlot(i);

                        if (recipeSlot.getItem().isEmpty()) {
                            continue;
                        }

                        if ((recipeSlot.getItem().isEmpty() && packet.stacks().get(relative).test(invSlot.getItem()))
                                || ItemStack.isSameItemSameComponents(invSlot.getItem(), recipeSlot.getItem())) {
                            if (packet.maxTransfer()) {
                                int toTransfer = invSlot.getItem().getMaxStackSize() - recipeSlot.getItem().getCount();
                                int actual = Math.min(invSlot.getItem().getCount(), toTransfer);

                                if (actual == 0) {
                                    break;
                                }

                                recipeSlot.set(invSlot.getItem().copyWithCount(actual +  recipeSlot.getItem().getCount()));
                                invSlot.getItem().shrink(actual);

                                if (actual == toTransfer) {
                                    break;
                                }
                            } else if (recipeSlot.getItem().isEmpty()) {
                                recipeSlot.set(invSlot.getItem().copyWithCount(1));
                                invSlot.getItem().shrink(1);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }
}
