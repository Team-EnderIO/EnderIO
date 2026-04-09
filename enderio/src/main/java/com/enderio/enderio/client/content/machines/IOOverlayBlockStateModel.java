package com.enderio.enderio.client.content.machines;

import com.enderio.core.data.model.ModelHelper;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.client.foundation.model.ModelRenderUtil;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.List;

public class IOOverlayBlockStateModel implements DynamicBlockStateModel {

    public static final Identifier TEX_DISABLED = EnderIO.id("block/overlay/disabled");
    public static final Identifier TEX_PULL = EnderIO.id("block/overlay/pull");
    public static final Identifier TEX_PUSH = EnderIO.id("block/overlay/push");
    public static final Identifier TEX_PUSH_PULL = EnderIO.id("block/overlay/push_pull");

    private static final EnumMap<Direction, Vector3f[]> QUADS = new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.values()) {
            QUADS.put(dir, ModelRenderUtil.createQuadVerts(dir, 0.0625f, 1 - 0.0625f, 1.0001f));
        }
    }

    private final BlockStateModelPart model;

    public IOOverlayBlockStateModel(BlockStateModelPart model) {
        this.model = model;
    }

    private BakedQuad.MaterialInfo getMaterialInfo(IOMode state) {
        Identifier tex = switch (state) {
            case NONE -> MissingTextureAtlasSprite.getLocation();
            case PUSH -> TEX_PUSH;
            case PULL -> TEX_PULL;
            case BOTH -> TEX_PUSH_PULL;
            case DISABLED -> TEX_DISABLED;
        };

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(tex);
        return BakedQuad.MaterialInfo.of(new Material.Baked(sprite, false), Transparency.TRANSLUCENT, -1, true, 0, true);
    }


    @Override
    public Material.Baked particleMaterial() {
        return model.particleMaterial();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return model.materialFlags();
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        if (level.getModelData(pos).has(MachineBlockEntity.IO_CONFIG_PROPERTY)) {
            // Get io config from the block entity.
            IOConfigurable config = level.getModelData(pos).get(MachineBlockEntity.IO_CONFIG_PROPERTY);
            if (config != null && config.shouldRenderIOConfigOverlay()) {
                // Build a list of quads
                var collection = new QuadCollection.Builder();

                // Get all states for each direction. If its not "None" then we render an
                // overlay quad.
                for (Direction dir : Direction.values()) {
                    IOMode mode = config.getIOMode(dir);
                    if (mode != IOMode.NONE) {
                        Vector3f[] verts = QUADS.get(dir);
                        collection.addCulledFace(dir, ModelRenderUtil.createQuad(verts, getMaterialInfo(mode)));
                    }
                }

                var quads = collection.build();

                parts.add(new BlockStateModelPart() {
                    @Override
                    public List<BakedQuad> getQuads(@Nullable Direction direction) {
                        return quads.getQuads(direction);
                    }

                    @Override
                    public boolean useAmbientOcclusion() {
                        return false;
                    }

                    @Override
                    public Material.Baked particleMaterial() {
                        return new Material.Baked(ModelHelper.getMissingTexture(), false);
                    }

                    @Override
                    public @BakedQuad.MaterialFlags int materialFlags() {
                        return quads.materialFlags();
                    }
                });
            }
        }

        parts.add(model);
    }

    public record Unbaked(Variant variant) implements CustomUnbakedBlockStateModel {
        public static final MapCodec<Unbaked> MAP_CODEC = Variant.MAP_CODEC.xmap(Unbaked::new, Unbaked::variant);

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new IOOverlayBlockStateModel(variant.bake(baker));
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            variant.resolveDependencies(resolver);
        }

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return MAP_CODEC;
        }
    }
}
