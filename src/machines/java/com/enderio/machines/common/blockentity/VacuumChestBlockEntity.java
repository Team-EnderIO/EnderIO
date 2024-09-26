package com.enderio.machines.common.blockentity;

import com.enderio.api.filter.ItemStackFilter;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.blockentity.base.VacuumMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MachineInventoryLayout.Builder;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.VacuumChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Predicate;

public class VacuumChestBlockEntity extends VacuumMachineBlockEntity<ItemEntity> {

    public static final SingleSlotAccess FILTER_SLOT = new SingleSlotAccess();

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
            .slot(slot -> slot.guiInsert().guiExtract().filter(this::acceptFilter))
            .slotAccess(FILTER_SLOT)
            .build();
    }

    private boolean acceptFilter(int slot, ItemStack itemStack) {
        return itemStack.getCapability(EIOCapabilities.FILTER)
            .map(filter -> filter instanceof ItemStackFilter)
            .orElse(false);
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

    @Override
    public Predicate<ItemEntity> getFilter() {
        ItemStack filterItemStack = FILTER_SLOT.getItemStack(this);
        LazyOptional<ResourceFilter> resourceFilter = filterItemStack.getCapability(EIOCapabilities.FILTER);

        return resourceFilter
            .map(filter -> {
                if (filter instanceof ItemStackFilter itemStackFilter) {
                    return (Predicate<ItemEntity>)(ItemEntity itemEntity) -> itemStackFilter.test(itemEntity.getItem());
                }

                return ITEM_ENTITY_FILTER_TRUE;
            })
            .orElse(ITEM_ENTITY_FILTER_TRUE);
    }

    // Slot config

    public Builder extractableGUISlot(Builder builder, int count) {
        for (int i = 0; i < count; i++) {
            builder.slot(slot -> slot.guiInsert().guiExtract().extract());
        }
        return builder;
    }
}
