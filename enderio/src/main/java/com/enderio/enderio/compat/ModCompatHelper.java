package com.enderio.enderio.compat;

import net.neoforged.fml.ModList;

public class ModCompatHelper {

    private static boolean initialized = false;
    private static boolean hasJEI;
    private static boolean hasIris;

    // Set by SodiumConduitFacadeMixin the first time it runs, proving the injection actually applied.
    // The conduit facade overlay only skips opaque facades once this is confirmed, so a failed mixin
    // target never leaves opaque facades unrendered by both paths.
    private static volatile boolean sodiumFacadeMixinActive = false;

    public static boolean hasRecipeViewer() {
        init();
        // Currently we only support JEI as a viewer.
        return hasJEI;
    }

    public static boolean hasIris() {
        init();
        return hasIris;
    }

    public static void markSodiumFacadeMixinActive() {
        sodiumFacadeMixinActive = true;
    }

    public static boolean isSodiumFacadeMixinActive() {
        return sodiumFacadeMixinActive;
    }

    private static void init() {
        if (!initialized) {
            hasJEI = ModList.get().isLoaded("jei");
            hasIris = ModList.get().isLoaded("iris");
            initialized = true;
        }
    }
}
