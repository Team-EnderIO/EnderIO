package com.enderio.decoration.client;

import com.enderio.decoration.client.model.painted.PaintedSimpleModel;
import com.enderio.decoration.client.model.painted.PaintedSlabModel;
import com.enderio.decoration.common.blockentity.IPaintableBlockEntity;
import com.enderio.decoration.common.init.DecorBlocks;
import com.enderio.decoration.common.util.PaintUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    private ClientSetup() {}

    @SubscribeEvent
    public static void modelInit(ModelEvent.RegisterGeometryLoaders event) {
        event.register("painted_model", new WrappedModelLoader(false));
        event.register("painted_slab", new WrappedModelLoader(true));
    }

    @SubscribeEvent
    public static void colorItemInit(RegisterColorHandlersEvent.Item event) {
        // TODO: Move into registrate.
        PaintedBlockColor color = new PaintedBlockColor();
        event.register(color, DecorBlocks.getPainted().toArray(new Block[0]));
        event.register(color, DecorBlocks.PAINTED_SLAB.get());
    }
    @SubscribeEvent
    public static void colorItemInit(RegisterColorHandlersEvent.Block event) {
        // TODO: Move into registrate.
        PaintedBlockColor color = new PaintedBlockColor();
        event.register(color, DecorBlocks.getPainted().toArray(new Block[0]));
        event.register(color, DecorBlocks.PAINTED_SLAB.get());
    }

    private static class PaintedBlockColor implements BlockColor, ItemColor {

        @Override
        public int getColor(@Nonnull BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
            if (level != null && pos != null && tintIndex != 0) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof IPaintableBlockEntity paintedBlockEntity) {
                    Block[] paints = paintedBlockEntity.getPaints();
                    for (Block paint : paints) {
                        if (paint == null)
                            continue;
                        BlockState paintState = paint.defaultBlockState();
                        int color = Minecraft.getInstance().getBlockColors().getColor(paintState, level, pos, tintIndex);
                        if (color != -1)
                            return color;
                    }
                }
            }
            return 0xFFFFFF;
        }

        @Override
        public int getColor(ItemStack itemStack, int tintIndex) {
            if (itemStack.getTag() != null && itemStack.getTag().contains("BlockEntityTag")) {
                CompoundTag blockEntityTag = itemStack.getTag().getCompound("BlockEntityTag");
                if (blockEntityTag.contains("paint")) {
                    Block paint = PaintUtils.getBlockFromRL(blockEntityTag.getString("paint"));
                    if (paint == null)
                        return 0;
                    return Minecraft.getInstance().getItemColors().getColor(paint.asItem().getDefaultInstance(), tintIndex);
                }
            }
            return 0;
        }
    }

    private static class WrappedModelLoader implements IGeometryLoader<Geometry> {

        private static final ItemTransforms.Deserializer INSTANCE = new ItemTransforms.Deserializer();

        private final boolean isDouble;

        WrappedModelLoader(boolean isDouble) {
            this.isDouble = isDouble;
        }

        @Nonnull
        @Override
        public Geometry read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
            return new Geometry(PaintUtils.getBlockFromRL(modelContents.get("reference").getAsString()),
                getItemTransforms(deserializationContext, modelContents), isDouble, getItemTextureRotation(modelContents));
        }

        private static ItemTransforms getItemTransforms(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            if (modelContents.has("display"))
                return new DefaultItemTransforms(INSTANCE.deserialize(modelContents.get("display"), null, deserializationContext));
            return new DefaultItemTransforms();
        }

        @Nullable
        private static Direction getItemTextureRotation(JsonObject modelContents) {
            if (modelContents.has("item_texture_rotation")) {
                return Arrays.stream(Direction.values())
                    .filter(dir -> dir.getName().equals(modelContents.get("item_texture_rotation").getAsString()))
                    .findFirst()
                    .orElse(null);
            }
            return null;
        }
    }

    private static class Geometry implements IUnbakedGeometry<Geometry> {

        private final Block shape;
        private final ItemTransforms transforms;
        private final boolean isDouble;
        @Nullable
        private final Direction itemTextureRotation;

        private Geometry(Block shape, ItemTransforms itemTransforms, boolean isDouble, @Nullable Direction itemTextureRotation) {
            this.shape = shape;
            this.transforms = itemTransforms;
            this.isDouble = isDouble;
            this.itemTextureRotation = itemTextureRotation;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
            ItemOverrides overrides, ResourceLocation modelLocation) {
            return isDouble ? new PaintedSlabModel(shape, transforms, itemTextureRotation) : new PaintedSimpleModel(shape, transforms, itemTextureRotation);
        }

        @Override
        public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter,
            Set<Pair<String, String>> missingTextureErrors) {
            return Collections.singletonList(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("minecraft", "missingno")));
        }
    }

    @SuppressWarnings("deprecation")
    private static class DefaultItemTransforms extends ItemTransforms {

        private static final ItemTransforms DEFAULT_TRANSFORMS = new ItemTransforms(
            new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 2.5f / 16, 0), new Vector3f(0.375f, 0.375f, 0.375f)),
            new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 2.5f / 16, 0), new Vector3f(0.375f, 0.375f, 0.375f)),
            new ItemTransform(new Vector3f(0, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f)),
            new ItemTransform(new Vector3f(0, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f)),
            new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)),
            new ItemTransform(new Vector3f(30, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.625f, 0.625f, 0.625f)),
            new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 3f / 16, 0), new Vector3f(0.25f, 0.25f, 0.25f)),
            new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f)));

        public DefaultItemTransforms(ItemTransforms pTransforms) {
            super(pTransforms);
        }

        public DefaultItemTransforms() {
            this(DEFAULT_TRANSFORMS);
        }

        @Nonnull
        @Override
        public ItemTransform getTransform(@Nonnull TransformType pType) {
            ItemTransform transform = super.getTransform(pType);
            if (transform == ItemTransform.NO_TRANSFORM) {
                return DEFAULT_TRANSFORMS.getTransform(pType);
            }
            return transform;
        }
    }
}
