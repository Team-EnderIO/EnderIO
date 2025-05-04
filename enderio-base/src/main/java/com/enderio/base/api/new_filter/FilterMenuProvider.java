package com.enderio.base.api.new_filter;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

// TODO: Needs better name
public interface FilterMenuProvider {
    void openMenu(Player player, IItemHandlerModifiable itemHandler, int slot, @Nullable Runnable goBackRunnable);
}
