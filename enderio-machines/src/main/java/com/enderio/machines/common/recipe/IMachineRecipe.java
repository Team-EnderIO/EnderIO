package com.enderio.machines.common.recipe;

import com.enderio.base.common.recipe.IEnderRecipe;
import com.enderio.machines.EIOMachines;
import net.minecraft.world.Container;

public interface IMachineRecipe<R extends IMachineRecipe<R, C>, C extends Container> extends IEnderRecipe<R, C> {
    @Override
    default String getOwningMod() {
        return EIOMachines.MODID;
    }

    int getEnergyCost();
}