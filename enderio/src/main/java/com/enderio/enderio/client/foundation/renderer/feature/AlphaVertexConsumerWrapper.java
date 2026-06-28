package com.enderio.enderio.client.foundation.renderer.feature;

import com.enderio.enderio.config.machines.MachinesConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

// TODO: Currently only used by the IO config renderer, hence config value names
public class AlphaVertexConsumerWrapper extends VertexConsumerWrapper {
    public AlphaVertexConsumerWrapper(VertexConsumer vertexConsumer) {
        super(vertexConsumer);
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        super.setColor(r, g, b, (int)(MachinesConfig.CLIENT.IO_CONFIG_NEIGHBOUR_TRANSPARENCY.get().floatValue() * 255));
        return this;
    }

    @Override
    public VertexConsumer setColor(int packedColor) {
        super.setColor(ARGB.color(MachinesConfig.CLIENT.IO_CONFIG_NEIGHBOUR_TRANSPARENCY.get().floatValue(), packedColor));
        return this;
    }
}
