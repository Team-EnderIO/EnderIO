package com.enderio.machines.common.transceiver;

import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.network.transceiver.ChannelsSyncPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class TransceiverMenu extends PoweredMachineMenu<TransceiverBlockEntity> {
    private TransceiverBlockEntity blockEntity;
    private ChannelList channelList = new ChannelList();
    private boolean isPrivate = false;
    private boolean needChannelSync = true;

    private ChannelType selectedType;

    public TransceiverMenu(int pContainerId, Inventory inventory, TransceiverBlockEntity blockEntity) {
        super(MachineMenus.TRANSCEIVER.get(), pContainerId, inventory, blockEntity);
        this.blockEntity = blockEntity;

        addSlots();
    }

    public TransceiverMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.TRANSCEIVER.get(), containerId, playerInventory, buf, MachineBlockEntities.TRANSCEIVER.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(8, 75);

        addInputSlots();
        addOutputSlots();

        addPlayerInventorySlots(47, 86);
    }

    private void addInputSlots() {
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 4; x++) {
                int index = x + y * 4;
                addSlot(new MachineSlot(getMachineInventory(), TransceiverBlockEntity.INPUTS.get(index), 54 + x * 18, 30 + y * 18));
            }
        }
    }

    private void addOutputSlots() {
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 4; x++) {
                int index = x + y * 4;
                addSlot(new MachineSlot(getMachineInventory(), TransceiverBlockEntity.OUTPUTS.get(index), 131 + x * 18, 30 + y * 18));
            }
        }
    }

    public ChannelList getChannelList() {
        return channelList;
    }

    public void setChannelList(ChannelList channelList) {
        this.channelList = channelList;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (needChannelSync) {
            ChannelList serverChannels = ChannelSavedData.get(Objects.requireNonNull(blockEntity.getLevel())).getChannelList();
            PacketDistributor.sendToPlayer((ServerPlayer) getPlayerInventory().player, new ChannelsSyncPacket(serverChannels));

            needChannelSync = false;
        }
    }

    public Supplier<Boolean> isPrivate() {
        return () -> isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Nullable
    public ChannelType getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(ChannelType selectedType) {
        this.selectedType = selectedType;
    }

}
