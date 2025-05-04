package com.enderio.base.api.new_filter;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

// TODO: Needs better name
public interface FilterMenuProvider {
    void openMenu(Player player, IItemHandler itemHandler, int slot, @Nullable Runnable goBackRunnable);
}
