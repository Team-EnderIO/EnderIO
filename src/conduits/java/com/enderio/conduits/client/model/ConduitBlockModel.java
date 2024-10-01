package com.enderio.conduits.client.model;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.model.ConduitCoreModelModifier;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.client.model.PaintingQuadTransformer;
import com.enderio.conduits.client.model.conduit.modifier.ConduitCoreModelModifiers;
import com.enderio.conduits.common.Area;
import com.enderio.conduits.common.conduit.ConduitGraphObject;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.conduit.OffsetHelper;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.enderio.core.data.model.EIOModel;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.enderio.conduits.client.ConduitClientSetup.BOX;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_CONNECTION;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_CONNECTION_BOX;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_CONNECTOR;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_CORE;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_FACADE;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_IO_IN;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_IO_IN_OUT;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_IO_OUT;
import static com.enderio.conduits.client.ConduitClientSetup.CONDUIT_IO_REDSTONE;
import static com.enderio.conduits.client.ConduitClientSetup.modelOf;

public class ConduitBlockModel implements IDynamicBakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData,
        @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>();
        ConduitBundle conduitBundle = extraData.get(ConduitBlockEntity.BUNDLE_MODEL_PROPERTY);
        BlockPos pos = extraData.get(ConduitBlockEntity.POS);

        if (conduitBundle != null && pos != null) {
            Direction.Axis axis = OffsetHelper.findMainAxis(conduitBundle);
            Map<ConduitType<?>, List<Vec3i>> offsets = new HashMap<>();

            for (Direction direction : Direction.values()) {
                boolean isEnd = conduitBundle.isConnectionEnd(direction);
                Direction preRotation = rotateDirection(direction, side);
                IQuadTransformer rotation = QuadTransformers.applying(rotateTransformation(direction));

                if (isEnd) {
                    quads.addAll(rotation.process(modelOf(CONDUIT_CONNECTOR).getQuads(state, preRotation, rand, extraData, renderType)));
                }

                var connectedTypes = conduitBundle.getConnectedTypes(direction);
                for (int i = 0; i < connectedTypes.size(); i++) {
                    ConduitType<?> type = connectedTypes.get(i);
                    ConduitGraphObject<?> node = conduitBundle.getNodeFor(type);
                    ConduitData<?> data = node.getConduitData();

                    Vec3i offset = OffsetHelper.translationFor(direction.getAxis(), OffsetHelper.offsetConduit(i, connectedTypes.size()));
                    offsets.computeIfAbsent(type, ignored -> new ArrayList<>()).add(offset);
                    IQuadTransformer rotationTranslation = rotation.andThen(QuadTransformers.applying(translateTransformation(offset)));
                    quads.addAll(new ConduitTextureEmissiveQuadTransformer(sprite(conduitBundle, type), 0)
                        .andThen(rotationTranslation)
                        .process(modelOf(CONDUIT_CONNECTION).getQuads(state, preRotation, rand, extraData, renderType)));

                    var conduitCoreModifier = ConduitCoreModelModifiers.getModifier(type);
                    if (conduitCoreModifier != null) {
                        quads.addAll(rotationTranslation.process(conduitCoreModifier.createConnectionQuads(data.cast(), side, direction, rand, renderType)));
                    }

                    if (isEnd) {
                        quads.addAll(rotationTranslation.process(modelOf(CONDUIT_CONNECTION_BOX).getQuads(state, preRotation, rand, extraData, renderType)));

                        ConnectionState connectionState = conduitBundle.getConnectionState(direction, type);
                        if (connectionState instanceof DynamicConnectionState dyn) {
                            IQuadTransformer color = rotationTranslation.andThen(new ColorQuadTransformer(dyn.insertChannel(), dyn.extractChannel()));
                            BakedModel model = null;
                            if (dyn.isExtract() && dyn.isInsert()) {
                                model = modelOf(CONDUIT_IO_IN_OUT);
                            } else if (dyn.isInsert()) {
                                model = modelOf(CONDUIT_IO_IN);
                            } else if (dyn.isExtract()) {
                                model = modelOf(CONDUIT_IO_OUT);
                            }

                            if (model != null) {
                                quads.addAll(color.process(model.getQuads(state, preRotation, rand, extraData, renderType)));
                            }

                            if (dyn.control() == RedstoneControl.ACTIVE_WITH_SIGNAL || dyn.control() == RedstoneControl.ACTIVE_WITHOUT_SIGNAL) {
                                quads.addAll(rotationTranslation
                                    .andThen(new ColorQuadTransformer(null, dyn.redstoneChannel()))
                                    .process(modelOf(CONDUIT_IO_REDSTONE).getQuads(state, preRotation, rand, extraData, renderType)));
                            }
                        }
                    }
                }

                Optional<BlockState> facadeOpt = conduitBundle.getFacade(direction);
                if (facadeOpt.isPresent()) {
                    BlockState facade = facadeOpt.get();
                    BakedModel facadeModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(facade);
                    List<BakedQuad> textureQuads = facadeModel.getQuads(state, preRotation, rand, ModelData.EMPTY, renderType);

                    quads.addAll(rotation
                        .andThen(new BlockColorQuadDataTransformer(pos, Minecraft.getInstance().level, facade))
                        .andThen(new PaintingQuadTransformer(facade, renderType))
                        .process(modelOf(CONDUIT_FACADE).getQuads(state, preRotation, rand, ModelData.EMPTY, renderType)));
                }
            }

            var allTypes = conduitBundle.getTypes();
            @Nullable Area box = null;
            Map<ConduitType<?>, Integer> notRendered = new HashMap<>();
            List<ConduitType<?>> rendered = new ArrayList<>();
            for (int i = 0; i < allTypes.size(); i++) {
                var type = allTypes.get(i);
                @Nullable List<Vec3i> offsetsForType = offsets.get(type);
                if (offsetsForType != null) {
                    //all are pointing to the same xyz reference meaning that we can draw the core
                    if (offsetsForType.stream().distinct().count() == 1) {
                        rendered.add(type);
                    } else {
                        if (box == null) {
                            box = new Area(offsetsForType.toArray(new Vec3i[0]));
                        } else {
                            offsetsForType.forEach(box::makeContain);
                        }
                    }
                } else {
                    notRendered.put(type, i);
                }
            }

            Set<Vec3i> duplicateFinder = new HashSet<>();
            //rendered have only one distinct pos, so I can safely assume get(0) is valid
            List<Vec3i> duplicatePositions = rendered.stream().map(offsets::get).map(l -> l.get(0)).filter(n -> !duplicateFinder.add(n)).toList();
            for (Vec3i duplicatePosition : duplicatePositions) {
                if (box == null) {
                    box = new Area(duplicatePosition);
                } else {
                    box.makeContain(duplicatePosition);
                }
            }
            for (ConduitType<?> toRender : rendered) {
                List<Vec3i> offsetsForType = offsets.get(toRender);
                if (box == null || !box.contains(offsetsForType.get(0))) {
                    quads.addAll(new ConduitTextureEmissiveQuadTransformer(sprite(conduitBundle, toRender), 0)
                        .andThen(QuadTransformers.applying(translateTransformation(offsetsForType.get(0))))
                        .process(modelOf(CONDUIT_CORE).getQuads(state, side, rand, extraData, renderType)));
                }
            }

            if (box != null) {
                for (Map.Entry<ConduitType<?>, Integer> notRenderedEntry : notRendered.entrySet()) {
                    Vec3i offset = OffsetHelper.translationFor(axis, OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size()));
                    if (!box.contains(offset)) {
                        quads.addAll(new ConduitTextureEmissiveQuadTransformer(
                            sprite(conduitBundle, notRenderedEntry.getKey()), 0)
                            .andThen(QuadTransformers.applying(translateTransformation(offset)))
                            .process(modelOf(CONDUIT_CORE).getQuads(state, side, rand, extraData, renderType)));
                    }
                }

                quads.addAll(new BoxTextureQuadTransformer(box.size())
                    .andThen(QuadTransformers.applying(translateTransformation(box.getMin())))
                    .process(modelOf(BOX).getQuads(state, side, rand, extraData, renderType)));
            } else {
                for (Map.Entry<ConduitType<?>, Integer> notRenderedEntry : notRendered.entrySet()) {
                    quads.addAll(new ConduitTextureEmissiveQuadTransformer(
                        sprite(conduitBundle, notRenderedEntry.getKey()), 0)
                        .andThen(QuadTransformers.applying(translateTransformation(
                            OffsetHelper.translationFor(axis, OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size())))))
                        .process(modelOf(CONDUIT_CORE).getQuads(state, side, rand, extraData, renderType)));
                }
            }
        }

        return quads;
    }

    /**
     * @param toDirection the Direction you want to transform to from the Bottom as base
     * @param toTransform the Direction to follow the same Transformation as bottom -> toDirection
     * @return the Direction toTransform was transformed to
     */
    @Nullable
    public static Direction rotateDirection(Direction toDirection, @Nullable Direction toTransform) {
        if (toTransform == null) {
            return null;
        }

        return switch (toDirection) {
            case DOWN -> toTransform;
            case UP -> toTransform.getClockWise(Direction.Axis.Z).getClockWise(Direction.Axis.Z);
            case NORTH -> toTransform.getCounterClockWise(Direction.Axis.X);
            case SOUTH -> toTransform.getClockWise(Direction.Axis.X);
            case WEST -> toTransform.getCounterClockWise(Direction.Axis.Z);
            case EAST -> toTransform.getClockWise(Direction.Axis.Z);
        };
    }

    public static Transformation rotateTransformation(Direction toDirection) {
        Quaternionf quaternion = new Quaternionf();
        switch (toDirection) {
        case UP -> quaternion.mul(Axis.ZP.rotationDegrees(180));
        case NORTH -> quaternion.mul(Axis.XP.rotationDegrees(90));
        case SOUTH -> quaternion.mul(Axis.XN.rotationDegrees(90));
        case WEST -> quaternion.mul(Axis.ZN.rotationDegrees(90));
        case EAST -> quaternion.mul(Axis.ZP.rotationDegrees(90));
        default -> {}
        }
        Transformation transformation = new Transformation(null, quaternion, null, null);
        return transformation.applyOrigin(new Vector3f(.5f, .5f, .5f));
    }

    private static Transformation translateTransformation(Vec3i offset) {
        return new Transformation(scale(offset, 3 / 16f), null, null, null);
    }

    private static Vector3f scale(Vec3i vector, float scaler) {
        return new Vector3f(vector.getX() * scaler, vector.getY() * scaler, vector.getZ() * scaler);
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
        return EIOModel.getMissingTexture();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        ConduitBundle conduitBundle = data.get(ConduitBlockEntity.BUNDLE_MODEL_PROPERTY); //default particle
        if (conduitBundle == null) {
            return EIOModel.getMissingTexture();
        }
        return sprite(conduitBundle, conduitBundle.getTypes().get(0));
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.cutout());
    }

    private static <T extends ConduitData<T>> TextureAtlasSprite sprite(ConduitBundle conduitBundle, ConduitType<T> type) {
        T data = conduitBundle.getNodeFor(type).getConduitData();

        ResourceLocation textureLocation = null;

        ConduitCoreModelModifier<T> conduitCoreModifier = ConduitCoreModelModifiers.getModifier(type);
        if (conduitCoreModifier != null) {
            textureLocation = conduitCoreModifier.getSpriteLocation(data);
        }

        // Default to a standard location
        if (textureLocation == null) {
            var conduitTypeKey = Objects.requireNonNull(EIOConduitTypes.REGISTRY.get().getKey(type));
            textureLocation = new ResourceLocation(conduitTypeKey.getNamespace(), "block/conduit/" + conduitTypeKey.getPath());
        }

        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(textureLocation);
    }

    private static boolean isMissingModel(BakedModel model) {
        return model == Minecraft.getInstance().getModelManager().getMissingModel();
    }
}
