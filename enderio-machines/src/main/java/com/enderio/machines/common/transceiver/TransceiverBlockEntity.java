package com.enderio.machines.common.transceiver;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TransceiverBlockEntity extends PoweredMachineBlockEntity {

    public static final MultiSlotAccess INPUTS = new MultiSlotAccess();
    public static final MultiSlotAccess OUTPUTS = new MultiSlotAccess();

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
        MachinesConfig.COMMON.ENERGY.TRANSCEIVER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
        MachinesConfig.COMMON.ENERGY.TRANSCEIVER_USAGE);

    public ChannelList sendChannels = new ChannelList();
    public ChannelList receiveChannels = new ChannelList();
    
    private static final String SEND_CHANNELS_TAG = "send_channels";
    private static final String RECEIVE_CHANNELS_TAG = "receive_channels";

    private boolean isRegistered = false;

    //todo fluid


    public TransceiverBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.TRANSCEIVER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, EnergyIOMode.Input, CAPACITY, USAGE);
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot(8)
            .slotAccess(INPUTS)
            .outputSlot(8)
            .slotAccess(OUTPUTS)
            .capacitor()
            .build();
    }
    @Override
    public boolean isActive() {
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player pPlayer) {
        return new TransceiverMenu(containerId, playerInventory, this);
    }

    @Override
    public void serverTick() {
        if (canAct()) {
            for (Channel channel : sendChannels.getChannels(ChannelType.ENERGY)) {
                TransceiverRegistry.INSTANCE.sendPower(this, 100, channel);
            }

            for (Channel channel : sendChannels.getChannels(ChannelType.ITEM)) {
                TransceiverRegistry.INSTANCE.sendItems(this, channel);
            }
        }
    }

    public ChannelList getSendChannels() {
        return sendChannels;
    }

    public ChannelList getReceiveChannels() {
        return receiveChannels;
    }

    public void addSendChannel(Channel channel) {
        sendChannels.addChannel(channel);
        TransceiverRegistry.INSTANCE.addSubscription(channel, this);
        setChanged();
    }

    public void deleteSendChannel(Channel channel) {
        sendChannels.removeChannel(channel);
        TransceiverRegistry.INSTANCE.deleteSubscription(channel, this);
        setChanged();
    }

    public void addReceiveChannel(Channel channel) {
        receiveChannels.addChannel(channel);
        TransceiverRegistry.INSTANCE.addSubscription(channel, this);
        setChanged();
    }

    public void deleteReceiveChannel(Channel channel) {
        receiveChannels.removeChannel(channel);
        TransceiverRegistry.INSTANCE.deleteSubscription(channel, this);
        setChanged();
    }


    @Override
    public void setRemoved() {
        if (isRegistered) {
            TransceiverRegistry.INSTANCE.unregister(this);
        }

        super.setRemoved();
    }


    @Override
    public void onChunkUnloaded() {
        if (isRegistered) {
            TransceiverRegistry.INSTANCE.unregister(this);
        }

        super.onChunkUnloaded();
    }


    // region Serialization


    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);

        pTag.put(SEND_CHANNELS_TAG, this.sendChannels.save(lookupProvider));
        pTag.put(RECEIVE_CHANNELS_TAG, this.receiveChannels.save(lookupProvider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);

        if (tag.contains(SEND_CHANNELS_TAG, Tag.TAG_COMPOUND)) {
            Tag sendChannelsTag = tag.get(SEND_CHANNELS_TAG);
            if (sendChannelsTag != null) {
                this.sendChannels = ChannelList.parse(lookupProvider, sendChannelsTag);
            }
        }

        if (tag.contains(RECEIVE_CHANNELS_TAG, Tag.TAG_COMPOUND)) {
            Tag receiveChannelsTag = tag.get(RECEIVE_CHANNELS_TAG);
            if (receiveChannelsTag != null) {
                this.receiveChannels = ChannelList.parse(lookupProvider, receiveChannelsTag);
            }
        }

        if (!isRegistered) {
            TransceiverRegistry.INSTANCE.register(this);
            isRegistered = true;
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = super.getUpdateTag(lookupProvider);
        tag.put(SEND_CHANNELS_TAG, sendChannels.save(lookupProvider));
        tag.put(RECEIVE_CHANNELS_TAG, receiveChannels.save(lookupProvider));

        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);

        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            if (tag.contains(SEND_CHANNELS_TAG, Tag.TAG_COMPOUND)) {
                Tag sendChannelsSubTag = tag.get(SEND_CHANNELS_TAG);
                if (sendChannelsSubTag != null) {
                    this.sendChannels = ChannelList.parse(lookupProvider, sendChannelsSubTag);
                }
            }

            if (tag.contains(RECEIVE_CHANNELS_TAG, Tag.TAG_COMPOUND)) {
                Tag receiveChannelsSubTag = tag.get(RECEIVE_CHANNELS_TAG);
                if (receiveChannelsSubTag != null) {
                    this.receiveChannels = ChannelList.parse(lookupProvider, receiveChannelsSubTag);
                }
            }
        }
    }
}


