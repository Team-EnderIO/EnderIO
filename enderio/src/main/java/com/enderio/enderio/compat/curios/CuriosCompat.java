package com.enderio.enderio.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CuriosCompat {

    public static Optional<List<ItemStack>> getActiveCurios(Player player, @Nullable Predicate<ItemStack> filter){
        if(ModList.get().isLoaded("curios")) {
            List<ItemStack> result = new ArrayList<>();
            var handlers = CuriosApi.getCuriosInventory(player).get().getCurios().values().stream()
                .map(ICurioStacksHandler::getStacks)
                .toList();
            for (var stackHandler : handlers) {
                for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
                    ItemStack stack = stackHandler.getStackInSlot(slot);
                    if(filter == null || filter.test(stack))
                        result.add(stack);
                }
            }

            return Optional.of(result);
        }

        return Optional.empty();
    }
}
