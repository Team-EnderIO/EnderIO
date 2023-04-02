package com.enderio.base.common.blockentity;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.extensions.IForgeBlockEntity;

/**
 * An interface that block entities may implement in order to implement special behaviours(other than to rotate the block) when right-clicked with the Yeta wrench.
 */
public interface IWrenchable {
    /**
     * Only called on the logical server side, never on the client.
     *
     * @param context - source event context.
     * @return - works exactly like the vanilla implementation.
     */
    InteractionResult onWrenched(UseOnContext context);
}
