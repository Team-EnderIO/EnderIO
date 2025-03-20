package com.enderio.base.client.paint.model;

import com.enderio.base.client.paint.PaintedBlockColor;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.paint.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.paint.blockentity.PaintedBlockEntity;
import com.enderio.base.common.paint.blockentity.SinglePaintedBlockEntity;
import com.enderio.core.client.RenderUtil;
import com.enderio.core.data.model.ModelHelper;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class PaintedBlockModel implements IDynamicBakedModel {

    private final Map<Block, List<BakedModel>> itemRenderCache = new HashMap<>();

    /**
     * The block which model we retexture
     */
    private final Block reference;

    /**
     * Rotate the item model perspective.
     * Used to fix stairs.
     */
    @Nullable
    private final Direction rotateItemTo;

    public PaintedBlockModel(Block reference, @Nullable Direction rotateItemTo) {
        this.reference = reference;
        this.rotateItemTo = rotateItemTo;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
            ModelData extraData, @Nullable RenderType renderType) {
        if (state != null) {
            BlockState replicaState = replicateState(state);

            if (state.hasProperty(SlabBlock.TYPE)) {
                // Support slabs with different textures on top and bottom
                List<BakedQuad> quads = new ArrayList<>();
                SlabType slabType = state.getValue(SlabBlock.TYPE);

                // Bottom slab
                if (slabType == SlabType.BOTTOM || slabType == SlabType.DOUBLE) {
                    Block paint = extraData.get(DoublePaintedBlockEntity.PAINT);
                    // @formatter:off
                    List<BakedQuad> shape = getModel(replicaState.setValue(SlabBlock.TYPE, SlabType.BOTTOM))
                        .getQuads(state, side, rand, ModelData.EMPTY, renderType);
                    // @formatter:on
                    IQuadTransformer transformer = quad -> quad.tintIndex = PaintedBlockColor
                            .moveTintIndex(quad.getTintIndex());
                    quads.addAll(transformer.process(getQuadsUsingShape(paint, shape, side, rand, null, renderType)));
                }

                // Top slab
                if (slabType == SlabType.TOP || slabType == SlabType.DOUBLE) {
                    Block paint = extraData.get(DoublePaintedBlockEntity.PAINT2);
                    // @formatter:off
                    List<BakedQuad> shape = getModel(replicaState.setValue(SlabBlock.TYPE, SlabType.TOP))
                        .getQuads(state, side, rand, ModelData.EMPTY, renderType);
                    // @formatter:on
                    quads.addAll(getQuadsUsingShape(paint, shape, side, rand, null, renderType));
                }

                return quads;
            } else {
                // Simple model
                List<BakedQuad> shape = getModel(replicaState).getQuads(replicaState, side, rand);
                Direction direction = null;
                for (Property<?> property : state.getProperties()) {
                    if (property instanceof DirectionProperty directionProperty) {
                        direction = state.getValue(directionProperty).getOpposite();
                    }
                }

                return getQuadsUsingShape(extraData.get(SinglePaintedBlockEntity.PAINT), shape, side, rand, direction,
                        renderType);
            }
        }

        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
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
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        Block paint = data.get(SinglePaintedBlockEntity.PAINT);
        if (paint != null) {
            BakedModel model = getModel(paint.defaultBlockState());
            TextureAtlasSprite sprite = model.getParticleIcon(ModelData.EMPTY);
            if (!sprite.contents().name().getPath().equals("missingno")) {
                return sprite;
            }
        }

        if (data.has(DoublePaintedBlockEntity.PAINT2)) {
            paint = data.get(DoublePaintedBlockEntity.PAINT2);
            if (paint != null) {
                BakedModel model = getModel(paint.defaultBlockState());
                return model.getParticleIcon(ModelData.EMPTY);
            }
        }

        return ModelHelper.getMissingTexture();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return ModelHelper.getMissingTexture();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState paintedBlockState, RandomSource rand, ModelData data) {
        ChunkRenderTypeSet chunkRenderTypeSet = ChunkRenderTypeSet
                .union(PaintedBlockEntity.PAINT_DATA_PROPERTIES.stream()
                        .map(data::get)
                        // remove all unset paints
                        .filter(Objects::nonNull)
                        .map(Block::defaultBlockState)
                        // do original ChunkRenderType lookup
                        .map(state -> getModel(state).getRenderTypes(state, rand, ModelData.EMPTY))
                        .toList());

        if (chunkRenderTypeSet.isEmpty()) {
            return ChunkRenderTypeSet.of(RenderType.solid());
        }
        return chunkRenderTypeSet;
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        if (!itemStack.has(EIODataComponents.BLOCK_PAINT)) {
            return List.of(fabulous ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet());
        }

        var paintData = itemStack.get(EIODataComponents.BLOCK_PAINT);
        return List.of(ItemBlockRenderTypes.getRenderType(paintData.paint().defaultBlockState(), fabulous));
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        if (!itemStack.has(EIODataComponents.BLOCK_PAINT)) {
            return List.of();
        }

        var paintData = itemStack.get(EIODataComponents.BLOCK_PAINT);
        return itemRenderCache.computeIfAbsent(paintData.paint(),
                paintKey -> List.of(new ItemModel(paintData.paint())));
    }

    @Override
    public ItemTransforms getTransforms() {
        return getItemModel().getTransforms();
    }

    // region Model Shadowing Logic

    /**
     * Get the block model for a given block state.
     */
    private BakedModel getModel(BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    }

    /**
     * Get the reference block's item model.
     */
    private BakedModel getItemModel() {
        return Minecraft.getInstance()
                .getItemRenderer()
                .getModel(reference.asItem().getDefaultInstance(), null, null, 0);
    }

    /**
     * Replicate the painted block state as the reference block.
     */
    private BlockState replicateState(@Nullable BlockState selfState) {
        BlockState toState = reference.defaultBlockState();
        if (selfState == null) {
            return toState;
        }

        for (Property<?> property : selfState.getProperties()) {
            if (property instanceof BooleanProperty booleanProperty && toState.hasProperty(booleanProperty)) {
                toState = toState.setValue(booleanProperty, selfState.getValue(booleanProperty));
            }

            // noinspection rawtypes,unchecked
            if (property instanceof EnumProperty enumProperty && toState.hasProperty(enumProperty)) {
                // noinspection unchecked
                toState = toState.setValue(enumProperty, selfState.getValue(enumProperty));
            }
        }

        return toState;
    }

    /**
     * @param paint
     * @param shape    quads you want to copy
     * @param side     side to render
     * @param rand
     * @param rotation rotation you want to have applied to the block
     * @return a List of BakedQuads from a shape using the paint as a texture
     */
    protected List<BakedQuad> getQuadsUsingShape(@Nullable Block paint, List<BakedQuad> shape, @Nullable Direction side,
            RandomSource rand, @Nullable Direction rotation, @Nullable RenderType renderType) {
        if (paint != null) {
            BakedModel model = getModel(paintWithRotation(paint, rotation));
            Optional<Pair<TextureAtlasSprite, Boolean>> spriteOptional = getSpriteData(paint, side, rand, rotation,
                    renderType);
            List<BakedQuad> returnQuads = new ArrayList<>();
            for (BakedQuad shapeQuad : shape) {
                Pair<TextureAtlasSprite, Boolean> spriteData = spriteOptional
                        .orElseGet(() -> getSpriteFromModel(shapeQuad, model, paint, rotation));
                returnQuads.add(paintQuad(shapeQuad, spriteData.getFirst(), spriteData.getSecond()));
            }
            return returnQuads;
        }
        return List.of();
    }

    private BlockState paintWithRotation(Block paint, @Nullable Direction rotation) {
        BlockState state = paint.defaultBlockState();
        if (rotation != null) {
            for (Property<?> property : state.getProperties()) {
                if (property instanceof DirectionProperty directionProperty
                        && directionProperty.getPossibleValues().contains(rotation)) {
                    state = state.setValue(directionProperty, rotation);
                }
            }
        }
        return state;
    }

    /**
     * @param paint
     * @param side     the side you want the texture from
     * @param rand
     * @param rotation a rotation value, so that if both blocks support rotation, the correct texture is gathered
     * @return an Optional of a Pair of the texture of the Block and if the texture is tinted at that side
     */
    private Optional<Pair<TextureAtlasSprite, Boolean>> getSpriteData(Block paint, @Nullable Direction side,
            RandomSource rand, @Nullable Direction rotation, @Nullable RenderType renderType) {
        BlockState state = paintWithRotation(paint, rotation);
        List<BakedQuad> quads = getModel(state).getQuads(state, side, rand, ModelData.EMPTY, renderType);
        return quads.isEmpty() ? Optional.empty()
                : Optional.of(Pair.of(quads.get(0).getSprite(), quads.get(0).isTinted()));
    }

    /**
     * A fallback for {@link this.getSpriteData}. Mostly used for if the Original Block doesn't have a texture for the null side.
     * That is the case for all textures not on the faces of a full block.
     * This method uses the BakedQuad of the Shape to unpack it's VertextData. This time at Element 4 which represents NormalData (Direction Data).
     * For more information of the Unpacking of data take a look at {@link this.copyQuad}. After that the nearest direction for the normal data is used to query the blockmodel again to hopefully get the correct texturedata.
     * If it can't find a correct Quad for that direction the missing texture is returned.
     *
     * @param shape
     * @param model
     * @param paint
     * @param rotation
     * @return Returns TextureData from baked model information. Is slower than the primary method, so this is just a fallback.
     */
    protected Pair<TextureAtlasSprite, Boolean> getSpriteFromModel(BakedQuad shape, BakedModel model, Block paint,
            Direction rotation) {
        BlockState state = paintWithRotation(paint, rotation);
        List<BakedQuad> quads = model.getQuads(state, shape.getDirection(), RandomSource.create());
        return quads.isEmpty() ? Pair.of(ModelHelper.getMissingTexture(), false)
                : Pair.of(quads.get(0).getSprite(), quads.get(0).isTinted());
    }

    /**
     * This method copies a quad from the shape and modifies it to create one, that display the new texture.
     * First it copies the quad with the values of the shape quad. The new Sprite and tintValues are added to the quad.
     * After that this method gets UV-Data out of the shape quad and modifies it to match the texture of the paint with their offsets. This Data is packed into an Int Array in the vertex data.
     * The unpacked representation of the data uses a float[4][6][4] where the first dimension is for each vertex, the second is for the dataType and the third is for different types of values like x1,y1,x2,y2 of uvdata
     * To use the data this method unpacks the UV-Data for each vertex and get element 2 which is for UV-Start-Data (UV0). To get the other element types you can look at {@link DefaultVertexFormat#BLOCK}
     * I modify the unpacked UV-Data by getting the relative offset to the texturestart-coordinates by subtracting it from the texturestart and dividing it by the width of the texture.
     * To get the values for the new textures I multiply that value by the size of the new texture and adding the texture start data pack. I then pack that float data back into the int form and put that into the cloned quad.
     *
     * @param toCopy     shapeQuad you want to copy
     * @param sprite     sprite that should be used
     * @param shouldTint is that quad on the texture tinted
     * @return a new Quad with the same coordinates but a different texture
     */
    protected BakedQuad paintQuad(BakedQuad toCopy, TextureAtlasSprite sprite, boolean shouldTint) {
        BakedQuad copied = new BakedQuad(Arrays.copyOf(toCopy.getVertices(), 32), shouldTint ? 1 : -1,
                toCopy.getDirection(), sprite, toCopy.isShade());

        for (int i = 0; i < 4; i++) {
            float[] uv0 = RenderUtil.unpackVertices(copied.getVertices(), i, IQuadTransformer.UV0, 2);
            uv0[0] = (uv0[0] - toCopy.getSprite().getU0()) * sprite.contents().width()
                    / toCopy.getSprite().contents().width() + sprite.getU0();
            uv0[1] = (uv0[1] - toCopy.getSprite().getV0()) * sprite.contents().height()
                    / toCopy.getSprite().contents().height() + sprite.getV0();
            int[] packedTextureData = RenderUtil.packUV(uv0[0], uv0[1]);
            copied.getVertices()[IQuadTransformer.UV0 + i * IQuadTransformer.STRIDE] = packedTextureData[0];
            copied.getVertices()[IQuadTransformer.UV0 + 1 + i * IQuadTransformer.STRIDE] = packedTextureData[1];
        }
        return copied;
    }

    // endregion

    /**
     * Simple pass-through item model.
     */
    private class ItemModel implements IDynamicBakedModel {

        private final Block paint;
        private final Map<Direction, List<BakedQuad>> bakedQuads = new HashMap<>();

        private ItemModel(Block paint) {
            this.paint = paint;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
                ModelData extraData, @Nullable RenderType renderType) {
            return bakedQuads.computeIfAbsent(side,
                    side1 -> getQuadsUsingShape(paint,
                            getItemModel().getQuads(state, side, rand, ModelData.EMPTY, renderType), side1, rand,
                            rotateItemTo, renderType));
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return true;
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
            return PaintedBlockModel.this.getParticleIcon();
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }

        @Override
        public ItemTransforms getTransforms() {
            return getItemModel().getTransforms();
        }
    }
}
