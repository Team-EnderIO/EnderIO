package com.enderio.api.recipe;

import net.minecraft.world.Container;

public interface IMachineRecipe<R extends IMachineRecipe<R, C>, C extends Container> extends IEnderRecipe<R, C> {
    int getEnergyCost();
}