package com.enderio.base.common.blockentity;

import com.enderio.api.UseOnly;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

/**
 * An interface that block entities may implement in order to implement special behaviours(other than to rotate the block) when right-clicked with the Yeta wrench.
 */
public interface IWrenchable {
    @UseOnly(LogicalSide.CLIENT)
    InteractionResult onWrenched(@Nullable Player player, @Nullable Direction side);
}
