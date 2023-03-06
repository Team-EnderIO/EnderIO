package com.enderio.machines.client.gui.widget.ioconfig;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Thanks XFactHD/FramedBlocks and ApexStudios-Dev/FantasyFurniture/
 * Modification to default {@link VertexConsumer} which overrides alpha to allow semi-transparent rendering
 */
record TransparentVertexConsumer(VertexConsumer delegate, int alpha) implements VertexConsumer {
    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        return delegate.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return delegate.color(red, green, blue, (alpha * this.alpha) / 0xFF);
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        return delegate.uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return delegate.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return delegate.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return delegate.normal(x, y, z);
    }

    @Override
    public void endVertex() {
        delegate.endVertex();
    }

    @Override
    public void defaultColor(int defaultR, int defaultG, int defaultB, int defaultA) {
        delegate.defaultColor(defaultR, defaultG, defaultB, defaultA);
    }

    @Override
    public void unsetDefaultColor() {
        delegate.unsetDefaultColor();
    }
}
