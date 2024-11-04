package com.enderio.base.common.integrations;

import net.minecraftforge.fml.ModList;

public class IntegrationUtility {
    public static boolean hasRecipeViewer() {
        // Currently we only support JEI as a viewer.
        return ModList.get().isLoaded("jei");
    }
}
