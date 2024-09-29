package com.enderio.machines.client.rendering.travel;

import com.enderio.EnderIOBase;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

public class OutlineRenderType extends RenderType {

    private static final Map<RenderType, OutlineRenderType> TYPES = new HashMap<>();

    private final RenderType parent;

    private OutlineRenderType(RenderType parent) {
        super("Outline" + parent.name, parent.format(), parent.mode(), parent.bufferSize(), parent.affectsCrumbling(), parent.sortOnUpload,
            parent::setupRenderState, parent::clearRenderState);
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
        if (Minecraft.getInstance().levelRenderer.entityTarget() != null) {
            //noinspection ConstantConditions
            Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false);
        }
    }

    @Override
    public void clearRenderState() {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        this.parent.clearRenderState();
    }

    public static RenderType createLines(String name, int strength) {
        return RenderType.create(EnderIOBase.REGISTRY_NAMESPACE + "_" + name, DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, false, false,
            CompositeState
                .builder()
                .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                .setLineState(new LineStateShard(OptionalDouble.of(strength)))
                .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(false));
    }
}
