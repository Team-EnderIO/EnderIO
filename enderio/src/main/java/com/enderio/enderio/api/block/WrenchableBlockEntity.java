package com.enderio.enderio.api.block;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * An interface that block entities may implement in order to implement special behaviours(other than to rotate the block) when right-clicked with the Yeta wrench.
 */
@ApiStatus.AvailableSince("10.0.0")
public interface WrenchableBlockEntity {
    /**
     * Handle what happens when the block entity has been right-clicked with a wrench.
     * @param context the use context.
     * @return the result of the interaction.
     */
    InteractionResult onWrenched(UseOnContext context);
}
