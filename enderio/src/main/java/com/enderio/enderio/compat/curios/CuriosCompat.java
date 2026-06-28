package com.enderio.enderio.compat.curios;

// 26.2-port: third-party mod interaction commented out — Curios compat deferred
// import top.theillusivec4.curios.api.CuriosApi;
// import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
// import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CuriosCompat {

    public static Optional<List<ItemStack>> getActiveCurios(Player player, @Nullable Predicate<ItemStack> filter){
        // 26.2-port: Curios integration disabled — returns empty Optional
        return Optional.empty();
    }
}
