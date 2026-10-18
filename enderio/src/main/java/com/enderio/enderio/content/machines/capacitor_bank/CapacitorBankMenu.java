package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.core.common.menu.BaseBlockEntityMenu;
import com.enderio.core.common.network.menu.EnumSyncSlot;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.foundation.energy.EnergyStorageInfo;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;

public class CapacitorBankMenu extends BaseBlockEntityMenu<CapacitorBankBlockEntity> {

    private final EnumSyncSlot<RedstoneControl> redstoneControlSlot;

    public CapacitorBankMenu(int containerId, Inventory playerInventory, CapacitorBankBlockEntity blockEntity) {
        super(EIOMenus.CAPACITOR_BANK.get(), containerId, playerInventory, blockEntity);

        redstoneControlSlot = addUpdatableSyncSlot(EnumSyncSlot.simple(RedstoneControl.class,
            blockEntity::getRedstoneControl, blockEntity::setNetworkRedstoneControl));

        addPlayerInventorySlots(8, 84);
    }

    public CapacitorBankMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.CAPACITOR_BANK.get(), containerId, playerInventory, buf,
            EIOBlockEntities.CAPACITOR_BANKS.values().stream().map(DeferredHolder::get).toArray(BlockEntityType[]::new));

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

    public EnergyStorageInfo getEnergyStorage() {
        CapacitorBankManager.CapacitorSyncData data = CapacitorBankManager.getData(getBlockEntity().getUuid());
        if (data == null) {
            return new EnergyStorageInfo(0L, 0L);
        }
        return new EnergyStorageInfo(data.storedEnergy(), data.capacity());
    }

    public RedstoneControl getRedstoneControl() {
        return Objects.requireNonNull(redstoneControlSlot).get();
    }

    public void setRedstoneControl(RedstoneControl redstoneControl) {
        Objects.requireNonNull(redstoneControlSlot).set(redstoneControl);
        updateSlot(redstoneControlSlot);
    }
}
