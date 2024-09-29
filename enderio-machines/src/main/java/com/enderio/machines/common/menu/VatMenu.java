package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.VatBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.menu.base.MachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class VatMenu extends MachineMenu<VatBlockEntity> {
    public static int INPUTS_INDEX = 0;
    public static int INPUT_COUNT = 2;
    public static int LAST_INDEX = 1;

    public VatMenu(int pContainerId, @Nullable VatBlockEntity blockEntity, Inventory inventory) {
        super(MachineMenus.VAT.get(), pContainerId, blockEntity, inventory);
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getInventory(), VatBlockEntity.REAGENTS.get(0), 56, 12));
            addSlot(new MachineSlot(blockEntity.getInventory(), VatBlockEntity.REAGENTS.get(1), 105, 12));
        }

        addPlayerInventorySlots(8, 84);
    }

    public MachineFluidTank getOutputTank() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getOutputTank();
    }

    public void moveFluidToOutputTank() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().moveFluidToOutputTank();
    }

    public void dumpOutputTank() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().dumpOutputTank();
    }

    public static VatMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof VatBlockEntity castBlockEntity) {
            return new VatMenu(pContainerId, castBlockEntity, inventory);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new VatMenu(pContainerId, null, inventory);
    }
}
