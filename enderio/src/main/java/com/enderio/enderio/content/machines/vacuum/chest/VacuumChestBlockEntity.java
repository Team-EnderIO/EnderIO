package com.enderio.enderio.content.machines.vacuum.chest;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.machines.vacuum.VacuumMachineBlockEntity;
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

    public static MultiResourceSlotKey<ItemResource> INVENTORY = new MultiResourceSlotKey<>(27);

    public VacuumChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.VACUUM_CHEST.get(), worldPosition, blockState, ItemEntity.class);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new VacuumChestMenu(containerId, inventory, this);
    }

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(INVENTORY, SlotTemplates.storage())
            .add(FILTER, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> itemResource.toStack().getCapability(EnderIOCapabilities.ITEM_FILTER) != null))
            .build();
    }

    @Override
    public void handleEntity(ItemEntity entity) {
        ItemStack itemToReceive = entity.getItem().copy();

        // Enable the filter to adjust the amount to accept (limited item filter)
        var filter = getInventory().getStack(FILTER).getCapability(EnderIOCapabilities.ITEM_FILTER);
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
        var filter = getInventory().getStack(FILTER).getCapability(EnderIOCapabilities.ITEM_FILTER);
        if (filter != null) {
            return itemEntity -> !filter.test(getInventory(), itemEntity.getItem()).isEmpty();
        }

        return super.getFilter();
    }
}
