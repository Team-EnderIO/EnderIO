package com.enderio.enderio.content.machines.wired_charger;

import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class WiredChargerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_USAGE);

    public static final SingleSlotAccess ITEM_TO_CHARGE = new SingleSlotAccess();
    public static final SingleSlotAccess ITEM_CHARGED = new SingleSlotAccess();

    private float progress = 0;

    public WiredChargerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.WIRED_CHARGER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .capacitor()
                .inputSlot((slot, resource) -> acceptItem(resource.toStack()))
                .slotAccess(ITEM_TO_CHARGE)
                .outputSlot()
                .slotAccess(ITEM_CHARGED)
                .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player pPlayer) {
        return new WiredChargerMenu(containerId, playerInventory, this);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (isActive()) {
            chargeItem();
        } else {
            this.progress = 0;
        }
    }

    @Override
    public boolean isActive() {
        return hasEnergy() && canAct();
    }

    public boolean acceptItem(ItemStack stack) {
        return stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack)) != null;
    }

    @Override
    public boolean canAct() {
        ItemStack inputItem = ITEM_TO_CHARGE.getItemStack(this);
        ItemStack outputItem = ITEM_CHARGED.getItemStack(this);
        return !inputItem.isEmpty() && outputItem.isEmpty() && acceptItem(inputItem) && super.canAct();
    }

    public void chargeItem() {
        ItemStack chargeable = ITEM_TO_CHARGE.getItemStack(this);
        EnergyHandler itemEnergyHandler = chargeable.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(chargeable));

        if (itemEnergyHandler != null) {
            if (itemEnergyHandler.getAmountAsInt() == itemEnergyHandler.getCapacityAsInt()) {
                ITEM_CHARGED.setStackInSlot(this, chargeable);
                ITEM_TO_CHARGE.setStackInSlot(this, ItemStack.EMPTY);
            } else {
                int energyToInsert = Math.min(
                        itemEnergyHandler.getCapacityAsInt() - itemEnergyHandler.getAmountAsInt(),
                        Math.max(getEnergyStorage().getCapacityAsInt(), getEnergyStorage().getMaxConsumption()));

                if (energyToInsert > 0) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        int maxConsumed;
                        try (Transaction simulatedExtract = Transaction.open(transaction)) {
                            maxConsumed = getEnergyStorage().consume(energyToInsert, simulatedExtract);
                        }

                        int inserted = itemEnergyHandler.insert(maxConsumed, transaction);
                        if (inserted == getEnergyStorage().consume(inserted, transaction)) {
                            transaction.commit();
                        }
                    }

                    // Update progress
                    this.progress = (float) itemEnergyHandler.getAmountAsInt()
                        / itemEnergyHandler.getCapacityAsInt();
                }
            }
        }
    }

    public float getChargeProgress() {
        return this.progress;
    }
}
