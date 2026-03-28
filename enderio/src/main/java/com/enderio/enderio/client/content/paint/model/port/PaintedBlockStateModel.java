package com.enderio.enderio.client.content.paint.model.port;

import com.enderio.core.data.model.ModelHelper;
import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PaintedBlockStateModel implements DynamicBlockStateModel {

    private final Block block;

    public PaintedBlockStateModel(Block block) {
        this.block = block;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        QuadCollection.Builder builder = new QuadCollection.Builder();

        List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
        directions.add(null);

        for (Direction side : directions) {
            List<BlockStateModelPart> shapeParts = new ArrayList<>();
            BlockState toCopy = block.defaultBlockState();
            for (Property prop : block.defaultBlockState().getProperties()) {
                if (state.hasProperty(prop)) {
                    toCopy = toCopy.setValue(prop, state.getValue(prop));
                }
            }
            Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(toCopy).collectParts(level, pos, state, random, shapeParts);
            List<BakedQuad> shape = shapeParts.stream().flatMap(p -> p.getQuads(side).stream()).toList();
            Direction directon = null;
            for (Property<?> property : state.getProperties()) {
                if (property.equals(BlockStateProperties.FACING)) { //TODO can't do instance of anymore, look into options
                    directon = state.getValue(BlockStateProperties.FACING).getOpposite();
                    break;
                }
            }

            List<BakedQuad> result = this.getQuadsUsingShape(level.getModelData(pos).get(SinglePaintedBlockEntity.PAINT), shape, side, directon, level, pos, random);
            for (BakedQuad quad : result) {
                if (directon != null) {
                    builder.addCulledFace(directon, quad);
                } else {
                    builder.addUnculledFace(quad);
                }
            }
        }

        parts.add(new SimpleModelWrapper(builder.build(), true, particleMaterial(level, pos, state)));
    }

    private List<BakedQuad> getQuadsUsingShape(Block paint, List<BakedQuad> shape, Direction side, Direction rotation, BlockAndTintGetter level, BlockPos pos, RandomSource random) {
        BlockState state = paintWithRotation(paint, rotation);
        var model = getModel(state);

        Optional<List<Pair<BakedColors, BakedQuad.MaterialInfo>>> spriteOptional = getSpriteData(paint, side, rotation, level, pos, random);
        List<BakedQuad> returnQuads = new ArrayList<>();
        for (BakedQuad shapeQuad : shape) {
            List<Pair<BakedColors, BakedQuad.MaterialInfo>> spriteData = spriteOptional.orElseGet(() -> getSpriteFromModel(shapeQuad, model, paint, rotation, level, pos));
            returnQuads.addAll(paintQuad(shapeQuad, spriteData));
        }
        return returnQuads;
    }

    @Override
    public Material.Baked particleMaterial() {
        return new Material.Baked(ModelHelper.getMissingTexture(), false);
    }

    @Override
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        Block paint = level.getModelData(pos).get(SinglePaintedBlockEntity.PAINT);
        if (paint == null) {
            return particleMaterial();
        }

        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(paint.defaultBlockState());
    }

    private BlockState paintWithRotation(Block paint, @Nullable Direction rotation) {
        BlockState state = paint.defaultBlockState();
        if (rotation != null) {
            state = state.setValue(BlockStateProperties.FACING, rotation);
        }
        return state;
    }

    /**
     * Get the block model for a given block state.
     */
    private BlockStateModel getModel(BlockState state) {
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
    }

    /**
     * @param paint
     * @param side     the side you want the texture from
     * @param random
     * @param rotation a rotation value, so that if both blocks support rotation, the correct texture is gathered
     * @return an Optional of a Pair of the texture of the Block and if the texture is tinted at that side
     */
    private Optional<List<Pair<BakedColors, BakedQuad.MaterialInfo>>> getSpriteData(Block paint, @Nullable Direction side, @Nullable Direction rotation, BlockAndTintGetter level, BlockPos pos, RandomSource random) {
        BlockState state = paintWithRotation(paint, rotation);
        List<BlockStateModelPart> parts = new ArrayList<>();
        getModel(state).collectParts(level, pos, state, random, parts);
        List<BakedQuad> quads = parts.stream().flatMap(p -> p.getQuads(side).stream()).toList();
        return quads.isEmpty() ? Optional.empty()
            : Optional.of(quads.stream().map(q-> Pair.of(q.bakedColors(), q.materialInfo())).toList());
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
    protected List<Pair<BakedColors, BakedQuad.MaterialInfo>> getSpriteFromModel(BakedQuad shape, BlockStateModel model, Block paint,
        Direction rotation,  BlockAndTintGetter level, BlockPos pos) {
        BlockState state = paintWithRotation(paint, rotation);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(level, pos, state, RandomSource.create(), parts);
        List<BakedQuad> quads = parts.stream().flatMap(p -> p.getQuads(shape.direction()).stream()).toList();
        return quads.isEmpty() ? null : quads.stream().map(q-> Pair.of(q.bakedColors(), q.materialInfo())).toList();
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
     * @return a new Quad with the same coordinates but a different texture
     */
    protected List<BakedQuad> paintQuad(BakedQuad toCopy,  @UnknownNullability List<Pair<BakedColors, BakedQuad.MaterialInfo>> sprites) {
        List<BakedQuad> quads = new ArrayList<>();
        MutableQuad copied = new MutableQuad();
        for (var sprite : sprites) {
            copied.setFrom(toCopy);
            BakedQuad.MaterialInfo info = sprite.getSecond();
            if (sprite.getSecond() == null) {
                copied.setSprite(ModelHelper.getMissingTexture(), ChunkSectionLayer.SOLID, null);
            } else {
                copied.setSpriteAndMoveUv(info.sprite(), info.layer(), info.itemRenderType());
                copied.setColor(sprite.getFirst());
                copied.setShade(info.shade());
                copied.setLightEmission(info.lightEmission());
                copied.setAmbientOcclusion(info.ambientOcclusion());
                copied.setTintIndex(info.tintIndex());
            }
            quads.add(copied.toBakedQuad());
        }
        return quads.reversed();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        // TODO: 26.1 can we refine this? for now we'll just return all flags lol
        return 0xFFFFFFFF;
    }

    public record Unbaked(Block block) implements CustomUnbakedBlockStateModel {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(Unbaked::block))
                .apply(builder, Unbaked::new));

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new PaintedBlockStateModel(block);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {

        }

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return MAP_CODEC;
        }
    }
}
