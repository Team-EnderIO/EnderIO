package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.ImpulseHopperBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class ImpulseHopperMenu extends PoweredMachineMenu<ImpulseHopperBlockEntity> {

    public ImpulseHopperMenu(@Nullable ImpulseHopperBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(MachineMenus.IMPULSE_HOPPER.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            for (int i = 0; i < 6; i++) {
                this.addSlot(new MachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.INPUT.get(i), 8 + 36 + i * 18, 9));
                this.addSlot(new GhostMachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.GHOST.get(i), 8 + 36 + i * 18, 9 + 27));
                this.addSlot(new MachineSlot(getMachineInventory(), ImpulseHopperBlockEntity.OUTPUT.get(i), 8 + 36 + i * 18, 9 + 54));
            }
            this.addSlot(new MachineSlot(getMachineInventory(), 18, 11, 60));
        }

        addPlayerInventorySlots(8, 84);
    }

    public static ImpulseHopperMenu factory(int pContainerId, Inventory inventory,
        FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof ImpulseHopperBlockEntity castBlockEntity) {
            return new ImpulseHopperMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new ImpulseHopperMenu(null, inventory, pContainerId);
    }

}
