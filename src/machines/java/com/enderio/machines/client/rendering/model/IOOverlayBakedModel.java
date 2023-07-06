package com.enderio.machines.client.rendering.model;

import com.enderio.EnderIO;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public class IOOverlayBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEX_DISABLED = EnderIO.loc("block/overlay/disabled");
    public static final ResourceLocation TEX_PULL = EnderIO.loc("block/overlay/pull");
    public static final ResourceLocation TEX_PUSH = EnderIO.loc("block/overlay/push");
    public static final ResourceLocation TEX_PUSH_PULL = EnderIO.loc("block/overlay/push_pull");

    private static final EnumMap<Direction, Vec3[]> QUADS = new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.values()) {
            QUADS.put(dir, ModelRenderUtil.createQuadVerts(dir, 0.0625f, 1 - 0.0625f, 1.0001));
        }
    }

    private TextureAtlasSprite getTexture(IOMode state) {
        ResourceLocation tex = switch (state) {
            case NONE -> MissingTextureAtlasSprite.getLocation();
            case PUSH -> TEX_PUSH;
            case PULL -> TEX_PULL;
            case BOTH -> TEX_PUSH_PULL;
            case DISABLED -> TEX_DISABLED;
        };

        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(tex);
    }

    private final Direction north;

    public IOOverlayBakedModel(ModelState modelState) {
        this.north = Direction.rotate(modelState.getRotation().getMatrix(), Direction.NORTH);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        if (extraData.has(MachineBlockEntity.IO_CONFIG_PROPERTY)) {
            // Get io config from the block entity.
            IIOConfig config = extraData.get(MachineBlockEntity.IO_CONFIG_PROPERTY);
            if (config != null && config.renderOverlay()) {
                // Build a list of quads
                List<BakedQuad> quads = new ArrayList<>();

                // Get all states for each direction. If its not "None" then we render an overlay quad.
                for (Direction dir : Direction.values()) {
                    IOMode mode = config.getMode(dir);
                    if (mode != IOMode.NONE) {
                        Vec3[] verts = QUADS.get(dir);
                        quads.add(ModelRenderUtil.createQuad(verts, getTexture(mode)));
                    }
                }

                return quads;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.cutout());
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

    public static class Geometry implements IUnbakedGeometry<Geometry> {
        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
            ItemOverrides overrides, ResourceLocation modelLocation) {
            return new IOOverlayBakedModel(modelState);
        }
    }

    public static class Loader implements IGeometryLoader<Geometry> {
        @Override
        public Geometry read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
            return new Geometry();
        }
    }

}
