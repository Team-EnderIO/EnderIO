package com.enderio.enderio.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CuriosCompat {

    public static Optional<List<ItemStack>> getAllCuriosOnPlayer(Player player){
        if(ModList.get().isLoaded("curios")) {
            List<ItemStack> curiosItemStackList = new ArrayList<>();

            ICuriosItemHandler curiosItemHandler = player.getCapability(CuriosCapability.INVENTORY);
            if (curiosItemHandler != null) {
                for (IDynamicStackHandler curiosStackHandler : curiosItemHandler.getCurios().values().stream().map(ICurioStacksHandler::getStacks).collect(Collectors.toSet())) {
                    for (int slot = 0; slot < curiosStackHandler.getSlots(); slot++) {
                        ItemStack stack = curiosStackHandler.getStackInSlot(slot);
                        curiosItemStackList.add(stack);
                    }
                }
            }
            return Optional.of(curiosItemStackList);

        }else {
            return Optional.empty();
        }
    }
}
