package com.enderio.machines.common.integrations;

import net.neoforged.fml.ModList;

public class IntegrationUtility {
    public static boolean hasRecipeViewer() {
        // Currently we only support JEI as a viewer.
        return ModList.get().isLoaded("jei");
    }
}
