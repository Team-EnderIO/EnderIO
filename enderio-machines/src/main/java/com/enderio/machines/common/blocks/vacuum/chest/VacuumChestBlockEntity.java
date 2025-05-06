package com.enderio.machines.common.blocks.vacuum.chest;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout.Builder;
import com.enderio.machines.common.blocks.vacuum.VacuumMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class VacuumChestBlockEntity extends VacuumMachineBlockEntity<ItemEntity> {

    public VacuumChestBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineBlockEntities.VACUUM_CHEST.get(), pWorldPosition, pBlockState, ItemEntity.class);
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
                        .filter((i, s) -> s.getCapability(EIOCapabilities.ITEM_FILTER) != null))
                .slotAccess(FILTER)
                .build();
    }

    @Override
    public void handleEntity(ItemEntity entity) {
        for (int i = 0; i < this.getInventory().getSlots(); i++) {
            ItemStack itemToReceive = entity.getItem().copy();

            // Enable the filter to adjust the amount to accept (limited item filter)
            var filter = FILTER.getItemStack(this).getCapability(EIOCapabilities.ITEM_FILTER);
            if (filter != null) {
                itemToReceive = filter.test(getInventory(), itemToReceive);
            }

            // Abort if we can't accept the item.
            if (itemToReceive.isEmpty()) {
                return;
            }

            ItemStack remainder = this.getInventory().insertItem(i, itemToReceive, false);
            if (remainder.isEmpty()) {
                entity.discard();
                return;
            } else {
                entity.getItem().setCount(remainder.getCount());
            }
        }
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.VACUUM_CHEST_RANGE_COLOR.get();
    }

    @Override
    public Predicate<ItemEntity> getFilter() {
        var filter = FILTER.getItemStack(this).getCapability(EIOCapabilities.ITEM_FILTER);
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
