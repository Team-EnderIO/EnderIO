package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.PrimitiveAlloySmelterBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class PrimitiveAlloySmelterMenu extends MachineMenu<PrimitiveAlloySmelterBlockEntity> {
    public static int INPUTS_INDEX = 0;
    public static int INPUT_COUNT = 3;
    public static int LAST_INDEX = 4;
    
    public PrimitiveAlloySmelterMenu(Inventory inventory, int pContainerId, @Nullable PrimitiveAlloySmelterBlockEntity blockEntity) {
        super(MachineMenus.PRIMITIVE_ALLOY_SMELTER.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.FUEL, 40, 53));
            addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.INPUTS.get(0), 20, 17));
            addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.INPUTS.get(1), 40, 17));
            addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.INPUTS.get(2), 60, 17));
            addSlot(new MachineSlot(getMachineInventory(), PrimitiveAlloySmelterBlockEntity.OUTPUT, 116, 35));
        }

        addPlayerInventorySlots(8,84);
    }

    public float getBurnProgress() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getBurnProgress();
    }

    public float getCraftingProgress() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getCraftingProgress();
    }

    public static PrimitiveAlloySmelterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof PrimitiveAlloySmelterBlockEntity castBlockEntity) {
            return new PrimitiveAlloySmelterMenu(inventory, pContainerId, castBlockEntity);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new PrimitiveAlloySmelterMenu(inventory, pContainerId, null);
    }
}
