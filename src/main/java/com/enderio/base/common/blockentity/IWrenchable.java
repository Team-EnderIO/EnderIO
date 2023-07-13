package com.enderio.base.common.blockentity;

import com.enderio.api.UseOnly;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.fml.LogicalSide;

/**
 * An interface that block entities may implement in order to implement special behaviours(other than to rotate the block) when right-clicked with the Yeta wrench.
 */
public interface IWrenchable {
    @UseOnly(LogicalSide.CLIENT)
    InteractionResult onWrenched(UseOnContext context);
}
