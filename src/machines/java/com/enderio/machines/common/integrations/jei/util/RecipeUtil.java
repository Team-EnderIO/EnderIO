package com.enderio.machines.common.integrations.jei.util;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;

import java.util.List;

/**
 * From mezz utility library.
 * Should probably import.
 * Sounds to me like a TODO: 1.20.1 issue.
 */
public class RecipeUtil {
    public static List<OutputStack> getResultStacks(MachineRecipe<?> recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }
        RegistryAccess registryAccess = level.registryAccess();
        return recipe.getResultStacks(registryAccess);
    }
}