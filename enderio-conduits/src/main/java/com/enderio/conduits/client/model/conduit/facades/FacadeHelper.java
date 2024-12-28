package com.enderio.conduits.client.model.conduit.facades;

import net.minecraft.client.Minecraft;

// TODO: In future, support hiding specific conduit types too.
public class FacadeHelper {

    private static boolean FACADES_VISIBLE = true;

    public static void setFacadesVisible(boolean visible) {
        FACADES_VISIBLE = visible;
    }

    public static boolean areFacadesVisible() {
        return FACADES_VISIBLE;
    }

    public static void rebuildChunkMeshes() {
        var minecraft = Minecraft.getInstance();

        if (minecraft.levelRenderer.viewArea == null) {
            return;
        }

        for (var section : minecraft.levelRenderer.viewArea.sections) {
            section.setDirty(false);
        }
    }
}
