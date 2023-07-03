package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.blockentity.base.VacuumMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MachineInventoryLayout.Builder;
import com.enderio.machines.common.menu.VacuumChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class VacuumChestBlockEntity extends VacuumMachineBlockEntity<ItemEntity> {

    public VacuumChestBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState, ItemEntity.class);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new VacuumChestMenu(this, inventory, containerId);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return extractableGUISlot(MachineInventoryLayout.builder(), 27)
            .slot(slot -> slot.guiInsert().guiExtract().filter((i, s) -> false))
            .build(); //TODO add proper filter slot and predicate
    }

    @Override
    public void handleEntity(ItemEntity entity) {
        for (int i = 0; i < this.getInventory().getSlots(); i++) {
            ItemStack reminder = this.getInventory().insertItem(i, entity.getItem().copy(), false);
            if (reminder.isEmpty()) {
                entity.discard();
                return;
            } else {
                entity.getItem().setCount(reminder.getCount());
            }
        }
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.VACUUM_CHEST_RANGE_COLOR.get();
    }

    //TODO filter
    @Override
    public Predicate<ItemEntity> getFilter() {
        // get filter slot -> get filter item -> filter
        // maybe cache on item insert
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
