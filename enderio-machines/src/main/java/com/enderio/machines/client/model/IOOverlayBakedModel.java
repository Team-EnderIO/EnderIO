package com.enderio.machines.client.model;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.IOConfig;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IOOverlayBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEX_DISABLED = EIOMachines.loc("block/overlay/disabled");
    public static final ResourceLocation TEX_PULL = EIOMachines.loc("block/overlay/pull");
    public static final ResourceLocation TEX_PUSH = EIOMachines.loc("block/overlay/push");
    public static final ResourceLocation TEX_PUSH_PULL = EIOMachines.loc("block/overlay/push_pull");

    private static final EnumMap<Direction, Vec3[]> QUADS = new EnumMap<>(Direction.class);

    static {
        double leftEdge = 0.0625;
        double rightEdge = 1 - leftEdge;
        double topElevation = 1;
        double bottomElevation = 1 - topElevation;

        QUADS.put(Direction.UP, new Vec3[]{
            v(leftEdge, topElevation, leftEdge), v(leftEdge, topElevation, rightEdge), v(rightEdge, topElevation, rightEdge), v(rightEdge, topElevation, leftEdge)
        });
        QUADS.put(Direction.DOWN, new Vec3[]{
            v(leftEdge, bottomElevation, leftEdge), v(rightEdge, bottomElevation, leftEdge), v(rightEdge, bottomElevation, rightEdge), v(leftEdge, bottomElevation, rightEdge)
        });
        QUADS.put(Direction.EAST, new Vec3[]{
            v(topElevation, rightEdge, rightEdge), v(topElevation, leftEdge, rightEdge), v(topElevation, leftEdge, leftEdge), v(topElevation, rightEdge, leftEdge)
        });
        QUADS.put(Direction.WEST, new Vec3[]{
            v(bottomElevation, rightEdge, leftEdge), v(bottomElevation, leftEdge, leftEdge), v(bottomElevation, leftEdge, rightEdge), v(bottomElevation, rightEdge, rightEdge)
        });
        QUADS.put(Direction.NORTH, new Vec3[]{
            v(rightEdge, rightEdge, bottomElevation), v(rightEdge, leftEdge, bottomElevation), v(leftEdge, leftEdge, bottomElevation), v(leftEdge, rightEdge, bottomElevation)
        });
        QUADS.put(Direction.SOUTH, new Vec3[]{
            v(leftEdge, rightEdge, topElevation), v(leftEdge, leftEdge, topElevation), v(rightEdge, leftEdge, topElevation), v(rightEdge, rightEdge, topElevation)
        });
    }

    private TextureAtlasSprite getTexture(IOConfig.IOState ioState) {
        ResourceLocation tex = switch (ioState) {
            case NONE -> MissingTextureAtlasSprite.getLocation();
        case PUSH -> TEX_PUSH;
        case PULL -> TEX_PULL;
        case BOTH -> TEX_PUSH_PULL;
        case DISABLED -> TEX_DISABLED;
        };

        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(tex);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
        if (extraData.hasProperty(MachineBlockEntity.IO_CONFIG_PROPERTY)) {
            // Get io config from the block entity.
            IOConfig config = extraData.getData(MachineBlockEntity.IO_CONFIG_PROPERTY);
            if (config != null) {
                // Build a list of quads
                List<BakedQuad> quads = new ArrayList<>();

                // Get all states for each direction. If its not "None" then we render an overlay quad.
                for (Direction dir : Direction.values()) {
                    IOConfig.IOState ioState = config.getIO(dir);
                    if (ioState != IOConfig.IOState.NONE) {
                        Vec3[] verts = QUADS.get(dir);
                        quads.add(createQuad(verts[0], verts[1], verts[2], verts[3], getTexture(ioState)));
                    }
                }

                return quads;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    // Thanks McJty
    private void putVertex(BakedQuadBuilder builder, Vec3 normal,
        double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b) {

        ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements().asList();
        for (int j = 0 ; j < elements.size() ; j++) {
            VertexFormatElement e = elements.get(j);
            switch (e.getUsage()) {
            case POSITION:
                builder.put(j, (float) x, (float) y, (float) z, 1.0f);
                break;
            case COLOR:
                builder.put(j, r, g, b, 1.0f);
                break;
            case UV:
                switch (e.getIndex()) {
                case 0:
                    float iu = sprite.getU(u);
                    float iv = sprite.getV(v);
                    builder.put(j, iu, iv);
                    break;
                case 2:
                    builder.put(j, (short) 0, (short) 0);
                    break;
                default:
                    builder.put(j);
                    break;
                }
                break;
            case NORMAL:
                builder.put(j, (float) normal.x, (float) normal.y, (float) normal.z);
                break;
            default:
                builder.put(j);
                break;
            }
        }
    }

    private BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();
        int tw = sprite.getWidth();
        int th = sprite.getHeight();

        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, th, sprite, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v3.x, v3.y, v3.z, tw, th, sprite, 1.0f, 1.0f, 1.0f);
        putVertex(builder, normal, v4.x, v4.y, v4.z, tw, 0, sprite, 1.0f, 1.0f, 1.0f);
        return builder.build();
    }

    private static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }
}
