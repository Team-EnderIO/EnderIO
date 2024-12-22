package com.enderio.machines.common.integrations.jei.util;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * From mezz utility library.
 * Should probably import.
 * Sounds to me like a TODO: 1.20.1 issue.
 */
public class RecipeUtil {
    public static <T extends MachineRecipe<?>> List<OutputStack> getResultStacks(RecipeHolder<T> recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }
        RegistryAccess registryAccess = level.registryAccess();
        return recipe.value().getResultStacks(registryAccess);
    }

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
