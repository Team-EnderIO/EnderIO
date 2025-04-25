package com.enderio.conduits.client.model.conduit.facades;

import com.enderio.conduits.common.conduit.block.ConduitBundleBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;

// TODO: In future, support hiding specific conduit types too.
public class FacadeHelper {

    private static boolean FACADES_VISIBLE = true;

    public static void setFacadesVisible(boolean visible) {
        if (visible != FACADES_VISIBLE) {
            RenderSystem.recordRenderCall(() -> {
                ConduitBundleBlockEntity.CHUNK_FACADES.keySet().forEach((section) -> {
                    Minecraft.getInstance().levelRenderer.setSectionDirty(SectionPos.x(section), SectionPos.y(section),
                            SectionPos.z(section));
                });
            });
        }

        FACADES_VISIBLE = visible;
    }

    public static boolean areFacadesVisible() {
        return FACADES_VISIBLE;
    }
}
