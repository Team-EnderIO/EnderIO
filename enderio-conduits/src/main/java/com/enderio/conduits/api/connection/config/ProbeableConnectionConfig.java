package com.enderio.conduits.api.connection.config;

import net.minecraft.nbt.CompoundTag;

/**
 * Get the list of redstone signal colors that this connection is sensitive to.
 * This is exclusively used for conduit connection rendering.
 */
public interface ProbeableConnectionConfig {
    /**
     * @return the data in a conduit connection to be copied
     */
    CompoundTag getProbeTag();

    /**
     * @param tag the new data to set in a conduit connection
     */
    ProbeableConnectionConfig fromProbeTag(CompoundTag tag);
}
