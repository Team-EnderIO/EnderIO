package com.enderio.machines.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
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

// TODO: This will be used for the item
public class TankFluidBakedModel implements IDynamicBakedModel {
    private static final EnumMap<Direction, Vec3[]> QUADS = new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.values()) {
            QUADS.put(dir, ModelRenderUtil.createQuadVerts(dir, 1/16f, 1 - 1/16f, 1 - 1/16f));
        }
    }

    private final Direction north;

    public TankFluidBakedModel(ModelState transform) {
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

    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation("block/water_still")); // TODO: This gonna need dynamic colouring for fluid types :/
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
        // Build a list of quads
        List<BakedQuad> quads = new ArrayList<>();

        // Get all states for each direction. If its not "None" then we render an overlay quad.
        for (Direction dir : Direction.values()) {
            Vec3[] verts = QUADS.get(getWorldDirection(dir));
            quads.add(ModelRenderUtil.createQuad(verts, getTexture(), Fluids.WATER.getAttributes().getColor()));
        }

        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
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
        return null;
    }

    public static class Geometry implements IModelGeometry<Geometry> {
        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
            ItemOverrides overrides, ResourceLocation modelLocation) {
            return new TankFluidBakedModel(modelTransform);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
            Set<Pair<String, String>> missingTextureErrors) {
            return Collections.emptyList();
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
