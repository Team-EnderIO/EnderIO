package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.SoulBinderBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class SoulBinderMenu extends PoweredMachineMenu<SoulBinderBlockEntity> {
    public static int INPUTS_INDEX = 1;
    public static int INPUT_COUNT = 2;
    public static int LAST_INDEX = 4;


    public SoulBinderMenu(int pContainerId, @Nullable SoulBinderBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.SOUL_BINDER.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            addSlot(new MachineSlot(getMachineInventory(), blockEntity.getCapacitorSlot(), 12, 60));

            addSlot(new MachineSlot(getMachineInventory(), SoulBinderBlockEntity.INPUT_SOUL, 38, 34));
            addSlot(new MachineSlot(getMachineInventory(), SoulBinderBlockEntity.INPUT_OTHER, 59, 34));
            addSlot(new MachineSlot(getMachineInventory(), SoulBinderBlockEntity.OUTPUT.get(0), 112, 34));
            addSlot(new MachineSlot(getMachineInventory(), SoulBinderBlockEntity.OUTPUT.get(1), 134, 34));

        }

        addPlayerInventorySlots(8,84);
    }

    public float getCraftingProgress() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getCraftingProgress();
    }

    public MachineFluidTank getFluidTank() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getFluidTank();
    }

    public int getExperience() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getClientExp();
    }

    public static SoulBinderMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof SoulBinderBlockEntity castBlockEntity) {
            return new SoulBinderMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new SoulBinderMenu(pContainerId, null, inventory);
    }
}
