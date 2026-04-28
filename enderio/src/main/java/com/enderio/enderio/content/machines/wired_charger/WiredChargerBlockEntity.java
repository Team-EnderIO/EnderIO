package com.enderio.enderio.content.machines.wired_charger;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.QuadraticIntScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
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
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class WiredChargerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticIntScalable CAPACITY = new QuadraticIntScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_CAPACITY);
    public static final QuadraticIntScalable USAGE = new QuadraticIntScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.WIRED_CHARGER_USAGE);

    public static final SingleResourceSlotKey<ItemResource> ITEM_TO_CHARGE = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> ITEM_CHARGED = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    private float progress = 0;

    public WiredChargerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.WIRED_CHARGER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, CAPACITY, USAGE);
    }

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .add(ITEM_TO_CHARGE, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> acceptItem(itemResource.toStack())))
            .add(ITEM_CHARGED, SlotTemplates.output())
            .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
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
        ItemStack inputItem = getInventory().getStack(ITEM_TO_CHARGE);
        ItemStack outputItem = getInventory().getStack(ITEM_CHARGED);
        return !inputItem.isEmpty() && outputItem.isEmpty() && acceptItem(inputItem) && super.canAct();
    }

    public void chargeItem() {
        ItemStack chargeable = getInventory().getStack(ITEM_TO_CHARGE);
        EnergyHandler itemEnergyHandler = chargeable.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(chargeable));

        if (itemEnergyHandler != null) {
            if (itemEnergyHandler.getAmountAsInt() == itemEnergyHandler.getCapacityAsInt()) {
                getInventory().setStack(ITEM_CHARGED, chargeable);
                getInventory().setStack(ITEM_TO_CHARGE, ItemStack.EMPTY);
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
