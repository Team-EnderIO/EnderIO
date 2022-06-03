package com.enderio.base.common.integration;


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
}