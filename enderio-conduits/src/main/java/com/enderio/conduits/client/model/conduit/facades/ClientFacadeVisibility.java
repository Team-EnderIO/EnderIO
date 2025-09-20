package com.enderio.conduits.client.model.conduit.facades;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

// TODO: In future, support hiding specific conduit types too.
// TODO: Move to client package.
@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, value = Dist.CLIENT)
public class ClientFacadeVisibility {

    private static boolean FACADES_VISIBLE = true;

    @EnsureSide(EnsureSide.Side.CLIENT)
    public static boolean areFacadesVisible() {
        return FACADES_VISIBLE;
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Pre event) {
        // Update every tick on the client.
        setFacadesVisible(FacadeUtil.areFacadesVisible(event.getEntity()));
    }

    private static void setFacadesVisible(boolean visible) {
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
}
