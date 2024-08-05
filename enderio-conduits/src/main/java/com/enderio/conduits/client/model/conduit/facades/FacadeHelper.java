package com.enderio.conduits.client.model.conduit.facades;

import com.enderio.base.common.tag.EIOTags;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

// TODO: In future, support hiding specific conduit types too.
public class FacadeHelper {
    public static boolean areFacadesVisible() {
        var minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return true;
        }

        var mainHand = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
        var offHand = minecraft.player.getItemInHand(InteractionHand.OFF_HAND);

        return !mainHand.is(EIOTags.Items.WRENCH) && !offHand.is(EIOTags.Items.WRENCH);
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
