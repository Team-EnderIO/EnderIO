package com.enderio.machines.common.transceiver;

import com.enderio.base.api.collections.RoundRobinList;
import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum TransceiverRegistry {
    INSTANCE;

    private final Map<Channel, RoundRobinList<TransceiverBlockEntity>> energyChannels = new ConcurrentHashMap<>();
    private final Map<Channel, RoundRobinList<TransceiverBlockEntity>> fluidChannels = new ConcurrentHashMap<>();
    private final Map<Channel, RoundRobinList<TransceiverBlockEntity>> itemChannels = new ConcurrentHashMap<>();

    private final Set<TransceiverBlockEntity> activeTransceivers = ConcurrentHashMap.newKeySet();

    public void register(TransceiverBlockEntity transceiver) {
        if (activeTransceivers.add(transceiver)) {
            registerForChannels(transceiver.getSendChannels().get(ChannelType.ENERGY), energyChannels, transceiver);
            registerForChannels(transceiver.getReceiveChannels().get(ChannelType.ENERGY), energyChannels, transceiver);

            registerForChannels(transceiver.getSendChannels().get(ChannelType.FLUID), fluidChannels, transceiver);
            registerForChannels(transceiver.getReceiveChannels().get(ChannelType.FLUID), fluidChannels, transceiver);

            registerForChannels(transceiver.getSendChannels().get(ChannelType.ITEM), itemChannels, transceiver);
            registerForChannels(transceiver.getReceiveChannels().get(ChannelType.ITEM), itemChannels, transceiver);
        }
    }

    public void unregister(TransceiverBlockEntity transceiver) {
        if (activeTransceivers.remove(transceiver)) {
            removeFromAllChannels(energyChannels, transceiver);
            removeFromAllChannels(fluidChannels, transceiver);
            removeFromAllChannels(itemChannels, transceiver);
        }
    }

    private void registerForChannels(Set<Channel> channels, Map<Channel, RoundRobinList<TransceiverBlockEntity>> channelMap, TransceiverBlockEntity transceiver) {
        for (Channel channel : channels) {
            channelMap.computeIfAbsent(channel, k -> new RoundRobinList<>()).add(transceiver);
        }
    }

    private void removeFromAllChannels(Map<Channel, RoundRobinList<TransceiverBlockEntity>> channelMap, TransceiverBlockEntity transceiver) {
        channelMap.entrySet().removeIf(entry -> {
            entry.getValue().remove(transceiver);
            return entry.getValue().isEmpty();
        });
    }

    public void sendPower(TransceiverBlockEntity sender, int amount, Channel channel) {
        if (!sender.canAct()) return;

        RoundRobinList<TransceiverBlockEntity> receivers = energyChannels.get(channel);
        if (receivers == null) return;

        for (TransceiverBlockEntity receiver : receivers.iterate()) {
            if (isValidReceiver(receiver, sender, channel, ChannelType.ENERGY)) {
                int received = receiver.getEnergyStorage().receiveEnergy(amount, true);
                if (received > 0) {
                    receiver.getEnergyStorage().receiveEnergy(amount, false);
                    sender.getEnergyStorage().consumeEnergy(amount);
                    return;
                }
            }
        }
    }


    public void sendItems(TransceiverBlockEntity sender, Channel channel) {
        RoundRobinList<TransceiverBlockEntity> receivers = itemChannels.get(channel);
        MachineInventory senderInventory = sender.getInventory();

        for (TransceiverBlockEntity receiver : receivers.iterate()) {
            if (isValidReceiver(receiver, sender, channel, ChannelType.ITEM)) {
                MachineInventory receiverInventory = receiver.getInventory();
                transferItems(senderInventory, receiverInventory);

                return;
            }
        }

    }

    private void transferItems(MachineInventory senderInventory, MachineInventory receiverInventory) {
        for (SingleSlotAccess inputSlot : TransceiverBlockEntity.INPUTS.getAccesses()) {
            ItemStack stackInSenderSlot = inputSlot.getItemStack(senderInventory);

            if (!stackInSenderSlot.isEmpty()) {
                ItemStack stackToTransfer = stackInSenderSlot.copy();
                int transferAmount = Math.min(stackToTransfer.getCount(), 64);
                stackToTransfer.setCount(transferAmount);

                int insertedAmount = 0;
                ItemStack remainingStack = stackToTransfer.copy();

                for (SingleSlotAccess outputSlot : TransceiverBlockEntity.OUTPUTS.getAccesses()) {
                    if (remainingStack.isEmpty()) break;
                    ItemStack simulatedRemaining = outputSlot.insertItem(receiverInventory, remainingStack, true);

                    int currentInserted = remainingStack.getCount() - simulatedRemaining.getCount();
                    insertedAmount += currentInserted;
                    remainingStack = simulatedRemaining;

                    if (insertedAmount > 0) {
                        ItemStack extractedStack = stackInSenderSlot.copy();
                        extractedStack.setCount(stackInSenderSlot.getCount() - currentInserted);
                        inputSlot.setStackInSlot(senderInventory, extractedStack);


                        ItemStack actualInsertStack = stackToTransfer.copyWithCount(currentInserted);
                        outputSlot.insertItem(receiverInventory, actualInsertStack, false);

                        return;
                    }
                }
            }
        }
    }

    private boolean isValidReceiver(TransceiverBlockEntity receiver, TransceiverBlockEntity sender, Channel channel, ChannelType type) {
        return receiver != sender
            && !receiver.isRemoved()
            && receiver.getReceiveChannels().get(type).contains(channel);
    }

    public void addSubscription(Channel channel, TransceiverBlockEntity transceiver) {
        ChannelType type = channel.type();
        var channelMap = getChannelMapForType(type);

        channelMap.computeIfAbsent(channel, k -> new RoundRobinList<>()).add(transceiver);
    }

    public void deleteSubscription(Channel channel, TransceiverBlockEntity transceiver) {
        ChannelType type = channel.type();
        var channelMap = getChannelMapForType(type);

        RoundRobinList<TransceiverBlockEntity> list = channelMap.get(channel);
        if (list != null) {
            list.remove(transceiver);

            if (list.isEmpty()) {
                channelMap.remove(channel);
            }
        }
    }


    public void deleteChannel(Channel channel) {
        ChannelType type = channel.type();
        var channelMap = getChannelMapForType(type);

        for (TransceiverBlockEntity transceiver : channelMap.get(channel).iterate()) {
            transceiver.deleteSendChannel(channel);
            transceiver.deleteReceiveChannel(channel);
        }

        channelMap.remove(channel);
    }


    private Map<Channel, RoundRobinList<TransceiverBlockEntity>> getChannelMapForType(ChannelType type) {
        return switch (type) {
            case ITEM -> itemChannels;
            case FLUID -> fluidChannels;
            case ENERGY -> energyChannels;
        };
    }
}
