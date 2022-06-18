package com.enderio.api.grindingball;

import net.minecraft.resources.ResourceLocation;

/**
 * Grinding ball bonus data.
 */
public interface IGrindingBallData {

    /**
     * The grinding ball ID used to find it in the manager.
     * This will be the recipe ID.
     */
    ResourceLocation getId();

    /**
     * Get main output chance.
     */
    float getMainOutput();

    /**
     * Get bonus output chance.
     */
    float getBonusOutput();

    /**
     * Get power use modifier.
     */
    float getPowerUse();

    /**
     * Get durability.
     * @return Griding ball durability in micro infinity.
     */
    int getDurability();
    
}
