package com.enderio.enderio.client.content.travel;

import com.enderio.enderio.api.travel.TravelRenderer;
import com.enderio.enderio.content.travel.travel_anchor.AnchorTravelTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.ARGB;

// 26.2-port: The travel-anchor world-space rendering was rewritten on the new
//   submit-node pipeline (TravelRendererUtil no longer exposes the old
//   MultiBufferSource/BufferSource-based buffers, getCenter() and several other
//   APIs were removed). The full implementation requires the 26.2 render API
//   rework. The text overlay and screen-space behaviour are unaffected.

public class TravelAnchorRenderer implements TravelRenderer<AnchorTravelTarget> {

    @Override
    public void render(AnchorTravelTarget travelData, LevelRenderer levelRenderer, PoseStack poseStack,
        double distanceSquared, boolean active, float partialTick) {
        // 26.2-port: rendering stubbed — see comment above
    }
}
