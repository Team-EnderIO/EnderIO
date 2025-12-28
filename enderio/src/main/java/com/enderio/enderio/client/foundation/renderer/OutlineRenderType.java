package com.enderio.enderio.client.foundation.renderer;

import com.enderio.enderio.EnderIO;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

public class OutlineRenderType extends RenderType {

    private static final Map<RenderType, OutlineRenderType> TYPES = new HashMap<>();

    private final RenderType parent;

    private OutlineRenderType(RenderType parent) {
        super("Outline" + parent.name, parent.bufferSize, parent.affectsCrumbling(), parent.sortOnUpload, parent::setupRenderState,
            parent::clearRenderState);
        this.parent = parent;
    }

    public static RenderType get(RenderType parent) {
        if (parent.name.contains("glint")) {
            return parent;
        } else if (parent instanceof OutlineRenderType) {
            return parent;
        } else {
            if (!TYPES.containsKey(parent)) {
                TYPES.put(parent, new OutlineRenderType(parent));
            }
            return TYPES.get(parent);
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "Outline" + this.parent;
    }

    @Override
    public void setupRenderState() {
        this.parent.setupRenderState();
//        if (Minecraft.getInstance().levelRenderer.entityOutlineTarget() != null) {
//            // noinspection ConstantConditions
//            Minecraft.getInstance().levelRenderer.entityOutlineTarget().bindWrite(false);
//        }
    }

    @Override
    public void clearRenderState() {
//        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        this.parent.clearRenderState();
    }

    @Override
    public void draw(MeshData meshData) {
        parent.draw(meshData);
    }

    @Override
    public VertexFormat format() {
        return parent.format();
    }

    @Override
    public VertexFormat.Mode mode() {
        return parent.mode();
    }

    //TODO this probably is wrong
    public static RenderType createLines(String name, int strength) {
        return RenderType.create(EnderIO.MOD_ID + "_" + name, 1536, false, false, LINES_NO_CULL,
            CompositeState.builder()
                .setLineState(new LineStateShard(OptionalDouble.of(strength)))
                .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                .createCompositeState(false));
    }

    public static final RenderPipeline LINES_NO_CULL = RenderPipelines.LINES.toBuilder()
        .withCull(false)
        .build();

}
