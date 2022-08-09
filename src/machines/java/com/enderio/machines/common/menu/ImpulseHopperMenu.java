package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.ImpulseHopperBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class ImpulseHopperMenu extends MachineMenu<ImpulseHopperBlockEntity> {

    public ImpulseHopperMenu(ImpulseHopperBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.IMPULSE_HOPPER.get(), pContainerId);
        if (blockEntity != null) {
            for (int j = 0; j < 2; ++j) {
                for (int k = 0; k < 6; ++k) {
                    this.addSlot(new MachineSlot(blockEntity.getInventory(), k + j * 6, 8 + 36 + k * 18, 9 + j * 54));
                }
            }
            for (int k = 0; k < 6; ++k) {
                this.addSlot(new GhostMachineSlot(blockEntity.getInventory(), 12 + k, 8 + 36 + k * 18, 9 + 27));
            }
            this.addSlot(new MachineSlot(blockEntity.getInventory(), 18, 11, 60));
        }
        addInventorySlots(8, 84);
    }

    public static ImpulseHopperMenu factory(@Nullable MenuType<ImpulseHopperMenu> pMenuType, int pContainerId, Inventory inventory,
        FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof ImpulseHopperBlockEntity castBlockEntity)
            return new ImpulseHopperMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new ImpulseHopperMenu(null, inventory, pContainerId);
    }

}
