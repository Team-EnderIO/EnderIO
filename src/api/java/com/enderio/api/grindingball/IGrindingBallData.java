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
     * Get the chance of doubling all outputs.
     * 1.0 - no doubling, 2.0 - guaranteed doubling, 3.0 - tripling...
     */
    float getOutputMultiplier();

    /**
     * Get bonus output multiplier.
     * 0 - no bonuses
     * 2.0 -
     */
    float getBonusMultiplier();

    /**
     * Get power use modifier.
     */
    float getPowerUse();

    /**
     * Get durability.
     * @return Griding ball durability in micro infinity.
     */
    int getDurability();

    /**
     * Grinding ball identity value. Used when no bonus grinding ball is installed.
     */
    IGrindingBallData IDENTITY = new IGrindingBallData() {
        @Override
        public ResourceLocation getId() {
            // ID isn't actually mapped anywhere.
            return new ResourceLocation("enderio", "grindingball/identity");
        }

        @Override
        public float getOutputMultiplier() {
            return 1;
        }

        @Override
        public float getBonusMultiplier() {
            return 1;
        }

        @Override
        public float getPowerUse() {
            return 1;
        }

        @Override
        public int getDurability() {
            return 0;
        }
    };

}
