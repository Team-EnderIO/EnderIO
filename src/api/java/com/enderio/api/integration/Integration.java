package com.enderio.api.integration;

import net.minecraftforge.eventbus.api.IEventBus;

public abstract class Integration implements IntegrationMethods {

    private String modid;

    /**
     * @return the modid this integration was made for. Usage intended for datagen
     */
    public String getModid() {
        return modid;
    }

    /**
     * sets the modid for this integration. Usage intended for datagen and set by {@link IntegrationWrapper} in it's constructor for first party integrations
     */
    public void setModid(String modid) {
        if (this.modid != null)
            throw new IllegalCallerException("You are not allowed to set the modid of an integration");
        this.modid = modid;
    }

    void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
    }
}