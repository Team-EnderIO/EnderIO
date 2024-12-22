package com.enderio.machines.common.blocks.obelisks.xp;

import com.enderio.core.common.network.menu.FluidStackSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;

public class XPObeliskMenu extends MachineMenu<XPObeliskBlockEntity> {

    public static final int ADD_1_LEVEL_BUTTON_ID = 0;
    public static final int REMOVE_1_LEVEL_BUTTON_ID = 1;
    public static final int ADD_10_LEVELS_BUTTON_ID = 2;
    public static final int REMOVE_10_LEVELS_BUTTON_ID = 3;
    public static final int ADD_ALL_XP_BUTTON_ID = 4;
    public static final int REMOVE_ALL_XP_BUTTON_ID = 5;

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
        case ADD_1_LEVEL_BUTTON_ID -> blockEntity.addLevelsToPlayer(player, 1);
        case REMOVE_1_LEVEL_BUTTON_ID -> blockEntity.removeLevelsFromPlayer(player, 1);
        case ADD_10_LEVELS_BUTTON_ID -> blockEntity.addLevelsToPlayer(player, 10);
        case REMOVE_10_LEVELS_BUTTON_ID -> blockEntity.removeLevelsFromPlayer(player, 10);
        case ADD_ALL_XP_BUTTON_ID -> blockEntity.addAllXpToPlayer(player);
        case REMOVE_ALL_XP_BUTTON_ID -> blockEntity.removeAllXpFromPlayer(player);
        default -> throw new IllegalStateException("Unexpected value: " + id);
        }
        return true;
    }

}
