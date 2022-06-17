package com.enderio.machines.datagen.recipe;

import com.enderio.machines.datagen.recipe.enchanter.AlloyRecipeGenerator;
import com.enderio.machines.datagen.recipe.enchanter.EnchanterRecipeGenerator;
import net.minecraft.data.DataGenerator;

public class MachineRecipeGenerator {

    public static void generate(boolean includeServer, DataGenerator dataGenerator) {
        dataGenerator.addProvider(includeServer, new AlloyRecipeGenerator(dataGenerator));
        dataGenerator.addProvider(includeServer, new EnchanterRecipeGenerator(dataGenerator));
    }
}
