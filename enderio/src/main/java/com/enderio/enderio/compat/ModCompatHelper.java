package com.enderio.enderio.compat;

import net.neoforged.fml.ModList;

public class ModCompatHelper {

    private static boolean initialized = false;
    private static boolean hasJEI;
    private static boolean hasIris;

    public static boolean hasRecipeViewer() {
        init();
        // Currently we only support JEI as a viewer.
        return hasJEI;
    }

    public static boolean hasIris() {
        init();
        return hasIris;
    }

    private static void init() {
        if (!initialized) {
            hasJEI = ModList.get().isLoaded("jei");
            hasIris = ModList.get().isLoaded("iris");
            initialized = true;
        }
    }
}
