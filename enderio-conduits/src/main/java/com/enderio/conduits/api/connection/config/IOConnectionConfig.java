package com.enderio.conduits.api.connection.config;

import com.enderio.conduits.api.ConduitRedstoneSignalAware;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface IOConnectionConfig extends ConnectionConfig {
    /**
     * @return whether the connection is sending resources to the connected block
     */
    boolean isInsert();

    /**
     * @return whether the connection is taking resources from the connected block
     */
    boolean isExtract();

    /**
     * These colors are used for insert separation in the ticker.
     * If no channel separation is required, always return the same color for any connection.
     * In this scenario, this is now simply the color of the arrow on the model.
     * @return the insert color channel.
     */
    DyeColor insertChannel();

    /**
     * These colors are used for extract separation in the ticker.
     * If no channel separation is required, always return the same color for any connection.
     * In this scenario, this is now simply the color of the arrow on the model.
     * @return the extract color channel.
     */
    DyeColor extractChannel();

    default boolean canInsert(ConduitRedstoneSignalAware signalAware) {
        return isInsert();
    }

    default boolean canExtract(ConduitRedstoneSignalAware signalAware) {
        return isExtract();
    }

    @Override
    default boolean isConnected() {
        return isInsert() || isExtract();
    }
}
