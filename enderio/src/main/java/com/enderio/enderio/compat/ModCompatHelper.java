package com.enderio.enderio.compat;

import net.neoforged.fml.ModList;

public class ModCompatHelper {
    public static boolean hasRecipeViewer() {
        // Currently we only support JEI as a viewer.
        return ModList.get().isLoaded("jei");
    }
}
