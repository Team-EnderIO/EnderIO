package com.enderio.enderio.compat;

import net.neoforged.fml.ModList;

public class ModCompatHelper {

    private static boolean initialized = false;
    private static boolean hasJEI;
    private static boolean hasIris;
    private static boolean hasSodium;

    public static boolean hasRecipeViewer() {
        init();
        // Currently we only support JEI as a viewer.
        return hasJEI;
    }

    public static boolean hasIris() {
        init();
        return hasIris;
    }

    public static boolean hasSodium() {
        init();
        return hasSodium;
    }

    private static void init() {
        if (!initialized) {
            hasJEI = ModList.get().isLoaded("jei");
            hasIris = ModList.get().isLoaded("iris");
            hasSodium = ModList.get().isLoaded("sodium");
            initialized = true;
        }
    }
}
