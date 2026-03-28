package com.enderio.enderio.client.content.paint.model.port;

import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaintedItemModel implements ItemModel {
    private static final RandomSource RANDOM = RandomSource.create();

    private final BlockState modelState;
    private final ItemTransforms itemTransforms;
    private final List<BlockStateModelPart> partScratchList = new ObjectArrayList<>();

    public PaintedItemModel(BlockState model, ItemTransforms itemTransforms) {
        this.modelState = model;
        this.itemTransforms = itemTransforms;
    }

    @Override
    public void update(ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver,
        ItemDisplayContext itemDisplayContext, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int i) {

        var data = itemStack.getComponents().get(EIODataComponents.BLOCK_PAINT);

        if (data == null) {
            return;
        }

        itemStackRenderState.appendModelIdentityElement(this);
        itemStackRenderState.appendModelIdentityElement(data.paint());

        var model = getItemModel(data.paint());

        ItemStackRenderState.LayerRenderState layer = itemStackRenderState.newLayer();
        layer.prepareQuadList().addAll(model.quads);
        model.properties.applyToLayer(layer, itemDisplayContext);

        if (model.animated) {
            itemStackRenderState.setAnimated();
        }

        if (!model.tints.isEmpty()) {
            layer.tintLayers().addAll(model.tints);
        }

    }

    public ModelState getItemModel(Block paint) {
        ModelData modelData =  ModelData.of(SinglePaintedBlockEntity.PAINT, paint);
        var model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(modelState);
        BlockAndTintGetter level = new BlockAndTintGetter() {

            @Override
            public BlockState getBlockState(BlockPos pos) {
                if (pos.equals(pos())) {
                    return modelState;
                }
                return Blocks.AIR.defaultBlockState();
            }

            @Nullable
            @Override
            public BlockEntity getBlockEntity(BlockPos pos) {
                return null;
            }

            @Override
            public FluidState getFluidState(BlockPos pos) {
                return getBlockState(pos).getFluidState();
            }

            @Override
            public int getBrightness(LightLayer layer, BlockPos pos) {
                return LightEngine.MAX_LEVEL;
            }

            @Override
            public ModelData getModelData(BlockPos pos) {
                if (pos.equals(pos())) {
                    return modelData;
                }
                return ModelData.EMPTY;
            }

            public BlockPos pos() {
                return BlockPos.ZERO;
            }

            @Override
            public LevelLightEngine getLightEngine() {
                return LevelLightEngine.EMPTY;
            }

            @Override
            public CardinalLighting cardinalLighting() {
                return CardinalLighting.DEFAULT;
            }

            @Override
            public int getBlockTint(BlockPos pos, ColorResolver resolver) {
                return -1;
            }

            @Override
            public int getHeight() {
                return 1;
            }

            @Override
            public int getMinY() {
                return pos().getY();
            }
        };

        ArrayList<BakedQuad> allQuads = new ArrayList<>();
        boolean animated = false;

        RANDOM.setSeed(42);
        model.collectParts(level, BlockPos.ZERO, modelState, RANDOM, partScratchList);
        List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
        directions.add(null);
        for (BlockStateModelPart modelPart : partScratchList) {
            animated |= (modelPart.materialFlags() & BakedQuad.FLAG_ANIMATED) != 0;
            for (Direction face : directions) {
                RANDOM.setSeed(42);
                allQuads.addAll(modelPart.getQuads(face));
            }
        }
        partScratchList.clear();

        var tints = IntArrayList.toList(Minecraft.getInstance().getBlockColors().getTintSources(paint.defaultBlockState()).stream()
            .mapToInt(t-> t.color(paint.defaultBlockState())));

        ModelRenderProperties renderProps = new ModelRenderProperties(true, model.particleMaterial(BlockAndTintGetter.EMPTY, BlockPos.ZERO, modelState), itemTransforms);

        return new ModelState(allQuads, tints, renderProps, animated);
    }

    public record ModelState(List<BakedQuad> quads, IntList tints, ModelRenderProperties properties, boolean animated) {}


    public record Unbaked(BlockState model) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                BlockState.CODEC.fieldOf("model").forGetter(Unbaked::model))
            .apply(builder, Unbaked::new));

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return CODEC;
        }

        @Override
        public ItemModel bake(BakingContext bakingContext, Matrix4fc matrix4fc) {
            ItemTransforms transforms = bakingContext.blockModelBaker().getModel(ModelLocationUtils.getModelLocation(Blocks.GRASS_BLOCK)).getTopTransforms();
            return new PaintedItemModel(model, transforms);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {

        }
    }
}
