package com.enderio.machines.datagen.recipe;

import com.enderio.machines.datagen.recipe.enchanter.AlloyRecipeGenerator;
import com.enderio.machines.datagen.recipe.enchanter.EnchanterRecipeGenerator;
import net.minecraft.data.DataGenerator;

public class MachineRecipeGenerator {

    public static void generate(DataGenerator dataGenerator) {
        dataGenerator.addProvider(new AlloyRecipeGenerator(dataGenerator));
        dataGenerator.addProvider(new EnchanterRecipeGenerator(dataGenerator));
    }
}
