package com.enderio.machines.common.menu;

import org.apache.logging.log4j.LogManager;

import com.enderio.machines.common.blockentity.ImpulseHopperBlockEntity;
import com.enderio.machines.common.init.MachineMenus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ImpulseHopperMenu extends MachineMenu<ImpulseHopperBlockEntity>{


    public ImpulseHopperMenu(ImpulseHopperBlockEntity blockEntity, Inventory inventory,
			int pContainerId) {
		super(blockEntity, inventory, MachineMenus.IMPULSE_HOPPER.get(), pContainerId);
		if (blockEntity != null) {
			for(int j = 0; j < 2; ++j) {
				for(int k = 0; k < 6; ++k) {
					this.addSlot(new MachineSlot(blockEntity.getInventory(), k + j * 6, 8 + 36 + k * 18, 9 + j * 54));
				}
			}
			for(int k = 0; k < 6; ++k) {
				this.addSlot(new GhostSlot(blockEntity.getInventory(), 12 + k , 8 + 36 + k * 18, 9 + 27));
			}
			this.addSlot(new MachineSlot(blockEntity.getInventory(), 18, 11, 60));
		}
		addInventorySlots(8, 84);
	}
	
	public static ImpulseHopperMenu factory(@javax.annotation.Nullable MenuType<ImpulseHopperMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof ImpulseHopperBlockEntity castBlockEntity)
            return new ImpulseHopperMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new ImpulseHopperMenu(null, inventory, pContainerId);
    }
	
//	//Fix dragging items still being removed when the slot is a ghost slot.
//	@Override
//	public void clicked(int p_150400_, int button, ClickType clicktype, Player player) {
//        int h = getQuickcraftHeader(button);
//        if (h == 2 && this.quickcraftSlots.size() > 1) {
//            ItemStack itemstack3 = this.getCarried().copy();
//            int j1 = this.getCarried().getCount();
//
//            for(Slot slot1 : this.quickcraftSlots) {
//               ItemStack itemstack1 = this.getCarried();
//               if (slot1 != null && canItemQuickReplace(slot1, itemstack1, true) && slot1.mayPlace(itemstack1) && (getQuickcraftType(button) == 2 || itemstack1.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(slot1)) {
//                  ItemStack itemstack2 = itemstack3.copy();
//                  int j = slot1.hasItem() ? slot1.getItem().getCount() : 0;
//                  getQuickCraftSlotCount(this.quickcraftSlots, getQuickcraftType(button), itemstack2, j);
//                  int k = Math.min(itemstack2.getMaxStackSize(), slot1.getMaxStackSize(itemstack2));
//                  if (itemstack2.getCount() > k) {
//                     itemstack2.setCount(k);
//                  }
//
//                  if (slot1 instanceof GhostSlot) {
//                      //TODO maybe a different check?
//                  } else {
//                      j1 -= itemstack2.getCount() - j;
//                  }
//                  slot1.set(itemstack2);
//               }
//            }
//
//            itemstack3.setCount(j1);
//            this.setCarried(itemstack3);
//            return;
//        }
//	    super.clicked(p_150400_, button, clicktype, player);
//	}
}
