package com.enderio.base.common.integration;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public abstract class Integration {

    private String modid;

    public String getModid() {
        return modid;
    }

    public void setModid(String modid) {
        if (this.modid != null)
            throw new IllegalCallerException("You are not allowed to set the modid of an integration");
        this.modid = modid;
    }

    void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {

    }

    public void createData(GatherDataEvent event) {

    }
}