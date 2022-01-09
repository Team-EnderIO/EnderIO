package com.enderio.machines.client.rendering.model;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.IOConfig;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class IOOverlayBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEX_DISABLED = EIOMachines.loc("block/overlay/disabled");
    public static final ResourceLocation TEX_PULL = EIOMachines.loc("block/overlay/pull");
    public static final ResourceLocation TEX_PUSH = EIOMachines.loc("block/overlay/push");
    public static final ResourceLocation TEX_PUSH_PULL = EIOMachines.loc("block/overlay/push_pull");

    private static final EnumMap<Direction, Vec3[]> QUADS = new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.values()) {
            QUADS.put(dir, ModelRenderUtil.createQuadVerts(dir, 0.0625f, 1 - 0.0625f, 1));
        }
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

    private final Direction north;

    public IOOverlayBakedModel(ModelState transform) {
        this.north = Direction.rotate(transform.getRotation().getMatrix(), Direction.NORTH);
    }

    // Gets the direction local to the rendered block, rather than the query
    private Direction getWorldDirection(Direction direction) {
        return switch (direction) {
        case NORTH -> this.north;
        case SOUTH -> this.north.getOpposite();
        case WEST -> this.north.getCounterClockWise();
        case EAST -> this.north.getClockWise();
        default -> direction;
        };
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
                        Vec3[] verts = QUADS.get(getWorldDirection(dir));
                        quads.add(ModelRenderUtil.createQuad(verts, getTexture(ioState)));
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

    public static class Geometry implements IModelGeometry<Geometry> {
        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
            ItemOverrides overrides, ResourceLocation modelLocation) {
            return new IOOverlayBakedModel(modelTransform);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
            Set<Pair<String, String>> missingTextureErrors) {
            return List.of(
                new Material(TextureAtlas.LOCATION_BLOCKS, EIOMachines.loc("block/overlay/disabled")),
                new Material(TextureAtlas.LOCATION_BLOCKS, EIOMachines.loc("block/overlay/pull")),
                new Material(TextureAtlas.LOCATION_BLOCKS, EIOMachines.loc("block/overlay/push")),
                new Material(TextureAtlas.LOCATION_BLOCKS, EIOMachines.loc("block/overlay/push_pull"))
            );
        }
    }

    public static class Loader implements IModelLoader<Geometry> {
        @Override
        public Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            return new Geometry();
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {

        }
    }

}
