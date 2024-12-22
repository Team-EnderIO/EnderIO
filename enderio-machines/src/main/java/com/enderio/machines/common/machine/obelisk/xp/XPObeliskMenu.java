package com.enderio.machines.common.machine.obelisk.xp;

import com.enderio.core.common.network.menu.FluidStackSyncSlot;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.machine.base.menu.NewMachineMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;

public class XPObeliskMenu extends NewMachineMenu<XPObeliskBlockEntity> {

    private final FluidStackSyncSlot tankSyncSlot;

    public XPObeliskMenu(int pContainerId, Inventory inventory, XPObeliskBlockEntity blockEntity) {
        super(MachineMenus.XP_OBELISK.get(), pContainerId, inventory, blockEntity);

        tankSyncSlot = addSyncSlot(FluidStackSyncSlot.readOnly(() -> blockEntity.getFluidTank().getFluid()));
    }

    public XPObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.XP_OBELISK.get(), MachineBlockEntities.XP_OBELISK.get(), containerId, playerInventory, buf);

        tankSyncSlot = addSyncSlot(FluidStackSyncSlot.standalone());
    }

    public FluidStack getFluid() {
        return tankSyncSlot.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        XPObeliskBlockEntity blockEntity = getBlockEntity();

        switch (id) {
        case 0 -> blockEntity.addLevelsToPlayer(player, 1);
        case 1 -> blockEntity.removeLevelsFromPlayer(player, 1);
        case 2 -> blockEntity.addLevelsToPlayer(player, 10);
        case 3 -> blockEntity.removeLevelsFromPlayer(player, 10);
        case 4 -> blockEntity.addAllXpToPlayer(player);
        case 5 -> blockEntity.removeAllXpFromPlayer(player);
        default -> throw new IllegalStateException("Unexpected value: " + id);
        }
        return true;
    }

}
