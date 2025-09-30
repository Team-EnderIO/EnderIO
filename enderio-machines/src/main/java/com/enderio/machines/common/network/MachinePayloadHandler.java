package com.enderio.machines.common.network;

import com.enderio.machines.client.gui.screen.TransceiverScreen;
import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.enderio.machines.common.blocks.crafter.CrafterMenu;
import com.enderio.machines.common.blocks.enderface.EnderfaceBlockEntity;
import com.enderio.machines.common.network.transceiver.AddRemoveGlobalChannelPacket;
import com.enderio.machines.common.network.transceiver.AddRemoveTransceiverChannelPacket;
import com.enderio.machines.common.network.transceiver.GlobalChannelsSyncPacket;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.FarmSoul;
import com.enderio.machines.common.souldata.SolarSoul;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.enderio.machines.common.transceiver.Channel;
import com.enderio.machines.common.transceiver.ChannelSavedData;
import com.enderio.machines.common.transceiver.TransceiverBlockEntity;
import com.enderio.machines.common.transceiver.TransceiverRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

        public void handleChannelsSync(GlobalChannelsSyncPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (Minecraft.getInstance().screen instanceof TransceiverScreen screen) {
                    screen.getMenu().setChannelList(packet.channels());
                }
            });
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

        public void handleAddRemoveGlobalChannel(AddRemoveGlobalChannelPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                Level level = context.player().level();
                Channel channel = packet.channel();

                if (packet.isAdd()) {
                    ChannelSavedData data = ChannelSavedData.get(level);
                    data.addChannel(channel);
                } else {
                    ChannelSavedData data = ChannelSavedData.get(level);
                    data.removeChannel(channel);

                    // Delete all of existing instances of this channel in loaded transceivers
                    TransceiverRegistry.INSTANCE.deleteChannel(channel);
                }
            });
        }

        public void handleAddRemoveTransceiverChannel(AddRemoveTransceiverChannelPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                Level level = context.player().level();

                BlockPos pos = packet.pos();
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity instanceof TransceiverBlockEntity transceiverBlockEntity) {
                    Channel channel = packet.channel();

                    if (packet.isAdd()) {
                        if (packet.isSend()) {
                            transceiverBlockEntity.addSendChannel(channel);
                        }
                        if (packet.isReceive()) {
                            transceiverBlockEntity.addReceiveChannel(channel);
                        }
                    } else {
                        if (packet.isSend()) {
                            transceiverBlockEntity.deleteSendChannel(channel);
                        }
                        if (packet.isReceive()) {
                            transceiverBlockEntity.deleteReceiveChannel(channel);
                        }
                    }

                    transceiverBlockEntity.setChanged();
                    level.sendBlockUpdated(pos, transceiverBlockEntity.getBlockState(), transceiverBlockEntity.getBlockState(), Block.UPDATE_ALL);
                }
            });
        }
    }
}
