package com.enderio.enderio.client.content.conduits.model.facades;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

// TODO: In future, support hiding specific conduit types too.
@EventBusSubscriber(value = Dist.CLIENT)
public class ClientFacadeVisibility {

    private static boolean FACADES_VISIBLE = true;

    //@EnsureSide(EnsureSide.Side.CLIENT) //TODO why does this crash?
    @UseOnly(LogicalSide.CLIENT)
    public static boolean areFacadesVisible() {
        return FACADES_VISIBLE;
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Pre event) {
        // Update every tick on the client.
        if (Minecraft.getInstance().level == null) {
            return;
        }

        if (event.getEntity() != Minecraft.getInstance().player) {
            return;
        }

        setFacadesVisible(FacadeUtil.areFacadesVisible(event.getEntity()));
    }

    private static void setFacadesVisible(boolean visible) {
        if (visible != FACADES_VISIBLE) {
            Minecraft.getInstance().execute(() -> {
                var level = Minecraft.getInstance().level;
                if (level != null) {
                    var chunkFacadesForDim = ConduitBundleBlockEntity.CHUNK_FACADES.get(level.dimension());
                    if (chunkFacadesForDim != null) {
                        chunkFacadesForDim.keySet().forEach((section) -> {
                            Minecraft.getInstance().levelRenderer.setSectionDirty(
                                SectionPos.x(section),
                                SectionPos.y(section),
                                SectionPos.z(section));
                        });
                    }
                }
            });
        }

        FACADES_VISIBLE = visible;
    }
}
