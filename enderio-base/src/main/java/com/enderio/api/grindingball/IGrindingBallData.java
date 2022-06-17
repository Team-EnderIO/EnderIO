package com.enderio.api.grindingball;

import net.minecraft.resources.ResourceLocation;

public interface IGrindingBallData {

    ResourceLocation getId();

    float getMainOutput();
    
    float getBonusOutput();
    
    float getPowerUse();
    
    int getDurability();
    
}
