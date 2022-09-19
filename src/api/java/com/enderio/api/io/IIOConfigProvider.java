package com.enderio.api.io;

/**
 * Provides access to the config for a given machine's IO
 */
public interface IIOConfigProvider {

    /**
     * Gets the IO Config for the given machine
     * @return the IO Config for the given machine
     */
    IIOConfig getIOConfig();
}
