package com.enderio.enderio.compat;

import net.neoforged.fml.ModList;

public class ModCompatHelper {

    private static boolean initialized = false;
    private static boolean hasJEI;
    private static boolean hasIris;

    // Set by SodiumConduitFacadeMixin's two @ModifyVariable injectors the first time each runs, proving
    // the injection actually applied. Both the state and the model interceptor must swap in lockstep, so
    // the overlay only defers to the mixin once BOTH are confirmed - a partial injection failure (one
    // interceptor unresolved) then cleanly falls back to overlay rendering instead of a broken half-swap.
    private static volatile boolean sodiumFacadeStateMixinActive = false;
    private static volatile boolean sodiumFacadeModelMixinActive = false;

    public static boolean hasRecipeViewer() {
        init();
        // Currently we only support JEI as a viewer.
        return hasJEI;
    }

    public static boolean hasIris() {
        init();
        return hasIris;
    }

    public static void markSodiumFacadeStateMixinActive() {
        sodiumFacadeStateMixinActive = true;
    }

    public static void markSodiumFacadeModelMixinActive() {
        sodiumFacadeModelMixinActive = true;
    }

    public static boolean isSodiumFacadeMixinActive() {
        return sodiumFacadeStateMixinActive && sodiumFacadeModelMixinActive;
    }

    private static void init() {
        if (!initialized) {
            hasJEI = ModList.get().isLoaded("jei");
            hasIris = ModList.get().isLoaded("iris");
            initialized = true;
        }
    }
}
