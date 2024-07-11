package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.menu.base.MachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class FluidTankMenu extends MachineMenu<FluidTankBlockEntity> {

    public FluidTankMenu(int pContainerId, @Nullable FluidTankBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.FLUID_TANK.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            addSlot(new MachineSlot(getMachineInventory(), FluidTankBlockEntity.FLUID_FILL_INPUT, 44, 21));
            addSlot(new MachineSlot(getMachineInventory(), FluidTankBlockEntity.FLUID_FILL_OUTPUT, 44, 52));
            addSlot(new MachineSlot(getMachineInventory(), FluidTankBlockEntity.FLUID_DRAIN_INPUT, 116, 21));
            addSlot(new MachineSlot(getMachineInventory(), FluidTankBlockEntity.FLUID_DRAIN_OUTPUT, 116, 52));
        }

        addPlayerInventorySlots(8,84);
    }

    public MachineFluidTank getFluidTank() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getFluidTank();
    }

    public static FluidTankMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof FluidTankBlockEntity castBlockEntity) {
            return new FluidTankMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new FluidTankMenu(pContainerId, null, inventory);
    }
}
