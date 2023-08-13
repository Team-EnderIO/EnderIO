package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.OmniBufferMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


//The Omni Buffer is basically a PowerBuffer in which we added 9 items slots
public class OmniBufferBlockEntity extends PowerBufferBlockEntity {

    public OmniBufferBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout
            .builder()
            .storageSlot(9)
            .capacitor()
            .build();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new OmniBufferMenu(this, playerInventory, containerId);
    }
}
