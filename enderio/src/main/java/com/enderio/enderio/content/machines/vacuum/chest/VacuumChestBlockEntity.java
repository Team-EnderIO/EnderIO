package com.enderio.enderio.content.machines.vacuum.chest;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.machines.vacuum.VacuumMachineBlockEntity;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout.Builder;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.Predicate;

public class VacuumChestBlockEntity extends VacuumMachineBlockEntity<ItemEntity> {

    public VacuumChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.VACUUM_CHEST.get(), worldPosition, blockState, ItemEntity.class);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new VacuumChestMenu(containerId, inventory, this);
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return extractableGUISlot(MachineInventoryLayout.builder(), 27)
                .slot(slot -> slot.guiInsert()
                        .guiExtract()
                        .filter((i, resource) -> resource.toStack().getCapability(EnderIOCapabilities.ITEM_FILTER) != null))
                .slotAccess(FILTER)
                .build();
    }

    @Override
    public void handleEntity(ItemEntity entity) {
        ItemStack itemToReceive = entity.getItem().copy();

        // Enable the filter to adjust the amount to accept (limited item filter)
        var filter = FILTER.getItemStack(this).getCapability(EnderIOCapabilities.ITEM_FILTER);
        if (filter != null) {
            itemToReceive = filter.test(getInventory(), itemToReceive);
        }

        // Abort if we can't accept the item.
        if (itemToReceive.isEmpty()) {
            return;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = ResourceHandlerUtil.insertStacking(getInventory(), ItemResource.of(itemToReceive), itemToReceive.getCount(), transaction);
            transaction.commit();

            if (inserted == itemToReceive.getCount()) {
                entity.discard();
            } else {
                entity.getItem().setCount(itemToReceive.getCount() - inserted);
            }
        }
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.VACUUM_CHEST_RANGE_COLOR.get();
    }

    @Override
    public Predicate<ItemEntity> getFilter() {
        var filter = FILTER.getItemStack(this).getCapability(EnderIOCapabilities.ITEM_FILTER);
        if (filter != null) {
            return itemEntity -> !filter.test(getInventory(), itemEntity.getItem()).isEmpty();
        }

        return super.getFilter();
    }

    // Slot config

    public Builder extractableGUISlot(Builder builder, int count) {
        for (int i = 0; i < count; i++) {
            builder.slot(slot -> slot.guiInsert().guiExtract().extract());
        }
        return builder;
    }
}
