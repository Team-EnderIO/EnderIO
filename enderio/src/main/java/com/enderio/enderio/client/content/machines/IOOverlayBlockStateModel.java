package com.enderio.enderio.client.content.machines;

import com.enderio.core.data.model.ModelHelper;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.io.IOConfigurable;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.client.foundation.model.ModelRenderUtil;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.List;

public class IOOverlayBlockStateModel implements DynamicBlockStateModel {

    public static final Identifier TEX_DISABLED = EnderIO.rl("block/overlay/disabled");
    public static final Identifier TEX_PULL = EnderIO.rl("block/overlay/pull");
    public static final Identifier TEX_PUSH = EnderIO.rl("block/overlay/push");
    public static final Identifier TEX_PUSH_PULL = EnderIO.rl("block/overlay/push_pull");

    private static final EnumMap<Direction, Vector3f[]> QUADS = new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.values()) {
            QUADS.put(dir, ModelRenderUtil.createQuadVerts(dir, 0.0625f, 1 - 0.0625f, 1.0001f));
        }
    }

    private final BlockModelPart model;

    public IOOverlayBlockStateModel(BlockModelPart model) {
        this.model = model;
    }

    private TextureAtlasSprite getSprite(IOMode state) {
        Identifier tex = switch (state) {
            case NONE -> MissingTextureAtlasSprite.getLocation();
            case PUSH -> TEX_PUSH;
            case PULL -> TEX_PULL;
            case BOTH -> TEX_PUSH_PULL;
            case DISABLED -> TEX_DISABLED;
        };

        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(tex);
    }


    @Override
    public TextureAtlasSprite particleIcon() {
        return model.particleIcon();
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
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
                        collection.addCulledFace(dir, ModelRenderUtil.createQuad(verts, getSprite(mode)));
                    }
                }

                var quads = collection.build();

                parts.add(new BlockModelPart() {
                    @Override
                    public List<BakedQuad> getQuads(@Nullable Direction direction) {
                        return quads.getQuads(direction);
                    }

                    @Override
                    public boolean useAmbientOcclusion() {
                        return false;
                    }

                    @Override
                    public TextureAtlasSprite particleIcon() {
                        return ModelHelper.getMissingTexture();
                    }

                    @Override
                    public ChunkSectionLayer getRenderType(BlockState state) {
                        return ChunkSectionLayer.CUTOUT;
                    }
                });
            }

            parts.add(model);
        }
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
