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
import net.minecraft.nbt.NbtOps;
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

    public ChannelList getSendChannels() {
        return sendChannels;
    }

    public ChannelList getReceiveChannels() {
        return receiveChannels;
    }


    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);

        var ctx = lookupProvider.createSerializationContext(NbtOps.INSTANCE);

        ChannelList.CODEC.encodeStart(ctx, this.sendChannels).ifSuccess(tag -> pTag.put(SEND_CHANNELS_TAG, tag));
        ChannelList.CODEC.encodeStart(ctx, this.receiveChannels).ifSuccess(tag -> pTag.put(RECEIVE_CHANNELS_TAG, tag));
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);

        var ctx = lookupProvider.createSerializationContext(NbtOps.INSTANCE);

        if (pTag.contains(SEND_CHANNELS_TAG)) {
            ChannelList.CODEC.parse(ctx, pTag.get(SEND_CHANNELS_TAG)).ifSuccess(list -> sendChannels = list);
        }

        if (pTag.contains(RECEIVE_CHANNELS_TAG)) {
            ChannelList.CODEC.parse(ctx, pTag.get(RECEIVE_CHANNELS_TAG)).ifSuccess(list -> receiveChannels = list);
        }
    }
    // endregion
}
