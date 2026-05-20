package com.enderio.enderio.content.machines.capacitor_bank.rework;

import com.enderio.core.common.menu.BaseBlockEntityMenu;
import com.enderio.core.common.network.menu.EnumSyncSlot;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.foundation.io.energy.ILargeMachineEnergyStorage;
import com.enderio.enderio.foundation.io.energy.IMachineEnergyStorage;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;

public class NewCapacitorBankMenu extends BaseBlockEntityMenu<NewCapacitorBankBlockEntity> {

    private final EnumSyncSlot<RedstoneControl> redstoneControlSlot;

    public NewCapacitorBankMenu(int containerId, Inventory playerInventory, NewCapacitorBankBlockEntity blockEntity) {
        super(EIOMenus.NEW_CAPACITOR_BANK.get(), containerId, playerInventory, blockEntity);

        redstoneControlSlot = addUpdatableSyncSlot(EnumSyncSlot.simple(RedstoneControl.class,
            blockEntity::getRedstoneControl, blockEntity::setNetworkRedstoneControl));

        addPlayerInventorySlots(8, 84);
    }

    public NewCapacitorBankMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.NEW_CAPACITOR_BANK.get(), containerId, playerInventory, buf,
            EIOBlockEntities.NEW_CAPACITOR_BANKS.values().stream().map(DeferredHolder::get).toArray(BlockEntityType[]::new));

        redstoneControlSlot = addUpdatableSyncSlot(EnumSyncSlot.standalone(RedstoneControl.class));

        addPlayerInventorySlots(8, 84);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(getBlockEntity(), player);
    }

    //No inv
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public EnergyStorage getEnergyStorage() {
        CapacitorBankManager.CapacitorSyncData data = CapacitorBankManager.getData(getBlockEntity().getUuid());
        if (data == null) {
            return EnergyStorage.EMPTY;
        }
        return new EnergyStorage(data.storedEnergy(), data.capacity());
    }

    public RedstoneControl getRedstoneControl() {
        return Objects.requireNonNull(redstoneControlSlot).get();
    }

    public void setRedstoneControl(RedstoneControl redstoneControl) {
        Objects.requireNonNull(redstoneControlSlot).set(redstoneControl);
        updateSlot(redstoneControlSlot);
    }

    public record EnergyStorage(Long energyStored, Long maxEnergyStored) implements IEnergyStorage, ILargeMachineEnergyStorage {

        public static final EnergyStorage EMPTY = new EnergyStorage(0L,0L);

        @Override
        public int receiveEnergy(int i, boolean b) {
            return 0;
        }

        @Override
        public int extractEnergy(int i, boolean b) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return Math.clamp(energyStored, 0, Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return Math.clamp(maxEnergyStored, 0, Integer.MAX_VALUE);
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }

        @Override
        public long getLargeEnergyStored() {
            return energyStored;
        }

        @Override
        public long getLargeMaxEnergyStored() {
            return maxEnergyStored;
        }
    }
}
